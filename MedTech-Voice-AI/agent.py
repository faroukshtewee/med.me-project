from __future__ import annotations

import asyncio
from datetime import datetime
import logging

import pytz
from livekit.agents import (
    AutoSubscribe,
    JobContext,
    WorkerOptions,
    cli,
    Agent,
    AgentSession,
)
from livekit.plugins import silero
from livekit.plugins import deepgram
from livekit.plugins import groq
from livekit.plugins import cartesia
from dotenv import load_dotenv
from api import submit_appointment, cancel_existing_appointment, reschedule_appointment, finalize_confirmation, \
    unreservation, finalize_reschedule, abort_reschedule
from prompts import WELCOME_MESSAGE, INSTRUCTIONS
import time
import re
from collections import defaultdict
import asyncio
from livekit.agents import llm
from livekit import rtc
TIME_ZONE = "Asia/Jerusalem"


load_dotenv()

logger = logging.getLogger("agent")

user_message_counts = defaultdict(list)
MAX_MESSAGES_PER_MINUTE = 20
MAX_MESSAGE_LENGTH = 1000


class SecurityMiddleware(llm.LLM):
    def __init__(self, inner_llm: llm.LLM, room_ctx):
        super().__init__()
        self._inner_llm = inner_llm
        self._room_ctx = room_ctx

    def chat(self, *args, chat_ctx: llm.ChatContext, **kwargs):
        messages = getattr(chat_ctx, 'messages', [])
        user_id = self._room_ctx.local_participant.identity
        if messages:
            message = messages[-1].content.lower()
            if not check_rate_limit(user_id):
                return self._create_blocked_response("Too many requests. Please slow down.")

            if len(message) > MAX_MESSAGE_LENGTH:
                logger.warning(f"⚠️ Message too long blocked: {len(message)} chars")
                return self._create_blocked_response("Your message is too long for me to process. Please be more brief.")
            if contains_prompt_injection(message):
                logger.warning(f"🛡️ Middleware BLOCKED: {message}")
                asyncio.create_task(
                     self._room_ctx.local_participant.publish_data("SECURITY_ALERT: PROMPT_INJECTION")
                )
                return self._create_blocked_response("I cannot perform that action due to security restrictions. "
                    "I can only help you schedule, reschedule, or cancel medical appointments.")

        return self._inner_llm.chat(*args,chat_ctx=chat_ctx, **kwargs)

    def _create_blocked_response(self, text: str):
        async def gen():
            yield llm.ChatChunk(choices=[llm.Choices(delta=llm.ChoiceDelta(content=text, role="assistant"))])

        return llm.LLMStream(gen())


def check_rate_limit(user_id: str) -> bool:
    now = time.time()
    # clean messages older than 1 min
    user_message_counts[user_id] = [t for t in user_message_counts[user_id] if now - t < 60]
    if len(user_message_counts[user_id]) >= MAX_MESSAGES_PER_MINUTE:
        return False
    user_message_counts[user_id].append(now)
    return True


