const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_BASE_URL || "http://localhost:8080",
  ENDPOINTS: {
    LOGIN: "/api/v1/auth/login",
    REGISTER: "/api/v1/auth/register",
    DOCTORS: "/api/v1/doctors",
  },
  VIDEOSDK_TOKEN:
    process.env.REACT_APP_VIDEOSDK_TOKEN ||
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiJkNDQyYmJmMS0zNGQ4LTRkODAtOWQyZC04NTczMTk1Mzc3ZjMiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTczMTE0NDA2NywiZXhwIjoxNzMxNzQ4ODY3fQ.783F1sbXWnBxNlhj2Cuu0DxWu-i9iNXW3032kjGffaM"
};

export default API_CONFIG;
