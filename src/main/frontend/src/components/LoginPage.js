import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const LoginPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [formData, setFormData] = useState({
      username: '',
      password: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

  // Get success message from registration if any
  const successMessage = location.state?.message;
  const messageType = location.state?.type;


    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await fetch('http://localhost:8080/api/v1/auth/login', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                  'accept': '*/*'
                },
                body: JSON.stringify(formData),
                credentials: 'include'
              });

              const data = await response.json();

              if (!response.ok) {
                throw new Error(data.message || 'Login failed');
              }

             // Store the JWT token and user data
            localStorage.setItem('token', data.token);
            localStorage.setItem('userData', JSON.stringify({
                userId: data.user_id,
                username: data.username,
                email: data.email,
                roles: data.roles
            }));

                  // Redirect to home page
            navigate('/home');

        } catch (err) {
            setError(err.message || 'Failed to connect with server')
        } finally {
            setLoading(false)
        }
    };

    return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
            <div>
            <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                Sign in to your account
            </h2>
            {successMessage && messageType === 'success' && (
                <div className="mt-2 p-2 bg-green-100 border border-green-400 text-green-700 text-center rounded">
                {successMessage}
                </div>
            )}
            </div>
            <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm space-y-4">
                <div>
                <label htmlFor="username" className="sr-only">
                    Username
                </label>
                <input
                    id="username"
                    name="username"
                    type="text"
                    required
                    className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                    placeholder="Username"
                    value={formData.username}
                    onChange={(e) =>
                        setFormData({ ...formData, username: e.target.value })
                    }
                />
                </div>
                <div>
                <label htmlFor="password" className="sr-only">
                    Password
                </label>
                <input
                    id="password"
                    name="password"
                    type="password"
                    required
                    className="appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                    placeholder="Password"
                    value={formData.password}
                    onChange={(e) =>
                    setFormData({ ...formData, password: e.target.value })
                    }
                />
                </div>
            </div>

            {error && (
                <div className="text-red-500 text-sm text-center">{error}</div>
            )}

            <div className="flex flex-col space-y-4">
                <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
                >
                {loading ? 'Signing in...' : 'Sign in'}
                </button>

                <button
                type="button"
                onClick={() => navigate('/register')}
                className="w-full flex justify-center py-2 px-4 border border-indigo-600 text-sm font-medium rounded-md text-indigo-600 bg-white hover:bg-indigo-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                Create new account
                </button>
            </div>
            </form>
        </div>
    </div>
    );
};

export default LoginPage;
