import axios from "axios";


const API_BASE_URL = "http://localhost:8080/api/auth";


const authApi = {

    // login request
    login(email, password) {

        return axios.post(`${API_BASE_URL}/login`, { email, password });
    },

    // register request
    register(data) {
        return axios.post(`${API_BASE_URL}/register`, data);
    },

    // logout request
    logout(refreshToken) {
        return axios.post(`${API_BASE_URL}/logout`, { refreshToken });
    }

};

export default authApi;
