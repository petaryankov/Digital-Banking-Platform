import { Outlet, useNavigate } from "react-router";

import Accounts from "../accounts/Accounts";
import { useContext } from "react";
import { AuthContext } from "../../contexts/AuthContext";

export default function Dashboard() {

  const navigate = useNavigate();

  const { role } = useContext(AuthContext);

  return (
    <div className="min-h-screen bg-gray-900 text-white">

      {/* Content */}
      <main className="p-8 max-w-7xl mx-auto">
        <h2 className="text-2xl font-bold mb-6">
          Welcome to your Digital Banking Dashboard
        </h2>

        {/* Summary cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Total Balance</h3>
            <p className="text-gray-400">Coming soon</p>
          </div>

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Transactions</h3>
            <p className="text-gray-400">Coming soon</p>
          </div>

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Quick Actions</h3>
            <p className="text-gray-400">Deposit / Transfer</p>
          </div>

        </div>

        <div className="mt-10">
            <Accounts />
        </div>

      </main>

      <Outlet />

      {role !== "ADMIN" && (
        <button
          onClick={() => navigate("delete-user")}
          className="fixed bottom-6 right-6 bg-red-600 hover:bg-red-500 text-white font-medium px-1.5 py-1.5 text-sm rounded-md shadow-md transition"
        >
          Delete User
        </button>
      )};

    </div>
  );
}
