import { useEffect, useState } from "react";
import accountApi from "../../api/accountApi";
import transactionApi from "../../api/transactionApi"; // You'll need to create this service

export default function Accounts() {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    
    // Modal & Form States
    const [activeModal, setActiveModal] = useState(null); // 'deposit', 'withdraw', 'transfer', 'create'
    const [selectedAccount, setSelectedAccount] = useState(null);
    const [formData, setFormData] = useState({ amount: "", targetAccount: "", currency: "EUR" });

    useEffect(() => {
        loadAccounts();
    }, []);

    const loadAccounts = async () => {
        try {
            const res = await accountApi.getUserAccounts();
            setAccounts(res.data);
        } catch (err) {
            console.error("Failed to load accounts");
        } finally {
            setLoading(false);
        }
    };

    const handleTransaction = async (e) => {
        e.preventDefault();
        try {
            if (activeModal === 'deposit') {
                await transactionApi.deposit(selectedAccount.accountNumber, formData.amount);
            } else if (activeModal === 'withdraw') {
                await transactionApi.withdraw(selectedAccount.accountNumber, formData.amount);
            } else if (activeModal === 'transfer') {
                await transactionApi.transfer(selectedAccount.accountNumber, formData.targetAccount, formData.amount);
            } else if (activeModal === 'create') {
                await accountApi.createAccount(formData.currency);
            }
            
            // Refresh data and close modal
            await loadAccounts();
            closeModal();
        } catch (err) {
            alert(err.response?.data?.message || "Transaction failed");
        }
    };

    const openModal = (type, account = null) => {
        setActiveModal(type);
        setSelectedAccount(account);
        setFormData({ amount: "", targetAccount: "", currency: "EUR" });
    };

    const closeModal = () => {
        setActiveModal(null);
        setSelectedAccount(null);
    };

    if (loading) return <div className="text-gray-500 animate-pulse p-6">Syncing with bank...</div>;

    return (
        <div className="bg-gray-800 rounded-2xl border border-gray-700 shadow-xl overflow-hidden">
            {/* Header */}
            <div className="p-6 border-b border-gray-700 flex justify-between items-center bg-gray-800/50">
                <h2 className="text-xl text-white font-semibold">Active Accounts</h2>
                <button
                    onClick={() => openModal('create')}
                    className="bg-blue-600 px-5 py-1.5 rounded-lg font-bold hover:bg-blue-500 transition shadow-lg"
                >
                    + Open Account
                </button>
            </div>

            {/* Account Grid */}
            <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                {accounts.map(acc => (
                    <div key={acc.id} className="bg-gray-900 p-5 rounded-xl border border-gray-700 hover:border-blue-500 transition-all duration-300">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <p className="text-blue-400 font-mono text-xs tracking-widest uppercase">Account Number</p>
                                <p className="text-sm font-semibold text-gray-300">{acc.accountNumber}</p>
                            </div>
                            <span className="bg-gray-800 text-gray-400 text-[10px] px-2 py-1 rounded font-bold border border-gray-700">
                                {acc.currency}
                            </span>
                        </div>

                        <div className="mb-6">
                            <p className="text-white text-3xl font-bold tracking-tight">
                                {Number(acc.balance).toLocaleString(undefined, { minimumFractionDigits: 2 })}
                            </p>
                            <p className="text-[10px] text-gray-500 uppercase mt-1">Available Balance</p>
                        </div>

                        {/* Action Buttons */}
                        <div className="flex gap-2 border-t border-gray-800 pt-4">
                            <button onClick={() => openModal('deposit', acc)} className="flex-1 text-xs bg-gray-800 hover:bg-green-900/30 text-green-400 py-2 rounded-md transition font-bold">Deposit</button>
                            <button onClick={() => openModal('withdraw', acc)} className="flex-1 text-xs bg-gray-800 hover:bg-red-900/30 text-red-400 py-2 rounded-md transition font-bold">Withdraw</button>
                            <button onClick={() => openModal('transfer', acc)} className="flex-1 text-xs bg-gray-800 hover:bg-blue-900/30 text-blue-400 py-2 rounded-md transition font-bold">Transfer</button>
                        </div>
                    </div>
                ))}
            </div>

            {/* UNIFIED MODAL SYSTEM */}
            {activeModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
                    <form onSubmit={handleTransaction} className="bg-gray-800 border border-gray-700 w-full max-w-md rounded-2xl shadow-2xl p-8">
                        <h2 className="text-2xl font-bold mb-1 text-white capitalize">{activeModal} Funds</h2>
                        <p className="text-gray-400 mb-6 text-sm">
                            {activeModal === 'create' ? "Select account details" : `Account: ${selectedAccount?.accountNumber}`}
                        </p>

                        <div className="space-y-4">
                            {activeModal === 'create' ? (
                                <div>
                                    <label className="text-xs text-gray-500 uppercase font-bold">Currency</label>
                                    <select 
                                        value={formData.currency}
                                        onChange={(e) => setFormData({...formData, currency: e.target.value})}
                                        className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 mt-1 outline-none focus:border-blue-500"
                                    >
                                        <option value="EUR">Euro (EUR)</option>
                                        <option value="USD">US Dollar (USD)</option>
                                    </select>
                                </div>
                            ) : (
                                <>
                                    <div>
                                        <label className="text-xs text-gray-500 uppercase font-bold">Amount</label>
                                        <input 
                                            type="number" required min="0.01" step="0.01"
                                            value={formData.amount}
                                            onChange={(e) => setFormData({...formData, amount: e.target.value})}
                                            className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 mt-1 outline-none focus:border-blue-500"
                                            placeholder="0.00"
                                        />
                                    </div>
                                    {activeModal === 'transfer' && (
                                        <div>
                                            <label className="text-xs text-gray-500 uppercase font-bold">Target Account Number</label>
                                            <input 
                                                type="text" required
                                                value={formData.targetAccount}
                                                onChange={(e) => setFormData({...formData, targetAccount: e.target.value})}
                                                className="w-full bg-gray-900 border border-gray-700 rounded-lg p-3 mt-1 outline-none focus:border-blue-500"
                                                placeholder="ACC123..."
                                            />
                                        </div>
                                    )}
                                </>
                            )}
                        </div>

                        <div className="flex gap-3 mt-8">
                            <button type="button" onClick={closeModal} className="flex-1 px-4 py-3 rounded-lg font-bold text-gray-400 hover:bg-gray-700 transition">Cancel</button>
                            <button type="submit" className="flex-1 px-4 py-3 rounded-lg font-bold bg-blue-600 hover:bg-blue-500 text-white transition shadow-lg">Confirm</button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}
