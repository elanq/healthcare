import React, { useRef, useEffect, useMemo } from "react";
import { useParticipant } from "@videosdk.live/react-sdk";
import ReactPlayer from "react-player";

const ParticipantView = ({ participantId }) => {
    const micRef = useRef(null);
    const { webcamStream, micStream, webcamOn, micOn, isLocal, displayName } =
      useParticipant(participantId);

      const videoStream = useMemo(() => {
        if (webcamOn && webcamStream) {
          const mediaStream = new MediaStream();
          mediaStream.addTrack(webcamStream.track);
          return mediaStream;
        }
      }, [webcamStream, webcamOn]);

      useEffect(() => {
        if (micRef.current) {
          if (micOn && micStream) {
            const mediaStream = new MediaStream();
            mediaStream.addTrack(micStream.track);
            micRef.current.srcObject = mediaStream;
            micRef.current
              .play()
              .catch((error) => console.error("Audio playback failed", error));
          } else {
            micRef.current.srcObject = null;
          }
        }
      }, [micStream, micOn]);

      return (
        <div className="relative">
          <audio ref={micRef} autoPlay playsInline muted={isLocal} />

          <div className="relative w-full aspect-video bg-gray-800 rounded-lg overflow-hidden">
            {webcamOn && videoStream ? (
              <ReactPlayer
                playsinline
                pip={false}
                light={false}
                controls={false}
                muted={true}
                playing={true}
                url={videoStream}
                width="100%"
                height="100%"
                onError={(err) => console.log(err, "participant video error")}
              />
            ) : (
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="w-20 h-20 rounded-full bg-gray-600 flex items-center justify-center">
                  <span className="text-2xl text-white">
                    {displayName ? displayName[0].toUpperCase() : "P"}
                  </span>
                </div>
              </div>
            )}

            <div className="absolute bottom-0 left-0 right-0 px-4 py-2 bg-black bg-opacity-50">
              <div className="flex items-center justify-between">
                <span className="text-white text-sm">
                  {displayName || "Participant"}
                </span>
              </div>
            </div>
          </div>
        </div>
      );
}


export default ParticipantView;
