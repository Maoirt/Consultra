import axios from 'axios';


export const getAuthToken = () => {
    return window.localStorage.getItem('auth_token');
};
export const setAuthHeader = (token) => {
    if (token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        localStorage.setItem('auth_token', token);
    } else {
        delete axios.defaults.headers.common['Authorization'];
        localStorage.removeItem('auth_token');
    }
};
// Get API URL from environment variable or use default
const API_URL = process.env.FRONTEND_REACT_APP_API_URL || 'http://localhost:8080';
axios.defaults.baseURL = API_URL;


export const request = (method, url, data) => {

    let headers = {};
    if (getAuthToken() !== null && getAuthToken() !== "null") {
        headers = {'Authorization': `Bearer ${getAuthToken()}`};
    }


    let sendData = data;
    if (typeof data === 'string') {
        sendData = JSON.stringify(data);
        headers['Content-Type'] = 'application/json';
    }

    return axios({
        method: method,
        url: url,
        headers: headers,
        data: sendData});
};