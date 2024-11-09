import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import DoctorCard from "./DoctorCard";
import API_CONFIG from "../config/api.config";

const HomePage = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const userData = JSON.parse(localStorage.getItem("userData") || "{}");
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    size: 10,
  });

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userData");
    navigate("/login");
  };

  const searchDoctors = async (page = 0) => {
    setLoading(true);
    setError("");

    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.DOCTORS}?keyword=${searchQuery}&page=${page}&size=${pagination.size}&sortBy=name&sortDir=asc`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Accept': '*/*'
          }
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch doctors");
      }

      const data = await response.json();
      setDoctors(data.content);
      setPagination({
        currentPage: data.number,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        size: data.size,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    searchDoctors(0);
  };

  const handlePageChange = (newPage) => {
    searchDoctors(newPage);
  };
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Bar */}
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="text-xl font-semibold">Healthcare System</div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate("/appointments")}
                className="text-gray-600 hover:text-gray-900"
              >
                My Appointments
              </button>
              <span className="text-gray-700">
                Welcome, {userData.username}
              </span>
              <button
                onClick={handleLogout}
                className="text-gray-600 hover:text-gray-900"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Search Section */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="max-w-3xl mx-auto">
          <form onSubmit={handleSearch} className="space-y-4">
            <div className="flex flex-col items-center space-y-4">
              <h2 className="text-2xl font-bold text-gray-900">
                Find a Doctor
              </h2>
              <div className="w-full">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search for doctors..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <button
                type="submit"
                className="w-full sm:w-auto px-6 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Search
              </button>
            </div>
          </form>
        </div>
      </div>
      {/*Result Section */}
      <div className="mt-8">
        {loading && (
          <div className="text-center py-4">
            <div className="text-gray-600">Loading...</div>
          </div>
        )}

        {error && (
          <div className="text-center py-4">
            <div className="text-red-500">{error}</div>
          </div>
        )}

        {!loading && !error && doctors.length === 0 && searchQuery && (
          <div className="text-center py-4">
            <div className="text-gray-600">No doctors found</div>
          </div>
        )}

        {!loading && !error && doctors.length > 0 && (
          <div className="space-y-8">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {doctors.map((doctor) => (
                <DoctorCard key={doctor.id} doctor={doctor} />
              ))}
            </div>

            {/* Pagination */}
            {pagination.totalPages > 1 && (
              <div className="flex justify-center space-x-2 mt-6">
                <button
                  onClick={() => handlePageChange(pagination.currentPage - 1)}
                  disabled={pagination.currentPage === 0}
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Previous
                </button>
                <span className="px-4 py-2 text-sm text-gray-700">
                  Page {pagination.currentPage + 1} of {pagination.totalPages}
                </span>
                <button
                  onClick={() => handlePageChange(pagination.currentPage + 1)}
                  disabled={
                    pagination.currentPage === pagination.totalPages - 1
                  }
                  className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default HomePage;
