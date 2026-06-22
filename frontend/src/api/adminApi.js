
import { clientApi } from "./clientApi";


const adminApi = {

    // get all users http://localhost:8080/api/users
    getAllUsers() {
        return clientApi.get("/users");
    },
    // activate user PUT http://localhost:8080/api/users/{userId}/activate
    activateUser(userId) {
        return clientApi.put(`/users/${userId}/activate`);
    },
    // deactivate user(admin only) PUT http://localhost:8080/api/users/{userId}/deactivate
    deactivateUser(userId) {
        return clientApi.put(`/users/${userId}/deactivate`);
    }
};

export default adminApi;