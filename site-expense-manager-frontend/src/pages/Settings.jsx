import { useEffect, useState } from "react";
import api, { getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import { Settings as SettingsIcon } from "lucide-react";

export default function Settings() {
    const { showToast } = useToast();
    const [form, setForm] = useState({ foodRatePerPerson: "", payoutCycleDays: "" });
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        api
            .get("/settings")
            .then((res) => setForm({
                foodRatePerPerson: String(res.data.foodRatePerPerson),
                payoutCycleDays: String(res.data.payoutCycleDays),
            }))
            .catch((err) => showToast(getErrorMessage(err), "error"))
            .finally(() => setLoading(false));
    }, []);

    async function handleSubmit(e) {
        e.preventDefault();
        setSaving(true);
        try {
            await api.put("/settings", {
                foodRatePerPerson: Number(form.foodRatePerPerson),
                payoutCycleDays: Number(form.payoutCycleDays),
            });
            showToast("Settings updated", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return (
            <div className="screen" style={{ display: "flex", justifyContent: "center", padding: 40 }}>
                <Spinner size={22} color="var(--accent)" />
            </div>
        );
    }

    return (
        <div className="screen">
            <div style={{ textAlign: "center", marginBottom: 24 }}>
                <div style={{ width: 56, height: 56, borderRadius: 16, background: "var(--accent-bg)", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 12px" }}>
                    <SettingsIcon size={26} color="var(--accent)" strokeWidth={1.8} />
                </div>
                <p style={{ fontSize: 18, fontWeight: 500, margin: 0 }}>System settings</p>
            </div>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                <div className="input-group">
                    <label>Food rate per person (₹)</label>
                    <input
                        type="number"
                        min="1"
                        value={form.foodRatePerPerson}
                        onChange={(e) => setForm((f) => ({ ...f, foodRatePerPerson: e.target.value }))}
                        required
                    />
                </div>

                <div className="input-group">
                    <label>Payout cycle days</label>
                    <input
                        type="number"
                        min="1"
                        value={form.payoutCycleDays}
                        onChange={(e) => setForm((f) => ({ ...f, payoutCycleDays: e.target.value }))}
                        required
                    />
                </div>

                <button type="submit" className="btn btn-primary" disabled={saving} style={{ marginTop: 8 }}>
                    {saving ? <Spinner size={16} color="#fff" /> : "Save settings"}
                </button>
            </form>
        </div>
    );
}