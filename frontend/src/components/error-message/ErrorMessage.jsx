export default function ErrorMessage({ error }) {
    // if no error, don't render anything
    if (!error) {
        return null;
    }

    return (
        <>
            <p className="mt-4 text-center text-sm text-red-400 bg-red-500/10 border border-red-500 rounded-md py-2 px-3">{error}</p>
        </>
    );
}