import { clientApi } from "./clientApi";


const API_BASE_URL = "http://localhost:8080/api/users";

const userApi = {

    // delete user user
    deleteUser() {
        return clientApi.delete("/users/me");
    }
};

export default userApi;