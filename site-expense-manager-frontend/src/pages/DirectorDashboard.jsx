import { useEffect, useState } from "react";
import api from "../api/client";
import Spinner from "../components/Spinner";
import { Wallet, TrendingDown, AlertTriangle, Clock } from "lucide-react";

export default function DirectorDashboard() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api
            .get("/dashboard/director")
            .then((res) => setStats(res.data))
            .catch(() => {})
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div className="screen" style={{ display: "flex", justifyContent: "center", padding: 40 }}>
                <Spinner size={22} color="var(--accent)" />
            </div>
        );
    }

    if (!stats) {
        return (
            <div className="screen">
                <p style={{ textAlign: "center", color: "var(--text-muted)", padding: 24 }}>
                    Couldn't load dashboard data.
                </p>
            </div>
        );
    }

    const disbursed = Number(stats.totalDisbursed) || 0;
    const used = Number(stats.totalUsed) || 0;
    const usedPct = disbursed > 0 ? Math.min(100, Math.round((used / disbursed) * 100)) : 0;

    const secondaryCards = [
        {
            label: "Total used",
            value: `₹${used.toLocaleString("en-IN")}`,
            Icon: TrendingDown,
            color: "var(--warning)",
            bg: "var(--warning-bg)",
        },
        {
            label: "Sites in minus",
            value: stats.sitesInMinus,
            Icon: AlertTriangle,
            color: "var(--danger)",
            bg: "var(--danger-bg)",
        },
        {
            label: "Pending approvals",
            value: stats.pendingApprovals,
            Icon: Clock,
            color: "var(--accent)",
            bg: "var(--accent-bg)",
        },
    ];

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Dashboard</p>

            {/* Hero card — total disbursed, with a usage progress bar */}
            <div
                className="card"
                style={{
                    marginBottom: 12,
                    background: "linear-gradient(135deg, var(--accent), #1f5aa8)",
                    border: "none",
                }}
            >
                <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 14 }}>
                    <div
                        style={{
                            width: 38, height: 38, borderRadius: "50%",
                            background: "rgba(255,255,255,0.18)",
                            display: "flex", alignItems: "center", justifyContent: "center",
                        }}
                    >
                        <Wallet size={18} color="#fff" strokeWidth={2} />
                    </div>
                    <p style={{ fontSize: 13, color: "rgba(255,255,255,0.85)", margin: 0 }}>
                        Total disbursed
                    </p>
                </div>

                <p style={{ fontSize: 28, fontWeight: 700, color: "#fff", margin: "0 0 12px" }}>
                    ₹{disbursed.toLocaleString("en-IN")}
                </p>

                <div
                    style={{
                        height: 6, borderRadius: 100,
                        background: "rgba(255,255,255,0.2)", overflow: "hidden",
                    }}
                >
                    <div
                        style={{
                            height: "100%", width: `${usedPct}%`,
                            background: "#fff", borderRadius: 100,
                            transition: "width 0.4s ease",
                        }}
                    />
                </div>
                <p style={{ fontSize: 11, color: "rgba(255,255,255,0.75)", margin: "8px 0 0" }}>
                    {usedPct}% used across all sites
                </p>
            </div>

            {/* Secondary stat cards */}
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {secondaryCards.map(({ label, value, Icon, color, bg }) => (
                    <div
                        key={label}
                        className="card"
                        style={{ display: "flex", alignItems: "center", gap: 12 }}
                    >
                        <div
                            style={{
                                width: 40, height: 40, borderRadius: "50%",
                                background: bg, color,
                                display: "flex", alignItems: "center", justifyContent: "center",
                                flexShrink: 0,
                            }}
                        >
                            <Icon size={18} strokeWidth={2} />
                        </div>
                        <div>
                            <p style={{ fontSize: 18, fontWeight: 600, margin: 0 }}>{value}</p>
                            <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "2px 0 0" }}>
                                {label}
                            </p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}