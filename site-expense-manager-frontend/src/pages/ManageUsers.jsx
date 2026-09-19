import { useEffect, useState } from "react";
import api from "../api/client";
import { getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import Select from "../components/Select";
import { Users, Plus, X, KeyRound, Power, Trash2 } from "lucide-react";
import ActionMenu from "../components/ActionMenu";

const ROLES = ["SUPERVISOR", "OPERATIONS", "ACCOUNTS", "DIRECTOR"];

export default function ManageUsers() {
    const { showToast } = useToast();
    const [users, setUsers] = useState([]);
    const [sites, setSites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyId, setBusyId] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [creating, setCreating] = useState(false);
    const [form, setForm] = useState({ fullName: "", username: "", password: "", role: "SUPERVISOR", siteId: "" });

    function update(field, value) {
        setForm((f) => ({ ...f, [field]: value }));
    }

    async function load() {
        setLoading(true);
        try {
            const [usersRes, sitesRes] = await Promise.all([
                api.get("/admin/users"),
                api.get("/sites"),
            ]);
            setUsers(usersRes.data);
            setSites(sitesRes.data);
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    async function handleCreate(e) {
        e.preventDefault();
        setCreating(true);
        try {
            const payload = {
                fullName: form.fullName,
                username: form.username.trim(),
                password: form.password,
                role: form.role,
                siteId: form.role === "SUPERVISOR" ? Number(form.siteId) : null,
            };
            await api.post("/admin/users", payload);
            showToast("User created successfully", "success");
            setForm({ fullName: "", username: "", password: "", role: "SUPERVISOR", siteId: "" });
            setShowForm(false);
            await load();
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setCreating(false);
        }
    }

    async function handleResetPassword(userId) {
        const newPassword = window.prompt("Enter a new password for this user (min 6 characters):");
        if (!newPassword) return;
        if (newPassword.length < 6) {
            showToast("Password must be at least 6 characters.", "error");
            return;
        }

        setBusyId(userId);
        try {
            await api.patch(`/admin/users/${userId}/reset-password`, { newPassword });
            showToast("Password reset — share the new password with the user.", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    async function handleToggleActive(user) {
        let confirmPassword = null;

        if (user.role === "DIRECTOR") {
            confirmPassword = window.prompt(
                "You're deactivating a Director account. Enter YOUR password to confirm:"
            );
            if (!confirmPassword) return; // cancelled
        }

        setBusyId(user.id);
        try {
            const res = await api.patch(`/admin/users/${user.id}/toggle-active`, { confirmPassword });
            setUsers((prev) => prev.map((u) => (u.id === user.id ? res.data : u)));
            showToast(res.data.active ? "User activated" : "User deactivated", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    async function handleDeleteUser(u) {
        const confirmed = window.confirm(`Delete "${u.fullName}"? This cannot be undone from the app.`);
        if (!confirmed) return;

        setBusyId(u.id);
        try {
            await api.delete(`/admin/users/${u.id}`);
            setUsers((prev) => prev.filter((x) => x.id !== u.id));
            showToast("User deleted", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="screen">
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
                <p style={{ fontSize: 20, fontWeight: 500, margin: 0 }}>Manage users</p>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm((s) => !s)}
                    style={{ display: "flex", alignItems: "center", gap: 6, padding: "8px 12px" }}
                >
                    {showForm ? <X size={16} /> : <Plus size={16} />}
                    {showForm ? "Cancel" : "New user"}
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleCreate} className="card" style={{ marginBottom: 16 }}>
                    <div className="input-group">
                        <label>Full name</label>
                        <input
                            value={form.fullName}
                            onChange={(e) => update("fullName", e.target.value)}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <label>Username</label>
                        <input
                            value={form.username}
                            onChange={(e) => update("username", e.target.value)}
                            minLength={4}
                            required
                        />
                    </div>

                    <div className="input-group">
                        <label>Temporary password</label>
                        <input
                            type="text"
                            value={form.password}
                            onChange={(e) => update("password", e.target.value)}
                            minLength={6}
                            required
                        />
                    </div>

                    <Select
                        label="Role"
                        value={form.role}
                        onChange={(val) => update("role", val)}
                        options={ROLES}
                    />

                    {form.role === "SUPERVISOR" && (
                        <Select
                            label="Site"
                            value={form.siteId}
                            onChange={(val) => update("siteId", val)}
                            options={sites.map((s) => String(s.id))}
                            formatLabel={(id) => sites.find((s) => String(s.id) === id)?.siteName || "Select site"}
                        />
                    )}

                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={creating}
                        style={{ width: "100%", marginTop: 8, display: "flex", alignItems: "center", justifyContent: "center", gap: 6 }}
                    >
                        {creating ? <Spinner size={14} color="#fff" /> : "Create user"}
                    </button>
                </form>
            )}

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && users.length === 0 && (
                <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                    <Users size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                    <p style={{ fontSize: 13, margin: 0 }}>No users yet</p>
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {users.map((u) => (
                    <div key={u.id} className="card">
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                            <div>
                                <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>{u.fullName}</p>
                                <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                    @{u.username} · {u.role}
                                    {u.siteName ? ` · ${u.siteName}` : ""}
                                </p>
                            </div>
                            <span className={u.active ? "badge badge-approved" : "badge badge-rejected"}>
                                {u.active ? "Active" : "Inactive"}
                            </span>
                        </div>
                        <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 8 }}>
                            <ActionMenu
                                actions={[
                                    { icon: <KeyRound size={14} />, label: "Reset password", onClick: () => handleResetPassword(u.id) },
                                    { icon: <Power size={14} />, label: u.active ? "Deactivate" : "Activate", onClick: () => handleToggleActive(u) },
                                    { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDeleteUser(u) },
                                ]}
                            />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}