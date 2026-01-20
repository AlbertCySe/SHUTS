import axios from 'axios';

/**
 * API Service for communicating with Spring Boot backend
 * Base URL: http://localhost:8080
 */

// Create Axios instance with base configuration
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 seconds timeout
});

/**
 * Generic GET request
 * @param {string} endpoint - API endpoint (e.g., '/users')
 * @returns {Promise} Response data or error
 */
export const getRequest = async (endpoint) => {
    try {
        const response = await api.get(endpoint);
        return response.data;
    } catch (error) {
        console.error('GET request failed:', error.message);
        throw error;
    }
};

/**
 * Generic POST request
 * @param {string} endpoint - API endpoint (e.g., '/users')
 * @param {object} data - Data to send in request body
 * @returns {Promise} Response data or error
 */
export const postRequest = async (endpoint, data) => {
    try {
        const response = await api.post(endpoint, data);
        return response.data;
    } catch (error) {
        console.error('POST request failed:', error.message);
        throw error;
    }
};

/**
 * Generic PUT request
 * @param {string} endpoint - API endpoint (e.g., '/users/1')
 * @param {object} data - Data to send in request body
 * @returns {Promise} Response data or error
 */
export const putRequest = async (endpoint, data) => {
    try {
        const response = await api.put(endpoint, data);
        return response.data;
    } catch (error) {
        console.error('PUT request failed:', error.message);
        throw error;
    }
};

/**
 * Generic DELETE request
 * @param {string} endpoint - API endpoint (e.g., '/users/1')
 * @returns {Promise} Response data or error
 */
export const deleteRequest = async (endpoint) => {
    try {
        const response = await api.delete(endpoint);
        return response.data;
    } catch (error) {
        console.error('DELETE request failed:', error.message);
        throw error;
    }
};

// Export the axios instance for direct use if needed
export default api;
