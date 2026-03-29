import axios from "axios";
import { tokenService } from "../services/tokenService";

const API_BASE_URL = "http://localhost:8080/api/transactions";

// Helper to get headers with the latest token
const getAuthHeaders = () => ({
    headers: {
        Authorization: `Bearer ${tokenService.getAccessToken()}`
    }
});

const transactionApi = {

    // POST /api/transactions/deposit
    deposit(accountNumber, amount) {
        return axios.post(`${API_BASE_URL}/deposit`, 
            { accountNumber, amount }, 
            getAuthHeaders()
        );
    },

    // POST /api/transactions/withdraw
    withdraw(accountNumber, amount) {
        return axios.post(`${API_BASE_URL}/withdraw`, 
            { accountNumber, amount }, 
            getAuthHeaders()
        );
    },

    // POST /api/transactions/transfer
    transfer(sourceAccountNumber, targetAccountNumber, amount) {
        return axios.post(`${API_BASE_URL}/transfer`, 
            { sourceAccountNumber, targetAccountNumber, amount }, 
            getAuthHeaders()
        );
    },

    // GET /api/transactions/target?accountNumber=...
    getTargetTransactions(accountNumber) {
        return axios.get(`${API_BASE_URL}/target`, {
            params: { accountNumber },
            ...getAuthHeaders()
        });
    }

};

export default transactionApi;
