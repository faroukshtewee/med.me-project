from livekit.agents.llm import function_tool
import logging
import httpx
from typing import List, Dict, Any
import datetime
import pytz
import json
from datetime import datetime
from fastapi.middleware.cors import CORSMiddleware
from livekit import api as lk_api
import os
from fastapi import FastAPI, Query, HTTPException
from dotenv import load_dotenv
import uuid
from datetime import datetime, timedelta
import pytz
import re
from typing import Tuple, Optional

logger = logging.getLogger("api-log")
logger.setLevel(logging.INFO)
load_dotenv()

BASE_URL = "http://localhost:8080/medme/api/appointment"
HTTP_CLIENT = httpx.AsyncClient()
SYSTEM_ID = 1
SYSTEM_TOKEN = "e0197978f235b1fbe2ecc386af12ddf5c1594219"
SYSTEM_USER = "67e16c3d5ce3f65a969705a0"
DEFAULT_NETWORK_ID = 456
TIME_ZONE = "Asia/Jerusalem"



app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    # allow_origins=["http://0.0.0.0:5173"],
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/getToken")
async def get_token(identity: str = Query(...)):
    try:
        api_key = os.getenv("LIVEKIT_API_KEY")
        api_secret = os.getenv("LIVEKIT_API_SECRET")
        unique_room = f"room-{str(uuid.uuid4())[:8]}"
        if not api_key or not api_secret:
            print("Error: LIVEKIT_API_KEY or SECRET missing in .env")
            raise HTTPException(status_code=500, detail="Server configuration error")
        token = lk_api.AccessToken(api_key, api_secret) \
            .with_identity(identity) \
            .with_grants(lk_api.VideoGrants(
            room_join=True,
            room=unique_room,
            can_publish=True,
            can_subscribe=True
        ))
        print(f"Token generated for user: {identity} in room: {unique_room}")
        return {"token": token.to_jwt()}
    except Exception as e:
        print(f"Critical Error in getToken: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


def parse_natural_date(date_input: str, timezone_str: str = "Asia/Jerusalem") -> Optional[str]:
    tz = pytz.timezone(timezone_str)
    now = datetime.now(tz)
    date_input = date_input.lower().strip()

    if "today" in date_input:
        return now.strftime("%Y-%m-%d")
    if "tomorrow" in date_input:
        return (now + timedelta(days=1)).strftime("%Y-%m-%d")

    if "next month" in date_input:
        try:
            return (now + timedelta(days=30)).strftime("%Y-%m-%d")
        except:
            return None

    if "next week" in date_input:
        return (now + timedelta(days=7)).strftime("%Y-%m-%d")

    weekdays = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"]
    for i, day in enumerate(weekdays):
        if day in date_input:
            days_ahead = (i - now.weekday() + 7) % 7
            if days_ahead == 0: days_ahead = 7
            return (now + timedelta(days=days_ahead)).strftime("%Y-%m-%d")

    try:
        match = re.search(r'(\d{1,2})[/-](\d{1,2})(?:[/-](\d{2,4}))?', date_input)
        if match:
            day, month, year = match.groups()
            year = year if year else str(now.year)
            if len(year) == 2: year = "20" + year
            parsed = datetime(int(year), int(month), int(day))
            return parsed.strftime("%Y-%m-%d")
    except:
        pass

    return None


def convert_to_iso_utc(date_str: str, time_str: str, input_timezone: str) -> Tuple[str, Optional[str]]:
    """
    Converts natural date/time to ISO UTC with better error handling.
    Returns: (iso_utc_string, error_message)
    """
    try:
        # Parse date
        date_parsed = parse_natural_date(date_str, input_timezone)
        if not date_parsed:
            return "", f"I couldn't understand the date '{date_str}'. Please use DD/MM/YYYY format or say 'tomorrow'."

        # Parse time (handle various formats)
        time_clean = time_str.strip().upper()

        # Handle 12-hour format (e.g., "2:30 PM")
        if "AM" in time_clean or "PM" in time_clean:
            try:
                time_obj = datetime.strptime(time_clean, "%I:%M %p")
                time_24h = time_obj.strftime("%H:%M")
            except ValueError:
                try:
                    time_obj = datetime.strptime(time_clean, "%I %p")
                    time_24h = time_obj.strftime("%H:%M")
                except ValueError:
                    return "", f"I couldn't understand the time '{time_str}'. Please use HH:MM format."
        else:
            # Assume 24-hour format
            if not re.match(r'\d{1,2}:\d{2}', time_str):
                return "", f"Please provide time in HH:MM format (e.g., '14:30' or '2:30 PM')."
            time_24h = time_str

        # Combine and convert to UTC
        datetime_str = f"{date_parsed} {time_24h}"
        local_dt = datetime.strptime(datetime_str, "%Y-%m-%d %H:%M")

        tz = pytz.timezone(input_timezone)
        localized_dt = tz.localize(local_dt)
        utc_dt = localized_dt.astimezone(pytz.utc)

        return local_dt.strftime("%Y-%m-%dT%H:%M:%S.000Z"), None

    except Exception as e:
        return "", f"Date/time conversion error: {str(e)}"


async def _post_form_data(endpoint: str, data: Dict[str, Any]) -> Any:
    url = f"{BASE_URL}/{endpoint}"
    payload = {
        "id": SYSTEM_ID,
        "token": SYSTEM_TOKEN,
        "user": SYSTEM_USER,
        "skip": True,
        "workerSort": "workload",
        "networkId": DEFAULT_NETWORK_ID,
        **data
    }

    async with httpx.AsyncClient() as client:
        try:
            response = await client.post(url, data=payload, timeout=15.0)
            response.raise_for_status()

            try:
                return response.json()
            except (json.JSONDecodeError, ValueError):
                return response.text

        except httpx.HTTPStatusError as e:
            msg = f"API Error {e.response.status_code}: {e.response.text}"
            logger.error(msg)
            raise Exception(msg)
        except Exception as e:
            logger.error(f"Connection failed: {e}")
            raise Exception(f"Backend unreachable: {e}")


async def _get_data(endpoint: str, params: Dict[str, Any]) -> Any:
    url = f"{BASE_URL}/{endpoint}"

    query_params = {
        **params
    }

    async with httpx.AsyncClient() as client:
        try:
            response = await client.get(url, params=query_params, timeout=15.0)
            response.raise_for_status()

            try:
                return response.json()
            except (json.JSONDecodeError, ValueError):
                return response.text
        except Exception as e:
            logger.error(f"GET Request failed: {e}")
            raise Exception(f"Backend unreachable: {e}")


async def search_business_id_by_name(department: str) -> str:
    try:
        list_businesses = await _post_form_data("search-business-id-by-name", {
            "department": department,
        })

        if not list_businesses:
            return f"I couldn't find a department matching '{department}'."

        if len(list_businesses) == 1:
            return str(list_businesses[0]["businessId"])

        options = f"I found several departments matching '{department}':\n"
        for i, b in enumerate(list_businesses, 1):
            options += f"{i}. {b['businessName']}\n"
        options += "Which one were you looking for?"

        return options

    except Exception as e:
        return f"Error searching for department: {e}"


async def get_business_taxonomies(business_id: str) -> List[Dict[str, Any]]:
    data = {
        "businessId": business_id,
    }
    result = await _post_form_data("get-business-taxonomies", data)
    return result if isinstance(result, list) else []


async def search_taxonomy_id_by_name(taxonomy_query: str, business_id: str) -> str:
    try:
        list_taxonomies = await get_business_taxonomies(business_id)
        query = taxonomy_query.lower().strip()
        matches = []
        all_names = []

        for resource_map in list_taxonomies:
            for tax_id, value_obj in resource_map.items():
                if isinstance(value_obj, dict):
                    alias = str(value_obj.get("alias", "")).lower()
                    all_names.append(value_obj.get("alias"))

                    if query in alias:
                        matches.append({"id": tax_id, "name": value_obj.get("alias")})

        if not matches:
            options = "\n".join([f"{i+1}. {name}" for i, name in enumerate(all_names) if name])
            return f"SUGGESTION_LIST: I couldn't find '{taxonomy_query}'. Here are the available services:\n{options}"

        if len(matches) == 1:
            return str(matches[0]["id"])

        options = f"I found several matches for '{taxonomy_query}':\n"
        for i, m in enumerate(matches, 1):
            options += f"{i}. {m['name']}\n"
        return f"SUGGESTION_LIST: {options}"

    except Exception as e:
        return f"Error: {e}"


async def search_resource_id_by_name(resource_name: str, business_id: str) -> str:
    try:
        data = {
            "resourceName": resource_name,
            "businessId": business_id,
            "skip": False,
            "workerSort": "workload"
        }
        list_resources = await _post_form_data("search-resource-id-by-name", data)

        if not list_resources:
            return f"I couldn't find anyone matching '{resource_name}'."

        if len(list_resources) == 1:
            return str(list_resources[0]["resourceId"])

        options = f"I found several options for '{resource_name}':\n"
        for i, m in enumerate(list_resources, 1):
            options += f"{i}. {m['resourceName']}\n"
        options += "Which one would you like to choose?"

        return options

    except Exception as e:
        logger.error(f"Error searching resource: {e}")
        return f"Error: {e}"


async def get_resource_available_time_slots(business_id: str, timezone: str, resources_id: str,taxonomies: str, date_from: str, date_to: str) -> List[Dict[str, Any]]:
    data = {
        "businessId": business_id,
        "timezone": TIME_ZONE,
        "resourcesId": resources_id,
        "taxonomies": taxonomies,
        "dateFrom": date_from,
        "dateTo": date_to
    }
    return await _post_form_data("get-resource-available-time-slots", data)


async def get_first_available_day(business_id: str, timezone: str, resources_id: List[str],taxonomies: str) -> List[Dict[str, str]]:
    data = {
        "businessId": business_id,
        "timezone": TIME_ZONE,
        "resourcesId": resources_id,
        "taxonomies": taxonomies
    }
    return await _post_form_data("get-resource-first-available-day", data)


async def reserve_appointment(start_datetime: str, business_id: str, taxonomies: str, resource_id: str, amount=0, currency="ILS") -> str:
    data = {
        "startDateTime": start_datetime,
        "amount": amount,
        "currency": currency,
        "businessId": business_id,
        "taxonomies": taxonomies,
        "client_appear": "NONE",
        "resourceId": resource_id
    }
    return await _post_form_data("reserve-appointment", data)


async def add_patient(business_id: str, name: str, surname: str, country_code: str, area_code: str, number: str, email: str) -> str:
    data = {
        "businessId": business_id,
        "name": name,
        "surname": surname,
        "country_code": country_code,
        "area_code": area_code,
        "number": number,
        "email": email
    }
    return await _post_form_data("add-patient", data)


async def confirm_appointment(appointment_id: str, client_id: str, datetime: str, businessId: str, taxonomyId: str, resourcesId: str) -> str:
    data = {
        "appointmentId": appointment_id,
        "clientId": client_id,
        "datetime": datetime,
        "businessId": businessId,
        "taxonomyId": taxonomyId,
        "resourcesId": resourcesId,
    }
    return await _post_form_data("confirm", data)


async def unreserve_appointment(appointment_id: str, business_id: str) -> str:
    try:
        result = await _post_form_data(
            endpoint="unreserve-appointment",
            data={
                "appointmentId": appointment_id,
                "businessId": business_id
            }
        )
        if isinstance(result, str) and result.lower() == "success":
            return "success"

        if isinstance(result, dict):
            if "error" in result:
                return f"ERROR: {result['error'].get('message', 'Unknown error')}"
            if result.get("result") is True:
                return "success"

        return str(result)
    except Exception as e:
        logger.error(f"Unreserve call failed: {e}")
        return f"ERROR: {str(e)}"


@function_tool(description="Finalize and confirm the reserved appointment.")
async def finalize_confirmation(appointment_id: str, client_id: str, start_iso: str, business_id: str, taxonomy_id: str,resource_id: str):
    try:
        confirm_message = await confirm_appointment(
            appointment_id, client_id, start_iso, business_id, taxonomy_id, resource_id
        )

        if "not confirmed" in confirm_message or "ERROR" in confirm_message:
            logger.error(f"Final confirmation failed: {confirm_message}. Triggering unreserve.")
            try:
                unreserve_result = await unreserve_appointment(appointment_id, business_id)
                logger.info(f"Unreserve result: {unreserve_result}")
            except Exception as e:
                logger.error(f"Failed to unreserve after confirmation error: {e}")
            return f"I'm sorry, the final confirmation failed: {confirm_message}. I've released the temporary hold on this slot."

        return "Excellent! Your appointment is now officially confirmed. We look forward to seeing you!"
    except Exception as e:
        return f"Error during final confirmation: {e}"


@function_tool(description="Finalize the rescheduling by confirming the new appointment and cancelling the old one.")
async def finalize_reschedule(old_date: str, old_time: str,new_date: str,new_time: str, new_app_id: str, client_id: str,business_id: str, taxonomy_id: str, resource_id: str, department: str, service_type: str,resource_name: str):
    try:
        date_obj = datetime.strptime(new_date, "%d/%m/%Y")
        new_start_iso = f"{date_obj.strftime('%Y-%m-%d')}T{new_time}:00"
        cancel_result = await cancel_existing_appointment(old_date, old_time, department, service_type, resource_name)

        if "ERROR" in cancel_result:
            logger.error(f"❌ Reschedule aborted: Failed to cancel old appointment: {cancel_result}")
            return (
                f"I couldn't cancel your original appointment, so I haven't moved it. "
                f"Please try again or contact the clinic. (Error: {cancel_result})"
            )

        status = await confirm_appointment(new_app_id, client_id, new_start_iso, business_id, taxonomy_id, resource_id)

        if "not confirmed" in status or "ERROR" in status:
            logger.error(f"Failed to confirm new appointment: {status}. Old appointment cancelled.")
            await unreserve_appointment(new_app_id, business_id)
            return f"I couldn't confirm the new time slot: {status}. Your original appointment is cancelled."

        logger.info(f"🎉 Reschedule complete: {new_app_id} confirmed, old slot freed.")
        return (
            f"Perfect! I've cancelled your previous appointment and successfully moved you to "
            f"**{new_date}** at **{new_time}**. Your new appointment is now confirmed."
        )

    except Exception as e:
        logger.error(f"Critical error during reschedule flow: {e}")
        return f"I encountered a technical error: {e}. No changes were made to your original appointment."


@function_tool(description="Cancel a temporary reservation if the user decides not to proceed.")
async def unreservation(appointment_id: str, business_id: str):
    try:
        result = await unreserve_appointment(appointment_id, business_id)
        return "No problem, I've released the held slot. Is there anything else I can help you with?"
    except Exception as e:
        return f"I tried to cancel the hold but encountered an error: {e}"


@function_tool(description="Abort rescheduling and release the new reserved slot.")
async def abort_reschedule(new_app_id: str, business_id: str):
    try:
        await unreserve_appointment(new_app_id, business_id)
        return "No changes were made. Your original appointment remains as it was."
    except Exception as e:
        return f"I tried to release the new slot but failed: {e}"


@function_tool(description="Submit appointment booking request with all required details, including department, service, resource, date (DD/MM/YYYY), time (HH:MM),and customer phone number, customer email address.")
async def submit_appointment(department: str, service_type: str, resource_name: str, date: str, time: str, customer_name: str, phoneNumber: str,email: str, timezone: str):
    logger.info(f"Starting booking for {service_type} with {resource_name} in {department} on {date} at {time}")

    name_parts = customer_name.split()
    surname = name_parts[-1] if len(name_parts) > 1 else ""
    first_name = " ".join(name_parts[:-1]) if len(name_parts) > 1 else customer_name

    COUNTRY_CODE = "972"
    AREA_CODE = phoneNumber[:3] if len(phoneNumber) >= 9 else "052"
    NUMBER = phoneNumber[3:] if len(phoneNumber) > 3 else phoneNumber
    EMAIL = f"{email}"

    try:

        start_datetime_iso_utc, error = convert_to_iso_utc(date,time,TIME_ZONE)
        start_datetime_iso_utc = start_datetime_iso_utc.strip().replace(",", "")
    except ValueError as e:
        return f"Error: {e}"

    try:
        business_result = await search_business_id_by_name(department)
        if any(keyword in str(business_result) for keyword in ["I found several", "Which one"]):
            logger.info("Multiple businesses found, forcing Agent to read the list.")
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {business_result}"
        business_id = business_result
        print(f'business_id-------------{business_id}')

        taxonomy_result = await search_taxonomy_id_by_name(service_type, business_id)
        if "SUGGESTION_LIST" in str(taxonomy_result):
            logger.info("Service not found, presenting list to user.")
            return f"I couldn't find the exact service. Please ask the user to choose from this list: {taxonomy_result}"
        taxonomy_id = taxonomy_result
        print(f'taxonomy_id-------------{taxonomy_id}')

        resource_result = await search_resource_id_by_name(resource_name, business_id)
        if any(keyword in str(resource_result) for keyword in ["I found several", "Which one"]):
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {resource_result}"
        resource_id = resource_result
        print(f'resource_id-------------{resource_id}')

        if not business_id or not taxonomy_id or not resource_id:
            raise Exception("One or more required IDs (Business, Service, or Worker) could not be found.")

    except Exception as e:
        return f"Booking failed during initial ID lookup. Please verify the department, service, and worker names. Error: {e}"

    is_exact_slot_available = False
    chosen_start_iso = ""
    alternative_slots_today = []
    try:
        availability = await get_resource_available_time_slots(
            business_id=business_id,
            timezone=TIME_ZONE,
            resources_id=resource_id,
            taxonomies=taxonomy_id,
            date_from=start_datetime_iso_utc,
            date_to=start_datetime_iso_utc
        )

        if availability and len(availability) > 0:
            user_h, user_m = map(int, time.split(':'))
            requested_minutes = (user_h * 60) + user_m

            first_entry = availability[0]
            for date_key, slots in first_entry.items():
                date_part = date_key.split('T')[0]
                for slot in slots:
                    start_min = int(slot.get('start'))
                    slot_time = f"{start_min // 60:02d}:{start_min % 60:02d}"
                    alternative_slots_today.append(slot_time)

                    if start_min == requested_minutes:
                        is_exact_slot_available = True
                        chosen_start_iso = f"{date_part}T{user_h:02d}:{user_m:02d}:00"
    except Exception as e:
        logger.warning(f"Initial availability check failed: {e}")

    if not is_exact_slot_available and alternative_slots_today:
        return (
            f"I'm sorry, {time} is not available on {date}. However, I found these other slots on the SAME day: "
            f"{', '.join(alternative_slots_today)}. Would you like one of these?"
        )
    if not is_exact_slot_available:
        first_available_slots = await get_first_available_day(
            business_id=business_id,
            timezone=TIME_ZONE,
            resources_id=[resource_id],
            taxonomies=taxonomy_id
        )

        if first_available_slots and first_available_slots[0].get('date') != "0001-01-01T00:00:00Z":
            suggested_date_iso = first_available_slots[0]['date']
            new_availability = await get_resource_available_time_slots(
                business_id=business_id,
                timezone=TIME_ZONE,
                resources_id=resource_id,
                taxonomies=taxonomy_id,
                date_from=suggested_date_iso,
                date_to=suggested_date_iso
            )

            if new_availability and len(new_availability) > 0:
                actual_slots = []
                date_str = list(new_availability[0].keys())[0]
                for s in new_availability[0][date_str]:
                    start_min = s.get('start')
                    actual_slots.append(f"{start_min // 60:02d}:{start_min % 60:02d}")

                return (
                    f"I'm sorry, that specific time is unavailable. However, I found openings "
                    f"on **{date_str.split('T')[0]}**. Available times: **{', '.join(actual_slots)}**. "
                    f"Which one would you prefer?"
                )
        return "I couldn't find any available slots. Please try a different service."

    try:
        appointment_id = await reserve_appointment(
            start_datetime=chosen_start_iso,
            business_id=business_id,
            taxonomies=taxonomy_id,
            resource_id=resource_id
        )

        client_id = await add_patient(
            business_id=business_id,
            name=first_name,
            surname=surname,
            country_code=COUNTRY_CODE,
            area_code=AREA_CODE,
            number=NUMBER,
            email=EMAIL
        )

        return (
            f"SYSTEM MESSAGE: I have reserved the slot. "
            f"Details for user: {service_type} with {resource_name} on {date} at {time}. "
            f"Please ask the user: 'I've reserved that spot for you. Should I go ahead and confirm it?' "
            f"Metadata for follow-up (keep in context): appointment_id={appointment_id}, client_id={client_id}, "
            f"start_iso={chosen_start_iso}, business_id={business_id}, taxonomy_id={taxonomy_id}, resource_id={resource_id}"
        )

    except Exception as e:
        logger.error(f"Reservation failed: {e}")
        return f"I encountered an error while trying to hold that slot: {e}"


@function_tool(description="Cancel an existing appointment")
async def cancel_existing_appointment(date: str,time: str,department: str,service_type: str ,resource_name: str):
    try:
        business_result = await search_business_id_by_name(department)
        if any(keyword in str(business_result) for keyword in ["I found several", "Which one"]):
            logger.info("Multiple businesses found, forcing Agent to read the list.")
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {business_result}"
        business_id = business_result
        print(f'business_id cancel-------------{business_id}')

        taxonomy_result = await search_taxonomy_id_by_name(service_type, business_id)
        if "SUGGESTION_LIST" in str(taxonomy_result):
            logger.info("Service not found, presenting list to user.")
            return f"I couldn't find the exact service. Please ask the user to choose from this list: {taxonomy_result}"
        taxonomy_id = taxonomy_result
        print(f'taxonomy_id cancel-------------{taxonomy_id}')

        resource_result = await search_resource_id_by_name(resource_name, business_id)
        if any(keyword in str(resource_result) for keyword in ["I found several", "Which one"]):
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {resource_result}"
        resource_id = resource_result
        print(f'resource_id cancel-------------{resource_id}')

        if not business_id or not taxonomy_id or not resource_id:
            raise Exception("One or more required IDs (Business, Service, or Worker) could not be found.")

    except Exception as e:
        return f"Booking failed during initial ID lookup. Please verify the department, service, and worker names. Error: {e}"
    client_id = await get_client_id()
    appointment_id = await find_appointment_by_time(date, time, business_id, taxonomy_id, resource_id)
    if "couldn't find" in str(appointment_id) or "Error" in str(appointment_id):
        return f"I'm sorry, I couldn't find an appointment on {date} at {time} to cancel."

    data = {
        "appointmentId": appointment_id,
        "clientId": client_id,
        "businessId": business_id,
        "taxonomyId": taxonomy_id,
        "resourceId": resource_id,

    }

    try:
        result = await _post_form_data("cancel-appointment", data)

        return f"Your appointment on **{date}** at **{time}** has been successfully cancelled. 🗑️"
    except Exception as e:
        logger.error(f"Cancellation failed: {e}")
        return "I encountered a technical issue while trying to cancel your appointment. Please try again later."


@function_tool(description="Reschedule an existing appointment to a new date and time")
async def reschedule_appointment(old_date: str,old_time: str,new_date: str,new_time: str,department: str,service_type: str,resource_name: str,):
    try:
        business_result = await search_business_id_by_name(department)
        if any(keyword in str(business_result) for keyword in ["I found several", "Which one"]):
            logger.info("Multiple businesses found, forcing Agent to read the list.")
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {business_result}"
        business_id = business_result
        print(f'business_id reschedule-------------{business_id}')

        taxonomy_result = await search_taxonomy_id_by_name(service_type, business_id)
        if "SUGGESTION_LIST" in str(taxonomy_result):
            logger.info("Service not found, presenting list to user.")
            return f"I couldn't find the exact service. Please ask the user to choose from this list: {taxonomy_result}"
        taxonomy_id = taxonomy_result
        print(f'taxonomy_id reschedule-------------{taxonomy_id}')

        resource_result = await search_resource_id_by_name(resource_name, business_id)
        if any(keyword in str(resource_result) for keyword in ["I found several", "Which one"]):
            return f"SYSTEM MESSAGE: I found multiple options. Please read this EXACT list to the user and ask them to pick one: {resource_result}"
        resource_id = resource_result
        print(f'resource_id reschedule-------------{resource_id}')

        if not business_id or not taxonomy_id or not resource_id:
            raise Exception("One or more required IDs (Business, Service, or Worker) could not be found.")

    except Exception as e:
        return f"Booking failed during initial ID lookup. Please verify the department, service, and worker names. Error: {e}"
    business_id = await search_business_id_by_name(department)
    appointment_id = await find_appointment_by_time(old_date, old_time, business_id, taxonomy_id, resource_id)

    if "couldn't find" in str(appointment_id) or "Error" in str(appointment_id):
        return f"I'm sorry, I couldn't find an existing appointment on {old_date} at {old_time} to reschedule."
    print(f'reschedule_appointment appointment_id-------------{appointment_id}')

    try:
        client_id = await get_client_id()
        print(f'reschedule_appointment client_id-------------{client_id}')

    except Exception as e:
        logger.error(f"Failed to fetch client ID: {e}")
        return "I couldn't verify your account details to move the appointment."
    logger.info(f"Rescheduling ID {appointment_id} from {old_date} {old_time} to {new_date} {new_time}")

    new_start_iso = convert_to_iso_utc(new_date,new_time,TIME_ZONE)

    try:
        availability = await get_resource_available_time_slots(
            business_id=business_id,
            timezone=TIME_ZONE,
            resources_id=resource_id,
            taxonomies=taxonomy_id,
            date_from=new_start_iso,
            date_to=new_start_iso
        )

        is_available = False
        if availability and len(availability) > 0:
            user_h, user_m = map(int, new_time.split(':'))
            requested_minutes = (user_h * 60) + user_m

            first_entry = availability[0]
            for date_key, slots in first_entry.items():
                for slot in slots:
                    if int(slot.get('start')) == requested_minutes:
                        is_available = True
                        break
    except Exception as e:
        return f"Error checking availability: {e}"
    if not is_available:
        return f"I'm sorry, the new time **{new_time}** on **{new_date}** is not available. Would you like me to find the next available day?"
    try:
        date_obj = datetime.strptime(new_date, "%d/%m/%Y")
        new_start_iso = f"{date_obj.strftime('%Y-%m-%d')}T{new_time}:00"
        new_app_id = await reserve_appointment(start_datetime=new_start_iso, business_id=business_id,taxonomies=taxonomy_id, resource_id=resource_id)
        print(f'reschedule_appointment new_app_id-------------{new_app_id}')

        return (
            f"SYSTEM MESSAGE: New slot reserved. "
            f"Please ask the user: 'I've found a spot on {new_date} at {new_time}. "
            f"Should I move your appointment from {old_date} {old_time} to this new time?' "
            f"Metadata: old_date={old_date}, old_time={old_time}, new_app_id={new_app_id}, "
            f"client_id={client_id}, new_start_iso={new_start_iso}, business_id={business_id}, "
            f"taxonomy_id={taxonomy_id}, resource_id={resource_id}"
        )

    except Exception as e:
        logger.error(f"Reschedule reservation failed: {e}")
        return f"I couldn't hold the new slot. Please try again. Error: {e}"


@function_tool(description="Find an appointment ID using a specific date and time, Department, Service and resource")
async def find_appointment_by_time(date: str, time: str, business_id: str, taxonomyId: str, resourcesId: str):
    date_old_obj = datetime.strptime(date, "%d/%m/%Y")
    standardized_old_datetime = f"{date_old_obj.strftime('%Y-%m-%d')}T{time}:00"
    print(f'find_appointment_by_time business_id-------------{business_id}')

    params = {
        "dateTime": standardized_old_datetime,
        "businessId": business_id,
        "taxonomyId": taxonomyId,
        "resourcesId": resourcesId,
    }
    try:
        appointment_id = await _get_data("search-appointment-by-dateTime", params)

        if not appointment_id or "null" in appointment_id.lower():
            return f"I couldn't find an appointment at {time} on {date}."

        return appointment_id
    except Exception as e:
        logger.error(f"Search failed: {e}")
        return f"Error finding appointment: {e}"


@function_tool(description="Get the current client ID for the logged-in user")
async def get_client_id():
    try:
        client_id = await _get_data("get-client-id-api", {})
        return client_id
    except Exception as e:
        logger.error(f"get_client_id failed: {e}")
        raise e