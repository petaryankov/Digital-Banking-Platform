import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { tokenService } from "../services/tokenService";

const API_BASE_URL = "http://localhost:8080/api";

export const clientApi = axios.create({
    baseURL: API_BASE_URL
});

let onTokenRefreshed = () => {};
let onSessionExpired = () => {};

export const setupInterceptors = (onRefreshSuccess, onForceLogout) => {
    onTokenRefreshed = onRefreshSuccess;
    onSessionExpired = onForceLogout;
};

// variable to handle simultaneous requests
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};

// request interceptor: attach access token if it exists
clientApi.interceptors.request.use(
    config => {
        const accessToken = tokenService.getAccessToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// response interceptor: handle 401 errors and token refresh
clientApi.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        // If error is 401 and we haven't already tried to refresh
        if(error.response?.status === 401 && !originalRequest._retry) {
            // If a refresh is already in progress, queue the request
            if (isRefreshing) {
                // If a refresh is already in progress, queue the request
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                }).then(token => {
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    return clientApi(originalRequest); // retry original request
                }).catch(err => {
                    return Promise.reject(err);
                });
            }

            // Mark the request as a retry to avoid infinite loops
            originalRequest._retry = true;
            isRefreshing = true;

            const refreshToken = tokenService.getRefreshToken();

            try {
                // Call the refresh token endpoint
                const res = await axios.post(`${API_BASE_URL}/auth/refresh`, { 
                    refreshToken: refreshToken
                });
                const { accessToken: newAccessToken, refreshToken: newRefreshToken } = res.data;

                // Save new tokens
                tokenService.setTokens(newAccessToken, newRefreshToken);

                // sync React Context
                const newDecodedToken = jwtDecode(newAccessToken);
                onTokenRefreshed({
                    email: newDecodedToken.sub,
                    role: newDecodedToken.role,
                    accessToken: newAccessToken,
                    refreshToken: newRefreshToken
                });

                // release the queue with new token
                processQueue(null, newAccessToken);
                isRefreshing = false;

                // retry original request with new token
                originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
                return clientApi(originalRequest);

            } catch (refreshError) {
                // refresh failed, clear tokens and force logout
                processQueue(refreshError, null);
                isRefreshing = false;
                tokenService.clearTokens();
                onSessionExpired();
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);