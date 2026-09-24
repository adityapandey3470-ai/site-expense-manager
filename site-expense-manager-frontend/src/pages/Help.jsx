import { Link } from "react-router-dom";
import { ChevronLeft, CheckSquare, PlusCircle, ArrowRightCircle, CheckCircle2 } from "lucide-react";

const ROLE_GUIDES = [
    {
        role: "Supervisor",
        points: [
            "Mark daily attendance for your site",
            "Submit new requests (Material, Emergency, Travel, etc.)",
            "Track the status of your submitted requests",
        ],
    },
    {
        role: "Operations",
        points: [
            "Forward Emergency/Material requests to Accounts or Director",
            "Reject requests that don't look right",
            "View site balances and ledger history",
        ],
    },
    {
        role: "Accounts",
        points: [
            "Give final approval on forwarded requests",
            "Manage ledger entries",
            "Process payouts",
        ],
    },
    {
        role: "Director",
        points: [
            "Full access — manage sites, users, and all approvals",
            "Create new Operations/Accounts/Director accounts",
            "Approve or reject any pending request",
        ],
    },
];

export default function Help() {
    return (
        <div className="screen">
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 16 }}>
                <Link to="/more" style={{ display: "flex" }}>
                    <ChevronLeft size={20} color="var(--text-primary)" />
                </Link>
                <p style={{ fontSize: 20, fontWeight: 500, margin: 0 }}>How to use</p>
            </div>

            <div className="card" style={{ marginBottom: 12 }}>
                <p style={{ fontSize: 14, fontWeight: 500, margin: "0 0 10px" }}>Quick start</p>
                <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                    <div style={{ display: "flex", gap: 10, alignItems: "flex-start" }}>
                        <CheckSquare size={18} color="var(--accent)" style={{ marginTop: 1, flexShrink: 0 }} />
                        <p style={{ fontSize: 13, margin: 0, color: "var(--text-secondary)" }}>
                            Mark attendance every day from the Home screen
                        </p>
                    </div>
                    <div style={{ display: "flex", gap: 10, alignItems: "flex-start" }}>
                        <PlusCircle size={18} color="var(--accent)" style={{ marginTop: 1, flexShrink: 0 }} />
                        <p style={{ fontSize: 13, margin: 0, color: "var(--text-secondary)" }}>
                            Tap "Add" to submit a new request or travel expense
                        </p>
                    </div>
                    <div style={{ display: "flex", gap: 10, alignItems: "flex-start" }}>
                        <ArrowRightCircle size={18} color="var(--accent)" style={{ marginTop: 1, flexShrink: 0 }} />
                        <p style={{ fontSize: 13, margin: 0, color: "var(--text-secondary)" }}>
                            Requests move through Forward → Approve based on your organization's rules
                        </p>
                    </div>
                    <div style={{ display: "flex", gap: 10, alignItems: "flex-start" }}>
                        <CheckCircle2 size={18} color="var(--accent)" style={{ marginTop: 1, flexShrink: 0 }} />
                        <p style={{ fontSize: 13, margin: 0, color: "var(--text-secondary)" }}>
                            Once approved, the amount is automatically reflected in the site ledger
                        </p>
                    </div>
                </div>
            </div>

            <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "0 0 8px" }}>
                What each role can do
            </p>

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {ROLE_GUIDES.map((g) => (
                    <div key={g.role} className="card">
                        <p style={{ fontSize: 14, fontWeight: 500, margin: "0 0 8px" }}>{g.role}</p>
                        <ul style={{ margin: 0, paddingLeft: 18, color: "var(--text-secondary)", fontSize: 13 }}>
                            {g.points.map((p, i) => (
                                <li key={i} style={{ marginBottom: 4 }}>{p}</li>
                            ))}
                        </ul>
                    </div>
                ))}
            </div>
        </div>
    );
}