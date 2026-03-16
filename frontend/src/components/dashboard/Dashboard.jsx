import { useNavigate } from "react-router";
import { tokenService } from "../../services/tokenService";
import { useEffect } from "react";

export default function Dashboard() {

  const navigate = useNavigate();

  // check authentication on component mount
  useEffect(() => {
    if (!tokenService.getAccessToken()) {
      navigate("/login", { replace: true });
    }
  }, [navigate]);

  return (
    <div className="min-h-screen bg-gray-900 text-white">

      {/* Content */}
      <main className="p-8">
        <h2 className="text-2xl font-bold mb-4">
          Welcome to your Digital Banking Dashboard
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Account Balance</h3>
            <p className="text-gray-400">$0.00</p>
          </div>

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Recent Transactions</h3>
            <p className="text-gray-400">No transactions yet</p>
          </div>

          <div className="bg-gray-800 p-6 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">Quick Actions</h3>
            <p className="text-gray-400">Transfer, Pay Bills, Deposit</p>
          </div>

        </div>
      </main>

    </div>
  );
}
