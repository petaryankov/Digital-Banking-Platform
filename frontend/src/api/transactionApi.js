import { clientApi } from "./clientApi";


const transactionApi = {

    // POST /api/transactions/deposit
    deposit(accountNumber, amount) {
        return clientApi.post("/transactions/deposit",
            { accountNumber, amount });
    },

    // POST /api/transactions/withdraw
    withdraw(accountNumber, amount) {
        return clientApi.post("/transactions/withdraw",
            { accountNumber, amount });
    },

    // POST /api/transactions/transfer
    transfer(sourceAccountNumber, targetAccountNumber, amount) {
        return clientApi.post("/transactions/transfer", {
            sourceAccountNumber,
            targetAccountNumber,
            amount
        });
    },

    // GET /api/transactions/target?accountNumber=...
    getTargetTransactions(accountNumber) {
        return clientApi.get("/transactions/target", {
            params: { accountNumber }
        });
    }

};

export default transactionApi;
