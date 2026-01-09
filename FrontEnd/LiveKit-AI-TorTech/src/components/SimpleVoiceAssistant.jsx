import {
  useVoiceAssistant,
  BarVisualizer,
  VoiceAssistantControlBar,
  useTrackTranscription,
  useLocalParticipant,
  useRoomContext,
} from "@livekit/components-react";
import { Track } from "livekit-client";
import { useEffect, useState, useRef  } from "react";
import PropTypes from 'prop-types';
import "./SimpleVoiceAssistant.css";
const Message = ({ type, text }) => (
  <div className={`message${type === "security" ? "message-security" : ""}`}>
    <strong className={`message-${type}`}>
      {type === "agent" && "Agent: "}
      {type === "user" && "You: "}
      {type === "security" && "🛡️ Security Alert: "}    
    </strong>
    <span className="message-text">{text}</span>
  </div>
);

Message.propTypes = {
  type: PropTypes.string.isRequired,
  text: PropTypes.string.isRequired,
};

const SimpleVoiceAssistant = () => {
  const { state, audioTrack, agentTranscriptions } = useVoiceAssistant();
  const { localParticipant } = useLocalParticipant();
  const room = useRoomContext();
  const [securityWarning, setSecurityWarning] = useState(null);
  const conversationRef = useRef(null);

  const microphonePublication = localParticipant?.getTrackPublication(Track.Source.Microphone);
  const { segments: userTranscriptions } = useTrackTranscription({
    publication: microphonePublication,
    source: Track.Source.Microphone,
    participant: localParticipant,
  });


  const voiceMessages = [
    ...(agentTranscriptions?.map((t) => ({
      id: `agent-voice-${t.firstReceivedTime}`,
      text: t.text,
      type: "agent",
      firstReceivedTime: t.firstReceivedTime,
    })) || []),
    ...(userTranscriptions?.map((t) => ({
      id: `user-voice-${t.firstReceivedTime}`,
      text: t.text,
      type: "user",
      firstReceivedTime: t.firstReceivedTime,
    })) || []),
  ].sort((a, b) => (a.firstReceivedTime || 0) - (b.firstReceivedTime || 0));

  useEffect(() => {
    if (!room) return;
    const handleData = (payload) => {
      const decoder = new TextDecoder();
      const message = decoder.decode(payload);
      if (message.includes("SECURITY_ALERT")) {
        setSecurityWarning("🚨 Security Violation Detected - Action Blocked!");
        setTimeout(() => setSecurityWarning(null), 5000);
      }
    };
    room.on("dataReceived", handleData);
    return () => room.off("dataReceived", handleData);
  }, [room]);

  // Auto-scroll
  useEffect(() => {
    if (conversationRef.current) {
      conversationRef.current.scrollTop = conversationRef.current.scrollHeight;
    }
  }, [voiceMessages]);

  return (
    <div className="voice-assistant-container">
      {securityWarning && <div className="security-banner">{securityWarning}</div>}
      
      <div className="visualizer-container">
        {audioTrack ? (
          <BarVisualizer state={state} barCount={7} trackRef={audioTrack} />
        ) : (
          <div className="status">Initializing Audio...</div>
        )}
      </div>

      <div className="control-section">
        <VoiceAssistantControlBar />
        
        <div className="conversation" ref={conversationRef}>
          {voiceMessages.length === 0 ? (
            <div className="status" style={{color: '#999', textAlign: 'center', padding: '20px'}}>
               🎙️ Start speaking to see the transcription...
            </div>
          ) : (
            voiceMessages.map((msg) => (
              <Message key={msg.id} type={msg.type} text={msg.text} />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default SimpleVoiceAssistant;