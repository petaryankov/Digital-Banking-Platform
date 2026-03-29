import Accounts from "./Accounts";

export default function AccountPage() {
    return (
        <div className="min-h-screen bg-gray-900 text-white p-8">
            <div className="max-w-5xl mx-auto">
                <div className="mb-10">
                    <h1 className="text-4xl font-extrabold tracking-tight">Your Portfolio</h1>
                    <p className="text-gray-400 mt-2 text-lg">Manage your digital assets and currency accounts.</p>
                </div>
                <Accounts />
            </div>
        </div>
    );
}
