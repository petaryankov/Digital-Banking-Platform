import { useContext } from "react";
import { Navigate, Outlet } from "react-router";
import { AuthContext } from "../../contexts/AuthContext";

export default function AuthGuard() {
    const auth = useContext(AuthContext);

    if (!auth.accessToken) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}