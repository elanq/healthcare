import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import API_CONFIG from '../config/api.config';
import { useLocation } from 'react-router-dom';

const AppointmentDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [appointmentData, setAppointmentData] = useState(null);
    const [patientData, setPatientData] = useState(null);
    const [doctorData, setDoctorData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const location = useLocation();
    const successMessage = location.state?.message;
    const messageType = location.state?.type;

    useEffect(() => {
        const fetchAllData = async () => {
            try {
                const token = localStorage.getItem('token');

                // Fetch appointment details
                const appointmentResponse = await fetch(
                    `${API_CONFIG.BASE_URL}/api/v1/appointments/${id}`,
                    {
                        headers: {
                            'accept': '*/*',
                            'Authorization': `Bearer ${token}`
                        }
                    }
                );

                if (!appointmentResponse.ok) {
                    throw new Error('Failed to fetch appointment details');
                }

                const appointmentData = await appointmentResponse.json();
                setAppointmentData(appointmentData);

                // Fetch patient (current user) details
                const userResponse = await fetch(
                    `${API_CONFIG.BASE_URL}/api/v1/users/me`,
                    {
                        headers: {
                            'accept': '*/*',
                            'Authorization': `Bearer ${token}`
                        }
                    }
                );

                if (!userResponse.ok) {
                    throw new Error('Failed to fetch user details');
                }

                const userData = await userResponse.json();
                setPatientData(userData);

                // Fetch doctor details
                if (appointmentData.doctor_id) {
                    const doctorResponse = await fetch(
                        `${API_CONFIG.BASE_URL}/api/v1/doctors/${appointmentData.doctor_id}`,
                        {
                            headers: {
                                'accept': '*/*',
                                'Authorization': `Bearer ${token}`
                            }
                        }
                    );

                    if (!doctorResponse.ok) {
                        throw new Error('Failed to fetch doctor details');
                    }

                    const doctorData = await doctorResponse.json();
                    setDoctorData(doctorData);
                }

            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchAllData();
    }, [id]);

    const handlePayment = () => {
        const paymentUrl = appointmentData?.payment_detail?.payment_url;
        if (paymentUrl) {
          window.open(paymentUrl, '_blank');
        }
      };

      const shouldShowPaymentButton = () => {
        const isAppointmentPending = appointmentData?.status === 'PENDING';
        const isPaymentPending = appointmentData?.payment_detail?.status === 'PENDING';
        const hasPaymentUrl = Boolean(appointmentData?.payment_detail?.payment_url);

        return isAppointmentPending && isPaymentPending && hasPaymentUrl;
      };

      const shouldShowMeetingButton = () => {
        const isAppointmentScheduled = appointmentData?.status === "SCHEDULED";
        const isPaymentCompleted =
          appointmentData?.payment_detail?.status === "COMPLETED";

        return isAppointmentScheduled && isPaymentCompleted
      };

      if (loading) {
        return (
          <div className="min-h-screen bg-gray-50 flex items-center justify-center">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading appointment details...</p>
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
                onClick={() => navigate('/home')}
                className="mt-4 text-indigo-600 hover:text-indigo-500"
              >
                Return to Home
              </button>
            </div>
          </div>
        );
      }

      if (!appointmentData || !patientData) {
        return (
          <div className="min-h-screen bg-gray-50 flex items-center justify-center">
            <div className="text-center">
              <p className="text-red-500">Appointment or patient data not found</p>
              <button
                onClick={() => navigate('/home')}
                className="mt-4 text-indigo-600 hover:text-indigo-500"
              >
                Return to Home
              </button>
            </div>
          </div>
        );
      }

      return (
        <div className="min-h-screen bg-gray-50 py-8">
          <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
            {/* Success Message */}
            {successMessage && messageType === "success" && (
              <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-md">
                {successMessage}
              </div>
            )}
            <div className="bg-white shadow rounded-lg overflow-hidden">
              {/* Header */}
              <div className="px-6 py-4 bg-indigo-600">
                <h1 className="text-xl font-semibold text-white">
                  Appointment Details
                </h1>
              </div>

              {/* Content */}
              <div className="p-6 space-y-6">
                {/* Patient and Doctor Info */}
                <div className="grid grid-cols-2 gap-6">
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">
                      Patient
                    </h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {patientData.email}
                    </p>
                  </div>
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">
                      Doctor
                    </h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {doctorData?.name || "Loading..."}
                    </p>
                  </div>
                </div>

                {/* Hospital and Date Info */}
                <div className="grid grid-cols-2 gap-6">
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">
                      Hospital
                    </h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {doctorData?.hospital_name || "Loading..."}
                    </p>
                  </div>
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">
                      Consultation Type
                    </h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {appointmentData.consultation_type}
                    </p>
                  </div>
                </div>

                {/* Appointment Time */}
                <div className="grid grid-cols-2 gap-6">
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">Date</h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {appointmentData.appointment_date}
                    </p>
                  </div>
                  <div>
                    <h2 className="text-sm font-medium text-gray-500">Time</h2>
                    <p className="mt-1 text-sm text-gray-900">
                      {`${appointmentData.start_time}`} -
                      {`${appointmentData.end_time}`}
                    </p>
                  </div>
                </div>

                {/* Status and Payment */}
                <div className="border-t pt-6">
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <h2 className="text-sm font-medium text-gray-500">
                        Appointment Status
                      </h2>
                      <span
                        className={`mt-1 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                        ${
                          appointmentData.status === "PENDING"
                            ? "bg-yellow-100 text-yellow-800"
                            : appointmentData.status === "CONFIRMED"
                            ? "bg-green-100 text-green-800"
                            : "bg-gray-100 text-gray-800"
                        }`}
                      >
                        {appointmentData.status}
                      </span>
                    </div>
                    {appointmentData.payment_detail && (
                      <div>
                        <h2 className="text-sm font-medium text-gray-500">
                          Payment Status
                        </h2>
                        <span
                          className={`mt-1 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                          ${
                            appointmentData.payment_detail.status === "PENDING"
                              ? "bg-yellow-100 text-yellow-800"
                              : appointmentData.payment_detail.status === "PAID"
                              ? "bg-green-100 text-green-800"
                              : "bg-gray-100 text-gray-800"
                          }`}
                        >
                          {appointmentData.payment_detail.status}
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Payment Details */}
                {appointmentData.payment_detail && (
                  <div className="border-t pt-6">
                    <div className="space-y-4">
                      <div>
                        <h2 className="text-sm font-medium text-gray-500">
                          Payment Amount
                        </h2>
                        <p className="mt-1 text-sm text-gray-900">
                          {new Intl.NumberFormat("id-ID", {
                            style: "currency",
                            currency: "IDR",
                          }).format(appointmentData.payment_detail.amount)}
                        </p>
                      </div>

                      {shouldShowPaymentButton() && (
                        <button
                          onClick={handlePayment}
                          className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                        >
                          Proceed to Payment
                        </button>
                      )}

                      {shouldShowMeetingButton() && (
                        <button
                          onClick={() =>
                            navigate("/appointments/" + id + "/meeting")
                          }
                          className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                        >
                          Proceed to Meeting Room
                        </button>
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div className="mt-4 text-center">
              <button
                onClick={() => navigate("/home")}
                className="text-indigo-600 hover:text-indigo-500"
              >
                Back to Home
              </button>
            </div>
          </div>
        </div>
      );

}

export default AppointmentDetail;
