import { useContext } from "react";
import { Navigate, Outlet } from "react-router";
import { AuthContext } from "../../contexts/AuthContext";

export default function AuthGuard() {
    const auth = useContext(AuthContext);

    // if not authenticated, redirect to login
    if (!auth.accessToken) {
        return <Navigate to="/login" replace />;
    }

    // if authenticated, render the protected component
    return <Outlet />;
}