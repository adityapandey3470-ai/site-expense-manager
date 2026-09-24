import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import ActionMenu from "../components/ActionMenu";
import { Building2, Trash2, Pencil, Plus, Power } from "lucide-react";

export default function SitesList() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const { showToast } = useToast();
    const [sites, setSites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyId, setBusyId] = useState(null);

    const canManage = user?.role === "OPERATIONS" || user?.role === "DIRECTOR";

    function load() {
        setLoading(true);
        api
            .get("/sites")
            .then((res) => setSites(res.data))
            .catch(() => showToast("Couldn't load sites.", "error"))
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        load();
    }, []);

    async function handleDelete(site) {
        const confirmed = window.confirm(`Delete "${site.siteName}"? This can be reversed later if needed.`);
        if (!confirmed) return;

        setBusyId(site.id);
        try {
            await api.delete(`/sites/${site.id}`);
            setSites((prev) => prev.filter((s) => s.id !== site.id));
            showToast("Site deleted", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    async function handleToggleActive(site) {
        setBusyId(site.id);
        try {
            const path = site.active ? "deactivate" : "activate";
            const res = await api.patch(`/sites/${site.id}/${path}`);
            setSites((prev) => prev.map((s) => (s.id === site.id ? res.data : s)));
            showToast(res.data.active ? "Site activated" : "Site deactivated", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="screen">
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
                <p style={{ fontSize: 20, fontWeight: 500, margin: 0 }}>Sites</p>
                {canManage && (
                    <Link
                        to="/sites/new"
                        className="btn btn-primary"
                        style={{ display: "flex", alignItems: "center", gap: 6, padding: "8px 12px", fontSize: 13 }}
                    >
                        <Plus size={16} />
                        New site
                    </Link>
                )}
            </div>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && sites.length === 0 && (
                <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                    <Building2 size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                    <p style={{ fontSize: 13, margin: 0 }}>No sites yet</p>
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {sites.map((s) => (
                    <div key={s.id} className="card">
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                            <div>
                                <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>
                                    {s.siteName} <span style={{ color: "var(--text-muted)", fontWeight: 400 }}>· {s.siteCode}</span>
                                </p>
                                <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                    {s.location} · PM: {s.projectManager}
                                </p>
                                <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "2px 0 0" }}>
                                    {s.teamSize} workers · Budget ₹{Number(s.budget).toLocaleString("en-IN")}
                                </p>
                            </div>
                            <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                                <span className={s.active ? "badge badge-approved" : "badge badge-rejected"}>
                                    {s.active ? "Active" : "Inactive"}
                                </span>
                                <ActionMenu
                                    actions={
                                        canManage
                                            ? [
                                                { icon: <Pencil size={14} />, label: "Edit", onClick: () => navigate(`/sites/${s.id}/edit`) },
                                                { icon: <Power size={14} />, label: s.active ? "Deactivate" : "Activate", onClick: () => handleToggleActive(s) },
                                                { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDelete(s) },
                                            ]
                                            : []
                                    }
                                />
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}