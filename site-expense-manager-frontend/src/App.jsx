import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Login from "./pages/Login";
import Home from "./pages/Home";
import RequestsList from "./pages/RequestsList";
import NewRequest from "./pages/NewRequest";
import NewAttendance from "./pages/NewAttendance";
import NewTravelExpense from "./pages/NewTravelExpense";
import Summary from "./pages/Summary";
import More from "./pages/More";
import Help from "./pages/Help";
import PrivacyPolicy from "./pages/PrivacyPolicy";
import { ToastProvider } from "./context/ToastContext";
import { ThemeProvider } from "./context/ThemeContext";
import NewLedgerEntry from "./pages/NewLedgerEntry";
import TravelExpensesList from "./pages/TravelExpensesList";
import AttendanceList from "./pages/AttendanceList";
import LedgerSummary from "./pages/LedgerSummary";
import PayoutsList from "./pages/PayoutsList";
import SitesList from "./pages/SitesList";
import Export from "./pages/Export";
import ManageUsers from "./pages/ManageUsers";
import ChangePassword from "./pages/ChangePassword.jsx";
import NewSite from "./pages/NewSite.jsx";
import Settings from "./pages/Settings";
import DirectorDashboard from "./pages/DirectorDashboard";

export default function App() {
  return (
      <ThemeProvider>
      <ToastProvider>
    <AuthProvider>
        <BrowserRouter>
        <div className="app-shell">
          <Routes>
            <Route path="/login" element={<Login />} />

            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<Home />} />
              <Route path="/requests" element={<RequestsList />} />
              <Route path="/requests/new" element={<NewRequest />} />
              <Route path="/attendance/new" element={<NewAttendance />} />
              <Route path="/travel/new" element={<NewTravelExpense />} />
              <Route path="/summary" element={<Summary />} />
              <Route path="/more" element={<More />} />
              <Route path="/help" element={<Help />} />
              <Route path="/privacy" element={<PrivacyPolicy />} />
              <Route path="/ledger/new" element={<NewLedgerEntry />} />
              <Route path="/travel-expenses" element={<TravelExpensesList />} />
              <Route path="/attendance" element={<AttendanceList />} />
              <Route path="/ledger/summary" element={<LedgerSummary />} />
              <Route path="/export" element={<Export />} />
              <Route path="/admin/users" element={<ManageUsers />} />
              <Route path="/payouts" element={<PayoutsList />} />
              <Route path="/sites" element={<SitesList />} />
              <Route path="/change-password" element={<ChangePassword />} />
              <Route path="/sites/new" element={<NewSite />} />
              <Route path="/sites/:id/edit" element={<NewSite />} />
              <Route path="/ledger/:id/edit" element={<NewLedgerEntry />} />
              <Route path="/requests/:id/edit" element={<NewRequest />} />
              <Route path="/travel/:id/edit" element={<NewTravelExpense />} />
              <Route path="/settings" element={<Settings />} />
              <Route path="/dashboard" element={<DirectorDashboard />} />

            </Route>
          </Routes>
        </div>
      </BrowserRouter>
    </AuthProvider>
      </ToastProvider>
        </ThemeProvider>
  );
}
