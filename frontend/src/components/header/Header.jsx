import { useContext } from "react";
import { Link } from "react-router";
import { AuthContext } from "../../contexts/AuthContext";

export default function Header() {

  // Access authentication data from context
  const { accessToken, role, email } = useContext(AuthContext);

  return (
    <header className="flex justify-between items-center px-8 py-4 bg-gray-900 text-white border-b border-gray-800">

      {/* Application logo / home navigation */}
      <Link to="/" className="text-xl font-bold">
        Digital Banking
      </Link>

      {/* Navigation section */}
      <nav className="flex items-center gap-6">

        {/* Guest navigation */}
        {!accessToken && (
          <>
            <Link to="/login" className="text-gray-300 hover:text-white">
              Login
            </Link>

            <Link to="/register" className="text-gray-300 hover:text-white">
              Register
            </Link>
          </>
        )}

        {/* Authenticated navigation */}
        {accessToken && (
          <>
          {/* Show admin link only if ADMIN role*/}
          {role === "ADMIN" && (
            <Link to="/admin" className="text-gray-300 hover:text-white">
              Admin
            </Link>
          )}
          {/* Dashboard link */}
            <Link to="/dashboard" className="text-gray-300 hover:text-white">
              Dashboard
            </Link>

            {/* Display logged user email */}
            <span className="text-gray-300">
              {email}
            </span>

            {/* Logout link */}
            <Link to="/logout" className="text-red-400 hover:text-red-300">
              Logout
            </Link>
          </>
        )}

      </nav>
    </header>
  );
}