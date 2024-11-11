import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API_CONFIG from "../config/api.config";
import { MeetingProvider, useMeeting } from "@videosdk.live/react-sdk";
import ParticipantView from "./ParticipantView";

const Controls = ({ onLeave }) => {
  const meeting = useMeeting();
  const { localMicOn, localWebcamOn } = meeting;

  return (
    <div className="fixed bottom-0 left-0 right-0 h-16 bg-gray-800 flex items-center justify-center px-4 space-x-4">
      <button
        onClick={() => {
          meeting.toggleMic();
        }}
        className={`px-4 py-2 ${
          localMicOn
            ? "bg-blue-600 hover:bg-blue-700"
            : "bg-red-600 hover:bg-red-700"
        } text-white rounded-md flex items-center gap-2`}
      >
        {localMicOn ? "🎤 Mic On" : "🚫 Mic Off"}
      </button>
      <button
        onClick={() => {
          meeting.toggleWebcam();
        }}
        className={`px-4 py-2 ${
          localWebcamOn
            ? "bg-blue-600 hover:bg-blue-700"
            : "bg-red-600 hover:bg-red-700"
        } text-white rounded-md flex items-center gap-2`}
      >
        {localWebcamOn ? "📹 Camera On" : "🚫 Camera Off"}
      </button>
      <button
        onClick={() => {
          meeting.leave();
          onLeave();
        }}
        className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
      >
        End Consultation
      </button>
    </div>
  );
};

const MeetingView = ({ onMeetingLeave }) => {
  const [joined, setJoined] = useState(false);

  const { join } = useMeeting({
    onMeetingJoined: () => setJoined(true),
    onMeetingLeft: () => onMeetingLeave(),
  });

  const { participants } = useMeeting();

  if (!joined) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900">
        <div className="bg-white p-8 rounded-lg shadow-xl max-w-md w-full">
          <h2 className="text-2xl font-bold mb-6 text-center">
            Join Consultation
          </h2>
          <button
            onClick={() => {
              join();
            }}
            className="w-full py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Join Meeting
          </button>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="min-h-screen flex items-center pb-16">
        <div className="w-full p-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {Array.from(participants.keys()).map((participantId) => (
              <ParticipantView
                participantId={participantId}
                key={participantId}
              />
            ))}
          </div>
        </div>
      </div>
      <Controls onLeave={onMeetingLeave} />
    </>
  );
};

const MeetingPage = () => {
  const { appointmentId } = useParams();
  const navigate = useNavigate();
  const [meetingDetails, setMeetingDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const initializeMeeting = async () => {
      try {
        const authToken = localStorage.getItem("token");
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/api/v1/appointments/${appointmentId}/meeting`,
          {
            headers: {
              Authorization: `Bearer ${authToken}`,
              accept: "*/*",
            },
          }
        );

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.message || "Failed to initialize meeting");
        }

          console.log(data)

        setMeetingDetails({
          meetingId: data.meeting_id,
          participantName: data.participant_name,
          status: data.status,
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    initializeMeeting();
  }, [appointmentId]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Initializing consultation...</p>
        </div>
      </div>
    );
  }

  if (error || !meetingDetails?.meetingId || !API_CONFIG.VIDEOSDK_TOKEN) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error || "Configuration error"}</p>
          <button
            onClick={() => navigate("/appointments")}
            className="text-indigo-600 hover:text-indigo-500"
          >
            Return to Appointments
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen bg-gray-900">
      <MeetingProvider
        config={{
          meetingId: meetingDetails.meetingId,
          micEnabled: true,
          webcamEnabled: true,
          name: meetingDetails.participantName,
          mode: "CONFERENCE",
          multiStream: true,
        }}
        token={API_CONFIG.VIDEOSDK_TOKEN}
      >
        <MeetingView onMeetingLeave={() => navigate("/appointments")} />
      </MeetingProvider>
    </div>
  );
};

export default MeetingPage;
