import { Link, useNavigate } from "react-router";
import { useActionState, useContext, useState } from "react";
import { jwtDecode } from "jwt-decode";

import { tokenService } from "../../../services/tokenService";
import { AuthContext } from "../../../contexts/AuthContext";
import ErrorMessage from "../../error-message/ErrorMessage";
import authApi from "../../../api/authApi";

export default function Login() {

  // ui state
  const [error, setError] = useState(null);

  // route navigation
  const navigate = useNavigate();

  // reset error on submit
  const handleSubmit = () => {
    setError(null);
  }

  // access login handler from context
  const { userLoginHandler } = useContext(AuthContext);

  // submit login
  const loginHandler = async (_, formData) => {

    const { email, password } = Object.fromEntries(formData);

    try {

      // call login api
      const res = await authApi.login(email, password);

      const { accessToken, refreshToken, role } = res.data;

      // save tokens
      tokenService.setTokens(
        accessToken,
        refreshToken
      );

      // decode token for user data to extract user info
      const decodedToken = jwtDecode(res.data.accessToken);

      // update auth context with user data
      userLoginHandler({
        email: decodedToken.sub,
        accessToken,
        refreshToken,
        role
      });

      // redirect based on role
      navigate(role === "ADMIN" ? "/admin" : "/dashboard", { replace: true });

    } catch (err) {

      // set error message based on response status
      setError(err.response?.status === 401
        ? "Invalid email or password!!!"
        : "Something went wrong! Try again later");
    }
  };

  // manage login action state
  const [_, loginAction, isPending] = useActionState(loginHandler);

  return (
    <>
      <div className="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
        <div className="sm:mx-auto sm:w-full sm:max-w-sm">
          <h2 className="mt-10 text-center text-2xl/9 font-bold tracking-tight text-white">Login to your account</h2>
        </div>

        <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
          <form id="login" action={loginAction} onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="email" className="block text-sm/6 font-medium text-gray-100">
                Email address
              </label>
              <div className="mt-2">
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  disabled={isPending}
                  autoComplete="email"
                  className="block w-full rounded-md bg-white/5 px-3 py-1.5 text-base text-white outline-1 -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-500 sm:text-sm/6"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between">
                <label htmlFor="password" className="block text-sm/6 font-medium text-gray-100">
                  Password
                </label>
                <div className="text-sm">
                  <Link to="#" className="font-semibold text-indigo-400 hover:text-indigo-300">
                    Forgot password?
                  </Link>
                </div>
              </div>
              <div className="mt-2">
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  disabled={isPending}
                  autoComplete="current-password"
                  className="block w-full rounded-md bg-white/5 px-3 py-1.5 text-base text-white outline-1 -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-500 sm:text-sm/6"
                />
              </div>
            </div>

            {error && <ErrorMessage error={error} />}

            <div>
              <button
                type="submit"
                value="Login"
                disabled={isPending}
                className="flex w-full justify-center rounded-md bg-indigo-500 px-3 py-1.5 text-sm/6 font-semibold text-white hover:bg-indigo-400 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
              >
                {isPending
                  ? (
                    <>
                      Signing in...
                    </>
                  )
                  : ("Login")}
              </button>
            </div>
          </form>

          <p className="mt-10 text-center text-sm/6 text-gray-400">
            Not a member?{' '}
            <Link to="/register" className="font-semibold text-indigo-400 hover:text-indigo-300">
              Register here
            </Link>
          </p>
        </div>
      </div>
    </>
  )
}
