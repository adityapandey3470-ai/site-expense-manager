import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import { useAuth } from "../context/AuthContext";
import StatusBadge from "../components/StatusBadge"
import {CheckSquare, PlusCircle, Car, FileText, Building2, Wallet, Download, ShieldCheck, Users} from "lucide-react";
import { ClipboardList } from "lucide-react";
import { HardHat, Settings2, Calculator, Crown } from "lucide-react";
import usePullToRefresh from "../hooks/usePullToRefresh";
import Spinner from "../components/Spinner";

const ROLE_ICONS = {
    SUPERVISOR: HardHat,
    OPERATIONS: Settings2,
    ACCOUNTS: Calculator,
    DIRECTOR: ShieldCheck,
};
export default function Home() {
  const { user } = useAuth();
  const [balance, setBalance] = useState(null);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);


    async function load() {
        try {
            const [balanceRes, requestsRes] = await Promise.all([
                user.siteId
                    ? api.get(`/ledgers/balance/site/${user.siteId}`)
                    : Promise.resolve(null),
                api.get("/requests"),
            ]);

            if (balanceRes) setBalance(balanceRes.data);

            const siteRequests = user.siteId
                ? requestsRes.data.filter((r) => r.siteId === user.siteId)
                : requestsRes.data;

            setRequests(siteRequests.slice(0, 5));
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, [user]);

    const { pulling, pullDistance } = usePullToRefresh(load);

    const QUICK_ACTIONS_BY_ROLE = {
        SUPERVISOR: [
            { to: "/attendance/new", Icon: CheckSquare, label: "Mark attendance" },
            { to: "/requests/new", Icon: PlusCircle, label: "New request" },
            { to: "/travel/new", Icon: Car, label: "Travel expense" },
            { to: "/summary", Icon: FileText, label: "Ledger history" },
        ],
        OPERATIONS: [
            { to: "/requests", Icon: FileText, label: "Pending approvals" },
            { to: "/sites", Icon: Building2, label: "Sites" },
            { to: "/attendance", Icon: CheckSquare, label: "Attendance" },
            { to: "/travel-expenses", Icon: Car, label: "Travel expenses" },
        ],
        ACCOUNTS: [
            { to: "/requests", Icon: FileText, label: "Approvals" },
            { to: "/payouts", Icon: Wallet, label: "Payouts" },
            { to: "/ledger/new", Icon: PlusCircle, label: "New ledger entry" },
            { to: "/travel-expenses", Icon: Car, label: "Travel expenses" },
            { to: "/ledger/summary", Icon: FileText, label: "Ledger summary" },
            { to: "/export", Icon: Download, label: "Export reports" },
            { to: "/dashboard", Icon: ShieldCheck, label: "Dashboard" },
        ],
        DIRECTOR: [
            { to: "/requests", Icon: FileText, label: "All requests" },
            { to: "/sites", Icon: Building2, label: "Sites" },
            { to: "/ledger/summary", Icon: FileText, label: "Ledger summary" },
            { to: "/attendance", Icon: CheckSquare, label: "Attendance" },
            { to: "/export", Icon: Download, label: "Export reports" },
            { to: "/ledger/new", Icon: PlusCircle, label: "New ledger entry" },
            { to: "/admin/users", Icon: Users, label: "Manage users" },
            { to: "/dashboard", Icon: ShieldCheck, label: "Dashboard" },
        ],
    };

    const quickActions = QUICK_ACTIONS_BY_ROLE[user?.role] || QUICK_ACTIONS_BY_ROLE.SUPERVISOR;

  return (
    <div className="screen">
        <div
            style={{
                height: pulling ? 50 : pullDistance,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                transition: pulling ? "none" : "height 0.2s ease",
                overflow: "hidden",
            }}
        >
            {(pulling || pullDistance > 40) && <Spinner size={20} color="var(--accent)" />}
        </div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
        <div>
          <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: 0 }}>Good day</p>
          <p style={{ fontSize: 16, fontWeight: 500, margin: 0 }}>{user?.fullName}</p>
        </div>
        <div
          style={{
            width: 36,
            height: 36,
            borderRadius: "50%",
            background: "var(--accent-bg)",
            color: "var(--accent)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            fontSize: 13,
            fontWeight: 500,
          }}
        >
          {user?.fullName?.split(" ").map((n) => n[0]).slice(0, 2).join("")}
        </div>
      </div>


        {user?.siteId && balance && (
            <div className="card" style={{ marginBottom: 12, display: "flex", gap: 12 }}>
                <div
                    style={{
                        width: 44,
                        height: 44,
                        borderRadius: 12,
                        background: "var(--accent-bg)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        flexShrink: 0,
                    }}
                >
                    <HardHat size={22} color="var(--accent)" strokeWidth={1.8} />
                </div>
                <div style={{ flex: 1 }}>
                    <p style={{ fontSize: 12, color: "var(--text-secondary)", margin: "0 0 2px" }}>
                        Site balance · {balance.siteName}
                    </p>
                    <p
                        style={{
                            fontSize: 24,
                            fontWeight: 500,
                            margin: 0,
                            color: balance.negative ? "var(--danger)" : "var(--text-primary)",
                        }}
                    >
                        {balance.negative ? "-" : ""}₹{Math.abs(balance.balance).toLocaleString("en-IN")}
                    </p>
                    <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "6px 0 0" }}>
                        {balance.teamSize} workers on site
                    </p>
                </div>
            </div>
        )}

        {!user?.siteId && (
            <div className="card" style={{ marginBottom: 12, display: "flex", alignItems: "center", gap: 12 }}>
                <div
                    style={{
                        width: 44,
                        height: 44,
                        borderRadius: 12,
                        background: "var(--accent-bg)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        flexShrink: 0,
                    }}
                >
                    {(() => {
                        const RoleIcon = ROLE_ICONS[user?.role] || ROLE_ICONS.SUPERVISOR;
                        return <RoleIcon size={22} color="var(--accent)" strokeWidth={1.8} />;
                    })()}
                </div>
                <div>
                    <p style={{ fontSize: 12, color: "var(--text-secondary)", margin: "0 0 2px" }}>
                        {user?.role?.charAt(0) + user?.role?.slice(1).toLowerCase()}
                    </p>
                    <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>
                        {user?.role === "DIRECTOR" ? "Full access — all sites" : "Approvals & ledger management"}
                    </p>
                </div>
            </div>
        )}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gap: 8,
          marginBottom: 20,
        }}
      >
        {quickActions.map((a) => (
          <Link key={a.to} to={a.to} className="card" style={{ display: "flex", flexDirection: "column", gap: 6 }}>
              <a.Icon size={22} strokeWidth={1.8} color="var(--accent)" />
            <span style={{ fontSize: 13 }}>{a.label}</span>
          </Link>
        ))}
      </div>

      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 8 }}>
        <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>Recent requests</p>
        <Link to="/requests" style={{ fontSize: 12, color: "var(--accent)" }}>
          View all
        </Link>
      </div>

      {loading && <p style={{ fontSize: 13, color: "var(--text-secondary)" }}>Loading…</p>}

        {!loading && requests.length === 0 && (
            <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                <ClipboardList size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                <p style={{ fontSize: 13, margin: 0 }}>No requests yet</p>
                <p style={{ fontSize: 12, margin: "4px 0 0" }}>Tap "New request" to get started</p>
            </div>
        )}

      <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
        {requests.map((r) => (
          <div
            key={r.id}
            className="card"
            style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}
          >
            <div>
              <p style={{ fontSize: 14, margin: 0 }}>{r.description}</p>
              <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                {r.requestedBy} · {r.requestDate}
              </p>
            </div>
            <StatusBadge status={r.status} />
          </div>
        ))}
      </div>
    </div>
  );
}
