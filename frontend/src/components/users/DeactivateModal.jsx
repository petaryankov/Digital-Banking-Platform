'use client'

import { useContext, useState } from 'react'
import { useNavigate } from 'react-router';
import { Dialog, DialogBackdrop, DialogPanel, DialogTitle } from '@headlessui/react'
import { ExclamationTriangleIcon } from '@heroicons/react/24/outline'
import { AuthContext } from '../../contexts/AuthContext';
import userApi from '../../api/userApi';

export default function DeactivateModal() {

    // state to track deletion process
    const [isDeleting, setIsDeleting] = useState(false);

    const navigate = useNavigate();

    // access context for auth state and handlers
    const auth = useContext(AuthContext);

    // delete user handler
    const handleDeleteUser = async () => {

        setIsDeleting(true);

        try {

            // call backend API to delete user
            await userApi.deleteUser();

            // clear tokens via context handler
            auth.userLogoutHandler();

            // redirect to home page after account deletion
            navigate("/", { replace: true });

        } catch (err) {

            console.error("Error deleting account:", err);
            alert("Failed to delete account. Please try again.");

        } finally {
            setIsDeleting(false);
        }
    };

    const handleClose = () => {
        navigate("/dashboard", { replace: true });
    };


    return (
        <>
            <Dialog open={true} onClose={handleClose} className="relative z-50">
                <DialogBackdrop
                    transition
                    className="fixed inset-0 bg-gray-900/75 transition-opacity data-closed:opacity-0 data-enter:duration-300 data-enter:ease-out data-leave:duration-200 data-leave:ease-in"
                />

                <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
                    <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
                        <DialogPanel
                            transition
                            className="relative transform overflow-hidden rounded-lg bg-gray-800 text-left shadow-xl outline -outline-offset-1 outline-white/10 transition-all data-closed:translate-y-4 data-closed:opacity-0 data-enter:duration-300 data-enter:ease-out data-leave:duration-200 data-leave:ease-in sm:my-8 sm:w-full sm:max-w-lg data-closed:sm:translate-y-0 data-closed:sm:scale-95"
                        >
                            <div className="bg-gray-800 px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                                <div className="sm:flex sm:items-start">
                                    <div className="mx-auto flex size-12 shrink-0 items-center justify-center rounded-full bg-red-500/10 sm:mx-0 sm:size-10">
                                        <ExclamationTriangleIcon aria-hidden="true" className="size-6 text-red-400" />
                                    </div>
                                    <div className="mt-3 text-center sm:mt-0 sm:ml-4 sm:text-left">
                                        <DialogTitle as="h3" className="text-base font-semibold text-white">
                                            Deactivate user: {auth.email}
                                        </DialogTitle>
                                        <div className="mt-2">
                                            <p className="text-sm text-gray-400">
                                                Are you sure you want to deactivate your user? All of your data will be permanently removed.
                                                This action cannot be undone.
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="bg-gray-700/25 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
                                <button
                                    type="button"
                                    disabled={isDeleting}
                                    onClick={handleDeleteUser}
                                    className="inline-flex w-full justify-center rounded-md bg-red-500 px-3 py-2 text-sm font-semibold text-white hover:bg-red-400 sm:ml-3 sm:w-auto disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {isDeleting ? "Deleting..." : "Yes, Delete Everything"}
                                </button>
                                <button
                                    type="button"
                                    data-autofocus
                                    onClick={handleClose}
                                    className="mt-3 inline-flex w-full justify-center rounded-md bg-white/10 px-3 py-2 text-sm font-semibold text-white inset-ring inset-ring-white/5 hover:bg-white/20 sm:mt-0 sm:w-auto"
                                >
                                    Cancel
                                </button>
                            </div>
                        </DialogPanel>
                    </div>
                </div>
            </Dialog>
        </>
    )
}
