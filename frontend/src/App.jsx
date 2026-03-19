import { Routes, Route } from 'react-router';
import { useState } from 'react';
import { jwtDecode } from 'jwt-decode';

import Dashboard from './components/dashboard/Dashboard';
import Login from './components/users/login/Login';
import Logout from './components/users/logout/Logout';
import Register from './components/register/Register';
import Home from './components/home/Home';
import Header from './components/header/Header';
import { AuthContext } from './contexts/AuthContext';
import AuthGuard from './components/guards/AuthGuard';
import { tokenService } from './services/tokenService';
import './App.css';

function App() {

  // lazy initializer restores auth state from local storage on app loads
  const [authData, setAuthData] = useState(() => {

    // get access token from storage
    const accessToken = tokenService.getAccessToken();

    // if no token, return empty auth state
    if (!accessToken) {
      return {};
    }

    // if token exists, decode it to extract user data
    try {
      const decodedToken = jwtDecode(accessToken);

      return {
        email: decodedToken.sub,
        accessToken
      };

    } catch (err) {

      // if token is invalid/expired, remove it from storage
      tokenService.clearTokens();

      return {};
    }
  });

  // Handler executed after successful login
  const userLoginHandler = (data) => {
    setAuthData(data);
  };

  // Handler executed on logout
  const userLogoutHandler = () => {
    setAuthData({});
  };


  return (
    <>
      <AuthContext.Provider value={{ ...authData, userLoginHandler, userLogoutHandler }}>
        <Header />
        <Routes>

          {/* Public routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Protected routes */}
          <Route element={<AuthGuard />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/logout" element={<Logout />} />
          </Route>
        </Routes>
      </AuthContext.Provider>
    </>
  )
}

export default App;
