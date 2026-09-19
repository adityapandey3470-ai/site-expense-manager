import { useEffect, useState } from "react";
import api from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";

export default function LedgerSummary() {
    const { showToast } = useToast();
    const [balances, setBalances] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api
            .get("/ledgers/balance")
            .then((res) => setBalances(res.data))
            .catch(() => showToast("Couldn't load ledger summary.", "error"))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Ledger summary — all sites</p>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {balances.map((b) => (
                    <div
                        key={b.siteId}
                        className="card"
                        style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}
                    >
                        <div>
                            <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>{b.siteName}</p>
                            <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                {b.teamSize} workers
                            </p>
                        </div>
                        <p
                            style={{
                                fontSize: 16,
                                fontWeight: 500,
                                margin: 0,
                                color: b.negative ? "var(--danger)" : "var(--text-primary)",
                            }}
                        >
                            {b.negative ? "-" : ""}₹{Math.abs(b.balance).toLocaleString("en-IN")}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
}