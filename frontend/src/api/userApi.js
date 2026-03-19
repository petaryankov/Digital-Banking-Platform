import axios from "axios";
import { tokenService } from "../services/tokenService";


const API_BASE_URL = "http://localhost:8080/api/users";

const userApi = {

    // delete user user
    deleteUser() {
        const accessToken = tokenService.getAccessToken();

        return axios.delete(`${API_BASE_URL}/me`, {
            headers: {
                Authorization: `Bearer ${accessToken}`
            }
        });
    }
};

export default userApi;