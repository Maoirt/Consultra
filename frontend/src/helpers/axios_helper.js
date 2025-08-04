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

const API_URL = process.env.REACT_APP_API_URL || 'https://auth-latest-mean.onrender.com';
const WS_URL = process.env.REACT_APP_WS_URL || 'https://auth-latest-mean.onrender.com';

axios.defaults.baseURL = API_URL;

export const getWebSocketUrl = () => {
    return WS_URL.replace('http', 'ws');
};

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

export const uploadFile = (url, formData) => {
    let headers = {};
    if (getAuthToken() !== null && getAuthToken() !== "null") {
        headers = {'Authorization': `Bearer ${getAuthToken()}`};
    }

    return axios({
        method: 'POST',
        url: url,
        headers: headers,
        data: formData
    });
};