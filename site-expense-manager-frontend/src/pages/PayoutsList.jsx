import { useEffect, useState } from "react";
import api, {getErrorMessage} from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import { Wallet } from "lucide-react";
import { useAuth } from "../context/AuthContext";



export default function PayoutsList() {
    const { showToast } = useToast();
    const [payouts, setPayouts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyId, setBusyId] = useState(null);
    const [paidToday, setPaidToday] = useState({});
    const { user } = useAuth();

    async function load() {
        setLoading(true);
        try {
            const res = user?.siteId
                ? await api.get(`/payouts/due/site/${user.siteId}`)
                : await api.get("/payouts/due");
            setPayouts(user?.siteId ? [res.data] : res.data);
        } catch {
            showToast("Couldn't load payout list.", "error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    async function markPaid(siteId) {
        setBusyId(siteId);
        try {
            await api.post(`/payouts/site/${siteId}/pay`);
            showToast("Marked as paid — ledger updated", "success");
            await load();
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Payouts due</p>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && payouts.length === 0 && (
                <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                    <Wallet size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                    <p style={{ fontSize: 13, margin: 0 }}>No sites are due for payout</p>
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {payouts.map((p) => (
                    <div key={p.siteId} className="card">
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                            <div>
                                <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>{p.siteName}</p>
                                <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                    {p.teamSize} workers ·{" "}
                                    <span style={{ color: p.currentBalance < 0 ? "var(--danger)" : "inherit" }}>
                    Balance: {p.currentBalance < 0 ? "-" : ""}₹
                                        {Math.abs(p.currentBalance).toLocaleString("en-IN")}
                  </span>
                                </p>
                            </div>
                            <p style={{ fontSize: 16, fontWeight: 600, margin: 0 }}>
                                ₹{Number(p.amountDue).toLocaleString("en-IN")}
                            </p>
                        </div>

                        {p.alreadyPaidToday ? (
                            <div style={{ marginTop: 12, width: "100%", display: "flex", alignItems: "center", justifyContent: "center", gap: 6, padding: "10px", borderRadius: 8, background: "var(--accent-bg)", color: "var(--accent)", fontSize: 13, fontWeight: 500 }}>
                                ✓ Paid today
                            </div>
                        ) : !p.isPayoutDayToday ? (
                            <div style={{ marginTop: 12, width: "100%", textAlign: "center", padding: "10px", borderRadius: 8, background: "var(--bg)", color: "var(--text-muted)", fontSize: 12 }}>
                                Payouts run Mon / Wed / Fri only
                            </div>
                            ) : Number(p.amountDue) <= 0 ? (
                            <div style={{ marginTop: 12, width: "100%", textAlign: "center", padding: "10px", borderRadius: 8, background: "var(--bg)", color: "var(--text-muted)", fontSize: 12 }}>
                        Nothing due — site already has enough balance
                            </div>
                        ) : (
                            <button
                                className="btn btn-primary"
                                disabled={busyId === p.siteId}
                                onClick={() => markPaid(p.siteId)}
                                style={{ marginTop: 12, width: "100%", display: "flex", alignItems: "center", justifyContent: "center", gap: 6 }}
                            >
                                {busyId === p.siteId ? <Spinner size={14} color="#fff" /> : "Mark paid"}
                            </button>
                        )}

                    </div>
                ))}
            </div>
        </div>
    );
}