import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import { KeyRound } from "lucide-react";

export default function ChangePassword() {
    const navigate = useNavigate();
    const { showToast } = useToast();
    const [form, setForm] = useState({ oldPassword: "", newPassword: "", confirmPassword: "" });
    const [loading, setLoading] = useState(false);

    function update(field, value) {
        setForm((f) => ({ ...f, [field]: value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();

        if (form.newPassword !== form.confirmPassword) {
            showToast("New passwords don't match.", "error");
            return;
        }
        if (form.newPassword.length < 6) {
            showToast("New password must be at least 6 characters.", "error");
            return;
        }

        setLoading(true);
        try {
            await api.patch("/auth/change-password", {
                oldPassword: form.oldPassword,
                newPassword: form.newPassword,
            });
            showToast("Password changed successfully", "success");
            navigate("/more");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="screen">
            <div style={{ textAlign: "center", marginBottom: 24 }}>
                <div
                    style={{
                        width: 56,
                        height: 56,
                        borderRadius: 16,
                        background: "var(--accent-bg)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        margin: "0 auto 12px",
                    }}
                >
                    <KeyRound size={26} color="var(--accent)" strokeWidth={1.8} />
                </div>
                <p style={{ fontSize: 18, fontWeight: 500, margin: 0 }}>Change password</p>
            </div>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                <div className="input-group">
                    <label>Current password</label>
                    <input
                        type="password"
                        value={form.oldPassword}
                        onChange={(e) => update("oldPassword", e.target.value)}
                        required
                    />
                </div>

                <div className="input-group">
                    <label>New password</label>
                    <input
                        type="password"
                        value={form.newPassword}
                        onChange={(e) => update("newPassword", e.target.value)}
                        minLength={6}
                        required
                    />
                </div>

                <div className="input-group">
                    <label>Confirm new password</label>
                    <input
                        type="password"
                        value={form.confirmPassword}
                        onChange={(e) => update("confirmPassword", e.target.value)}
                        minLength={6}
                        required
                    />
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading} style={{ marginTop: 8 }}>
                    {loading ? <Spinner size={16} color="#fff" /> : "Update password"}
                </button>
            </form>
        </div>
    );
}