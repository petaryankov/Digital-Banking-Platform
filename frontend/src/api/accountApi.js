import axios from "axios";
import { tokenService } from "../services/tokenService";


const API_BASE_URL = "http://localhost:8080/api/accounts";

const accountApi = {

    getUserAccounts() {
        return axios.get(`${API_BASE_URL}/me`, {
            headers: {
                Authorization: `Bearer ${tokenService.getAccessToken()}`
            }
        });
    },

    createAccount(currency) {
        return axios.post(API_BASE_URL, { currency }, {
            headers: {
                Authorization: `Bearer ${tokenService.getAccessToken()}`
            }
        });
    }

};

export default accountApi;