import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import API_CONFIG from "../config/api.config";

const BookingModal = ({ doctor, isOpen, onClose }) => {
  const navigate = useNavigate();
  const [selectedSpecialization, setSelectedSpecialization] = useState("");
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const timeSlots = [
    "07:00",
    "08:00",
    "09:00",
    "10:00",
    "11:00",
    "13:00",
    "14:00",
    "15:00",
    "16:00",
    "17:00",
  ];

  const handleBook = async () => {
    if (!selectedSpecialization || !selectedDate || !selectedTime) {
      setError("Please fill in all required fields");
      return;
    }

    setLoading(true);
    setError("");

    const endTime = new Date(`2000-01-01 ${selectedTime}`);
    endTime.setHours(endTime.getHours() + 1);
    const endTimeString = `${endTime
      .getHours()
      .toString()
      .padStart(2, "0")}:00`;

    const userData = JSON.parse(localStorage.getItem("userData"));

    try {
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/api/v1/appointments/book`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            accept: "*/*",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            userId: userData.userId,
            doctorId: doctor.id,
            doctorSpecializationId: parseInt(selectedSpecialization),
            appointmentDate: selectedDate.toLocaleDateString("en-CA"),
            startTime: selectedTime,
            endTime: endTimeString,
          }),
        }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Booking failed");
      }

      // Navigate to appointment detail page
      navigate(`/appointments/${data.id}`, {
        state: { appointmentData: data },
      });
    } catch (err) {
    } finally {
      setLoading(false);
    }


  };
  if (!isOpen) return null;
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">Book Appointment</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        <div className="space-y-4">
          {/* Specialization Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Select Specialization
            </label>
            <select
              value={selectedSpecialization}
              onChange={(e) => setSelectedSpecialization(e.target.value)}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Select a specialization</option>
              {doctor.specializations.map((spec) => (
                <option
                  key={spec.specialization_id}
                  value={spec.specialization_id}
                >
                  {spec.specialization_name} - {spec.consultation_type}
                </option>
              ))}
            </select>
          </div>

          {/* Date Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Select Date
            </label>
            <DatePicker
              selected={selectedDate}
              onChange={setSelectedDate}
              minDate={new Date()}
              className="w-full border border-gray-300 rounded-md p-2"
              placeholderText="Select date"
            />
          </div>

          {/* Time Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Select Time
            </label>
            <select
              value={selectedTime}
              onChange={(e) => setSelectedTime(e.target.value)}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Select time slot</option>
              {timeSlots.map((time) => (
                <option key={time} value={time}>
                  {time}
                </option>
              ))}
            </select>
          </div>

          {error && <div className="text-red-500 text-sm">{error}</div>}

          <button
            onClick={handleBook}
            disabled={loading}
            className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {loading ? "Booking..." : "Confirm Booking"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default BookingModal;
