import { useContext } from "react";
import { Link } from "react-router";
import { AuthContext } from "../../contexts/AuthContext";

export default function Home() {

  const { accessToken } = useContext(AuthContext);

  return (
    <div className="min-h-screen bg-gray-900 text-white flex flex-col">

      {/* Hero section */}
      <main className="flex flex-1 items-center justify-center text-center px-6">
        <div>
          <h2 className="text-4xl font-bold mb-6">
            Secure. Modern. Digital Banking.
          </h2>

          <p className="text-gray-400 mb-8">
            Manage your accounts, transfer funds, and monitor transactions securely.
          </p>

          {/* Show "Get Started" button only if user is NOT logged in */}
          {!accessToken ? (
            <Link
              to="/login"
              className="bg-indigo-500 hover:bg-indigo-400 px-6 py-3 rounded-md font-semibold"
            >
              Get Started
            </Link>
          ) : (
            <Link
              to="/dashboard"
              className="bg-indigo-500 hover:bg-indigo-400 px-6 py-3 rounded-md font-semibold"
            >
              View My Dashboard
            </Link>
          )}
        </div>
      </main>

    </div>
  );
}
