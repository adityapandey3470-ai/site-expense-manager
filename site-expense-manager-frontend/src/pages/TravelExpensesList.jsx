import { useEffect, useState } from "react";
import api, { getErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import StatusBadge from "../components/StatusBadge";
import { useNavigate } from "react-router-dom";
import { Car, Pencil, Trash2 } from "lucide-react";
import ActionMenu from "../components/ActionMenu";

export default function TravelExpensesList() {
    const { user } = useAuth();
    const { showToast } = useToast();
    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyId, setBusyId] = useState(null);
    const navigate = useNavigate();

    const canApprove = user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";
    const canReject =
        user?.role === "OPERATIONS" || user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";

    async function load() {
        setLoading(true);
        try {
            const url = user?.siteId ? `/travel-expenses/site/${user.siteId}` : "/travel-expenses";
            const res = await api.get(url);
            setExpenses(res.data);
        } catch {
            showToast("Couldn't load travel expenses.", "error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    async function act(id, action) {
        setBusyId(id);
        try {
            await api.patch(`/travel-expenses/${id}/${action}`);
            showToast(`Travel expense ${action}d successfully`, "success");
            await load();
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    async function handleDelete(id) {
        const confirmed = window.confirm("Delete this travel expense?");
        if (!confirmed) return;

        setBusyId(id);
        try {
            await api.delete(`/travel-expenses/${id}`);
            setExpenses((prev) => prev.filter((t) => t.id !== id));
            showToast("Travel expense deleted", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Travel expenses</p>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && expenses.length === 0 && (
                <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                    <Car size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                    <p style={{ fontSize: 13, margin: 0 }}>No travel expenses yet</p>
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {expenses.map((t) => {
                    const canEdit = t.travelStatus === "PENDING" && t.employeeName === user?.fullName;

                    return (
                        <div key={t.id} className="card">
                            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                                <div>
                                    <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>
                                        {t.employeeName} · {t.fromLocation} → {t.toLocation}
                                    </p>
                                    <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                        {t.travelMode} · {t.travelDate} · {t.travelPurpose}
                                    </p>
                                </div>
                                <div style={{ display: "flex", alignItems: "flex-start", gap: 4 }}>
                                    <div style={{ textAlign: "right" }}>
                                        <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>
                                            ₹{Number(t.travelCost).toLocaleString("en-IN")}
                                        </p>
                                        <div style={{ marginTop: 4 }}>
                                            <StatusBadge status={t.travelStatus} />
                                        </div>
                                    </div>
                                    <ActionMenu
                                        actions={
                                            canEdit
                                                ? [
                                                    { icon: <Pencil size={14} />, label: "Edit", onClick: () => navigate(`/travel/${t.id}/edit`) },
                                                    { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDelete(t.id) },
                                                ]
                                                : []
                                        }
                                    />
                                </div>
                            </div>

                            <div style={{ display: "flex", gap: 8, marginTop: 8, alignItems: "center" }}>
                                <span className={t.billAttached ? "badge badge-approved" : "badge badge-pending"}>
                                    {t.billAttached ? "Bill attached" : "No bill"}
                                </span>
                                {t.billUrl && (
                                    <a href={t.billUrl} target="_blank" rel="noreferrer" style={{ fontSize: 12, color: "var(--accent)" }}>
                                        View bill
                                    </a>
                                )}
                            </div>

                            {t.travelStatus === "PENDING" && (canApprove || canReject) && (
                                <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
                                    {canApprove && (
                                        <button
                                            className="btn btn-primary"
                                            disabled={busyId === t.id}
                                            onClick={() => act(t.id, "approve")}
                                            style={{ display: "flex", alignItems: "center", gap: 6 }}
                                        >
                                            {busyId === t.id ? <Spinner size={14} color="#fff" /> : "Approve"}
                                        </button>
                                    )}

                                    {canReject && (
                                        <button
                                            className="btn"
                                            disabled={busyId === t.id}
                                            onClick={() => act(t.id, "reject")}
                                            style={{ display: "flex", alignItems: "center", gap: 6 }}
                                        >
                                            {busyId === t.id ? <Spinner size={14} color="var(--text-primary)" /> : "Reject"}
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}