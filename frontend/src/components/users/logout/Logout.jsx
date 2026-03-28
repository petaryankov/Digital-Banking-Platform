import { useContext, useEffect } from "react";
import { useNavigate } from "react-router";
import { AuthContext } from "../../../contexts/AuthContext";

export default function Logout() {

  // access logout handler from context
  const { userLogoutHandler } = useContext(AuthContext);

  // route navigation
  const navigate = useNavigate();

  // execute logout logic on component mount
  useEffect(() => {

    // update auth context to reflect logout
    userLogoutHandler();

    // redirect to home page after logout
    navigate("/", { replace: true });

  }, [navigate, userLogoutHandler]);

  return null;

}
