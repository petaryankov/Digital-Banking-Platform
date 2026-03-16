import { useContext, useEffect } from "react";
import { useNavigate } from "react-router";
import { AuthContext } from "../../../contexts/AuthContext";
import { tokenService } from "../../../services/tokenService";


export default function Logout() {

  // access logout handler from context
  const { userLogoutHandler } = useContext(AuthContext);

  // route navigation
  const navigate = useNavigate();

  // execute logout logic on component mount
  useEffect(() => {

    // clear tokens from storage
    tokenService.clearTokens();

    // update auth context to reflect logout
    userLogoutHandler();

    // redirect to home page after logout
    navigate("/");
  }, [navigate, userLogoutHandler]);

  return null;

}
