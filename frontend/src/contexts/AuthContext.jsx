import { createContext } from "react";

export const AuthContext = createContext({
    email: "",
    role: "",
    accessToken: "",
    userLoginHandler: () => null,
    userLogoutHandler: () => null,
});
