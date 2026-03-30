import { postRequest } from './api';

const SESSION_KEY = 'sht_user_session';

/**
 * Authenticate user or admin against the backend
 * @param {string} email 
 * @param {string} phoneNumber 
 * @param {string} role 'user' or 'admin'
 * @returns {Promise<Object>} The user session data
 */
export const login = async (email, phoneNumber, role) => {
    try {
        const response = await postRequest('/auth/login', {
            email,
            phoneNumber,
            role
        });
        
        // Save session locally
        sessionStorage.setItem(SESSION_KEY, JSON.stringify(response));
        return response;
    } catch (error) {
        // Extract error message from backend if available
        if (error.response && error.response.data && error.response.data.message) {
            throw new Error(error.response.data.message);
        }
        throw new Error('Authentication failed. Please check your connection to the server.');
    }
};

/**
 * Get current active session
 * @returns {Object|null} Session data or null if not logged in
 */
export const getSession = () => {
    const session = sessionStorage.getItem(SESSION_KEY);
    return session ? JSON.parse(session) : null;
};

/**
 * Clear the current session (Logout)
 */
export const clearSession = () => {
    sessionStorage.removeItem(SESSION_KEY);
};

/**
 * Check if a user is currently logged in
 * @returns {boolean}
 */
export const isAuthenticated = () => {
    return !!getSession();
};
