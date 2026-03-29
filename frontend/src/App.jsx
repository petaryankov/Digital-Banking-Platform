import { Routes, Route } from 'react-router';
import { useState } from 'react';
import { jwtDecode } from 'jwt-decode';

import { AuthContext } from './contexts/AuthContext';
import { tokenService } from './services/tokenService';
import Home from './components/home/Home';
import Header from './components/header/Header';
import Login from './components/users/login/Login';
import Logout from './components/users/logout/Logout';
import Register from './components/register/Register';
import Dashboard from './components/dashboard/Dashboard';
import AuthGuard from './components/guards/AuthGuard';
import AdminDashboard from './components/admin/AdminDashboard';
import AdminGuard from './components/guards/AdminGuard';
import PublicGuard from './components/guards/PublicGuard';
import DeactivateModal from './components/users/DeactivateModal';
import AccountPage from './components/accounts/AccountPage';
import './App.css';

function App() {

  // empty auth state template
  const EMPTY_AUTH_STATE = {
    email: "",
    role: "",
    accessToken: "",
    refreshToken: ""
  };

  // lazy initializer restores auth state from local storage on app loads
  const [authData, setAuthData] = useState(() => {

    // get access token from storage
    const accessToken = tokenService.getAccessToken();

    // if no token, return empty auth state
    if (!accessToken) {
      return EMPTY_AUTH_STATE;
    }

    // if token exists, decode it to extract user data
    try {
      // decode token to get user info
      const decodedToken = jwtDecode(accessToken);

      return {
        email: decodedToken.sub,
        role: decodedToken.role,
        accessToken,
        refreshToken: tokenService.getRefreshToken()
      };

    } catch (err) {

      // if token is invalid/expired, remove it from storage
      tokenService.clearTokens();

      return EMPTY_AUTH_STATE;
    }
  });

  // handler executed on successful login
  const userLoginHandler = (data) => {

    // persist tokens in storage
    tokenService.setTokens(data.accessToken, data.refreshToken);

    // update auth state with user data
    setAuthData({
      email: data.email,
      role: data.role,
      accessToken: data.accessToken,
      refreshToken: data.refreshToken
    });
  };

  // Handler executed on logout
  const userLogoutHandler = () => {

    // Clear tokens from storage and reset auth state
    tokenService.clearTokens();

    setAuthData(EMPTY_AUTH_STATE);
  };


  return (
    <>
      <AuthContext.Provider value={{ ...authData, userLoginHandler, userLogoutHandler }}>
        <Header />
        <Routes>

          {/* Public routes */}
          <Route path="/" element={<Home />} />

          {/* Public routes accessible only to unauthenticated users*/}
          <Route element={<PublicGuard />} >
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
          </Route>

          {/* Protected routes */}
          <Route element={<AuthGuard />}>

            <Route path="/logout" element={<Logout />} />

            <Route path="/dashboard" element={<Dashboard />}>
              <Route path="delete-user" element={<DeactivateModal />} />
            </Route>

            <Route path="/accounts" element={<AccountPage />} />

          </Route>

          {/* Admin-only routes */}
          <Route element={<AdminGuard />}>
            <Route path="/admin" element={<AdminDashboard />} />
          </Route>

        </Routes>
      </AuthContext.Provider>
    </>
  )
}

export default App;