def contains_prompt_injection(message: str) -> bool:
    lower = message.lower()

    basic_patterns = [
        "admin access", "drop table", "reveal your secret",
        "delete all appointments", "ignore previous", "disregard all",
        "system prompt", "you are now", "forget everything",
        "ignore all previous instructions"
    ]

    advanced_patterns = [
        "system override", "developer mode", "admin mode",
        "bypass security", "disable filter", "unrestricted mode",
        "show configuration", "reveal instructions", "system access",
        "root access", "sudo ", "admin panel", "database query",
        "internal prompt", "hidden instructions", "override policy",
        "security off", "ignore restrictions", "forget your role",
        "act as if", "pretend you are", "roleplay as",
        "insert into", "update ", "delete from", "truncate table",
        "exec(", "eval(", "system(", "__import__",
        "disregard your", "forget your training", "ignore your programming",
        "free from all restrictions", "game where you are free"
    ]

    sql_patterns = [
        "'; drop", "1' or '1'='1", "union select", "-- ", "/*", "*/",
        "xp_cmdshell", "exec sp_", "execute immediate"
    ]

    all_patterns = basic_patterns + advanced_patterns + sql_patterns

    for pattern in all_patterns:
        if pattern in lower:
            return True

    # Check for obfuscation attempts
    obfuscated_checks = [
        re.sub(r'[^a-z]', '', lower),  # Remove all non-letters
        lower.replace('0', 'o').replace('1', 'i').replace('3', 'e').replace('4', 'a').replace('5', 's'),
        lower.replace(' ', '').replace('-', '').replace('_', '')
    ]
    for obfuscated in obfuscated_checks:
        for pattern in ["ignoreprevious", "systemprompt", "adminaccess", "droptable",
                       "ignoreall", "forgetyour", "freefrom", "allrestrictions"]:
            if pattern in obfuscated:
                return True
    no_spaces = lower.replace(' ', '')
    if any(p in no_spaces for p in ["ignoreprevious", "ignoreall", "systemprompt",
                                    "adminaccess", "freefrom"]):
        return True
        # Check for patterns split by dashes/underscores
    no_separators = lower.replace('-', '').replace('_', '').replace(' ', '')
    if any(p in no_separators for p in ["ignoreprevious", "ignoreall", "droptable"]):
        return True

    return False


def validate_message_length(message: str) -> bool:
    return len(message) <= MAX_MESSAGE_LENGTH


async def entrypoint(ctx: JobContext):
    logger.info(f"Connecting to room {ctx.room.name}...")
    await ctx.connect(auto_subscribe=AutoSubscribe.AUDIO_ONLY)
    last_speech_timestamp = time.time()
    await ctx.wait_for_participant()

    raw_llm = groq.LLM(model="openai/gpt-oss-120b", temperature=0.7)

    secure_llm = SecurityMiddleware(raw_llm, ctx.room)
    now_date = datetime.now(pytz.timezone(TIME_ZONE))
    current_date_str = now_date.strftime("%A, %d/%m/%Y")

    # add this settings in the beginning of Instructions in prompts
    DYNAMIC_INFO = f"\n[CURRENT SYSTEM DATE: {current_date_str}]\n"
    full_instructions = DYNAMIC_INFO + INSTRUCTIONS
    agent = Agent(
        instructions=INSTRUCTIONS,
        llm=secure_llm,
        tools=[submit_appointment, cancel_existing_appointment, reschedule_appointment, finalize_confirmation, finalize_reschedule , unreservation, abort_reschedule]
    )

    session = AgentSession(
        vad=silero.VAD.load(),
        stt=deepgram.STT(),
        llm=secure_llm,
        tts=cartesia.TTS(),
    )

    @session.on("user_started_speaking")
    def on_user_started_speaking():
        nonlocal last_speech_timestamp
        last_speech_timestamp = time.time()
        logger.debug("🎤 User started speaking")

    @session.on("user_speech_committed")
    def on_user_speech_committed(message: str):
        nonlocal last_speech_timestamp
        last_speech_timestamp = time.time()
        logger.info(f"💬 User (voice): {message[:100]}...")

    @session.on("agent_started_speaking")
    def on_agent_started_speaking():
        nonlocal last_speech_timestamp
        last_speech_timestamp = time.time()
        logger.debug("🤖 Agent started speaking")

    @session.on("agent_speech_committed")
    def on_agent_speech_committed(message: str):
        logger.info(f"🤖 Agent said: {message[:100]}...")

    @ctx.room.on("participant_disconnected")
    def on_participant_disconnected(participant: rtc.RemoteParticipant):
        user_id = participant.identity
        if user_id in user_message_counts:
            del user_message_counts[user_id]
        logger.info(f"👋 {user_id} disconnected")

    await session.start(agent=agent, room=ctx.room)
    await session.generate_reply(instructions=WELCOME_MESSAGE)
    logger.info("Agent is now active and waiting for user...")


if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint))
