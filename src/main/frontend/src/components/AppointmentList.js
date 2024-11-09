import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import API_CONFIG from "../config/api.config";
import BookingModal from './BookingModal';

const AppointmentList = () => {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);
  const [showRescheduleModal, setShowRescheduleModal] = useState(false);
  const [selectedAppointment, setSelectedAppointment] = useState(null);

  useEffect(() => {
    const fetchAppointments = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/api/v1/appointments`,
          {
            headers: {
              accept: "*/*",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch appointments");
        }

        const data = await response.json();
        setAppointments(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchAppointments();
  }, []);

  const formatTime = (timeString) => {
    return timeString.substring(0, 5); // Extract HH:mm from HH:mm:ss
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-GB", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
    }).format(amount);
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-yellow-100 text-yellow-800";
      case "CONFIRMED":
        return "bg-green-100 text-green-800";
      case "CANCELLED":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading appointments...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-500">{error}</p>
          <button
            onClick={() => navigate("/home")}
            className="mt-4 text-indigo-600 hover:text-indigo-500"
          >
            Return to Home
          </button>
        </div>
      </div>
    );
  }

  const handleRescheduleClick = (appointment) => {
    setSelectedAppointment(appointment);
    setShowRescheduleModal(true);
  };

  const handleCancel = async (appointmentId) => {
    if (!window.confirm("Are you sure you want to cancel this appointment?")) {
      return;
    }

    setCancellingId(appointmentId);
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/api/v1/appointments/${appointmentId}/cancel`,
        {
          method: "PUT",
          headers: {
            accept: "*/*",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to cancel appointment");
      }

      // Redirect to appointment detail with success message
      navigate(`/appointments/${appointmentId}`, {
        state: {
          message: "Appointment cancelled successfully",
          type: "success",
        },
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setCancellingId(null);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-900">My Appointments</h1>
          <p className="mt-2 text-sm text-gray-700">
            View and manage your appointments
          </p>
        </div>

        {/* Appointments List */}
        <div className="bg-white shadow overflow-hidden sm:rounded-md">
          {appointments.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-gray-500">No appointments found</p>
            </div>
          ) : (
            <ul className="divide-y divide-gray-200">
              {appointments.map((appointment) => (
                <li key={appointment.id} className="p-6 hover:bg-gray-50">
                  <div className="space-y-4">
                    {/* Date and Time */}
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="text-lg font-medium text-gray-900">
                          {formatDate(appointment.appointment_date)}
                        </p>
                        <p className="text-sm text-gray-500">
                          {formatTime(appointment.start_time)} -{" "}
                          {formatTime(appointment.end_time)}
                        </p>
                      </div>
                      <button
                        onClick={() =>
                          navigate(`/appointments/${appointment.id}`)
                        }
                        className="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
                      >
                        View Details →
                      </button>

                      {appointment.status === "PENDING" && (
                        <>
                          <button
                            onClick={() => handleRescheduleClick(appointment)}
                            className="text-yellow-600 hover:text-yellow-900 text-sm font-medium"
                          >
                            Reschedule
                          </button>
                          <button
                            onClick={() => handleCancel(appointment.id)}
                            disabled={cancellingId === appointment.id}
                            className="text-red-600 hover:text-red-900 text-sm font-medium disabled:opacity-50"
                          >
                            {cancellingId === appointment.id
                              ? "Cancelling..."
                              : "Cancel"}
                          </button>
                        </>
                      )}
                    </div>

                    {/* Status Badges */}
                    <div className="flex flex-wrap gap-2">
                      {/* Consultation Type */}
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                        {appointment.consultation_type}
                      </span>

                      {/* Appointment Status */}
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusBadgeClass(
                          appointment.status
                        )}`}
                      >
                        {appointment.status}
                      </span>

                      {/* Payment Status */}
                      {appointment.payment_detail && (
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusBadgeClass(
                            appointment.payment_detail.status
                          )}`}
                        >
                          Payment: {appointment.payment_detail.status}
                        </span>
                      )}
                    </div>

                    {/* Payment Amount */}
                    {appointment.payment_detail && (
                      <div className="text-sm text-gray-500">
                        Amount:{" "}
                        {formatCurrency(appointment.payment_detail.amount)}
                      </div>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        {selectedAppointment && (
          <BookingModal
            isOpen={showRescheduleModal}
            onClose={() => {
              setShowRescheduleModal(false);
              setSelectedAppointment(null);
            }}
            mode="reschedule"
            appointmentId={selectedAppointment.id}
            initialDate={selectedAppointment.appointment_date}
            initialTime={formatTime(selectedAppointment.start_time)}
          />
        )}

        {/* Back Button */}
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate("/appointments")}
            className="text-indigo-600 hover:text-indigo-500"
          >
            Back to My Appointment
          </button>
        </div>
      </div>
    </div>
  );
};

export default AppointmentList;
