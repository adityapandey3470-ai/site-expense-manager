import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import BottomNav from "./BottomNav";

export default function ProtectedRoute() {
  const { user } = useAuth();

    if (!user) {
        const hasSeenOnboarding = localStorage.getItem("hasSeenOnboarding");
        return <Navigate to={hasSeenOnboarding ? "/login" : "/welcome"} replace />;
    }

  return (
    <>
      <Outlet />
      <BottomNav />
    </>
  );
}
