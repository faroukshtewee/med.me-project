import { useState, useCallback } from "react";
import { LiveKitRoom, RoomAudioRenderer } from "@livekit/components-react";
import "@livekit/components-styles";
import SimpleVoiceAssistant from "./SimpleVoiceAssistant";
import PropTypes from 'prop-types';
const LiveKitModal = ({ setShowSupport }) => {
  const [isSubmittingName, setIsSubmittingName] = useState(true);
  const [name, setName] = useState("");
  const [token, setToken] = useState(null);

  const handleDisconnect = useCallback(() => {
    setToken(null);
    setIsSubmittingName(true);
    console.log("Session ended. Ready for a new conversation.");
  }, []);
  const getToken = useCallback(async (userName) => {
    try {
      console.log("run")
    const response = await fetch(
      `http://10.0.0.7:8000/getToken?identity=${encodeURIComponent(userName)}`
    );
    if (!response.ok){
      const errorDetail = await response.json();
      console.error("Server Error Detail:", errorDetail);
      throw new Error("Failed to fetch");
    } 
    const data = await response.json();
    setToken(data.token);
    setIsSubmittingName(false);
    } catch (error) {
      console.error("Token error:",error);
    }
  }, []);

  const handleNameSubmit = (e) => {
    e.preventDefault();
    if (name.trim()) {
      getToken(name);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="support-room">
          {isSubmittingName ? (
            <form onSubmit={handleNameSubmit} className="name-form">
              <h2>Enter your name to connect with support</h2>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Your name"
                required
              />
              <button type="submit">Connect</button>
              <button
                type="button"
                className="cancel-button"
                onClick={() => setShowSupport(false)}
              >
                Cancel
              </button>
            </form>
          ) : token ? (
            <LiveKitRoom
              key={token}
              serverUrl={import.meta.env.VITE_LIVEKIT_URL}
              token= {token}
              onDisconnected={handleDisconnect}
              connect={true}
              video={false}
              audio={true}
              onConnected={() => console.log("Connected to LiveKit")}
            >
              <RoomAudioRenderer />
              <SimpleVoiceAssistant />
            </LiveKitRoom>
          ) : null}
        </div>
      </div>
    </div>
  );
};
LiveKitModal.propTypes = {
  setShowSupport: PropTypes.func.isRequired,
};
export default LiveKitModal;
