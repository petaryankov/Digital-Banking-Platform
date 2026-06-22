import { clientApi } from "./clientApi";


const API_BASE_URL = "http://localhost:8080/api/accounts";

const accountApi = {

    getUserAccounts() {
        return clientApi.get("/accounts/me");
    },

    createAccount(currency) {
        return clientApi.post("/accounts", { currency });
    }

};

export default accountApi;