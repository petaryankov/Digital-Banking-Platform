import { useContext } from "react";
import { Navigate, Outlet } from "react-router";
import { AuthContext } from "../../contexts/AuthContext";

export default function PublicGuard() {

    const { accessToken, role } = useContext(AuthContext)

    // if authenticated, redirect to dashboard or admin page based on role
    if (accessToken) {
        return <Navigate to={role === "ADMIN" ? "/admin" : "/dashboard"} replace />;
    }

    // if not authenticated, render the public component
    return <Outlet />;

}