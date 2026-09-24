import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect } from "react";
import api, { getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";

export default function NewSite() {
    const navigate = useNavigate();
    const { id } = useParams(); // present when editing an existing site
    const { showToast } = useToast();

    const [form, setForm] = useState({
        siteName: "",
        siteCode: "",
        location: "",
        projectManager: "",
        budget: "",
        startDate: "",
        endDate: "",
        teamSize: "",
    });
    const [loading, setLoading] = useState(false);
    const [loadingExisting, setLoadingExisting] = useState(!!id);

    function update(field, value) {
        setForm((f) => ({ ...f, [field]: value }));
    }

    useEffect(() => {
        if (!id) return;
        api
            .get(`/sites/${id}`)
            .then((res) => {
                const s = res.data;
                setForm({
                    siteName: s.siteName || "",
                    siteCode: s.siteCode || "",
                    location: s.location || "",
                    projectManager: s.projectManager || "",
                    budget: String(s.budget ?? ""),
                    startDate: s.startDate || "",
                    endDate: s.endDate || "",
                    teamSize: String(s.teamSize ?? ""),
                });
            })
            .catch((err) => showToast(getErrorMessage(err), "error"))
            .finally(() => setLoadingExisting(false));
    }, [id]);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                ...form,
                budget: Number(form.budget),
                teamSize: Number(form.teamSize),
            };

            if (id) {
                await api.put(`/sites/${id}`, payload);
                showToast("Site updated", "success");
            } else {
                await api.post("/sites", payload);
                showToast("Site created", "success");
            }
            navigate("/sites");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setLoading(false);
        }
    }

    if (loadingExisting) {
        return (
            <div className="screen" style={{ display: "flex", justifyContent: "center", padding: 40 }}>
                <Spinner size={22} color="var(--accent)" />
            </div>
        );
    }

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>
                {id ? "Edit site" : "New site"}
            </p>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                <div className="input-group">
                    <label>Site name</label>
                    <input value={form.siteName} onChange={(e) => update("siteName", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Site code</label>
                    <input value={form.siteCode} onChange={(e) => update("siteCode", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Location</label>
                    <input value={form.location} onChange={(e) => update("location", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Project manager</label>
                    <input value={form.projectManager} onChange={(e) => update("projectManager", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Budget (₹)</label>
                    <input type="number" min="1" value={form.budget} onChange={(e) => update("budget", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Team size</label>
                    <input type="number" min="1" value={form.teamSize} onChange={(e) => update("teamSize", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>Start date</label>
                    <input type="date" value={form.startDate} onChange={(e) => update("startDate", e.target.value)} required />
                </div>

                <div className="input-group">
                    <label>End date</label>
                    <input type="date" value={form.endDate} onChange={(e) => update("endDate", e.target.value)} required />
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading} style={{ marginTop: 8 }}>
                    {loading ? <Spinner size={16} color="#fff" /> : id ? "Save changes" : "Create site"}
                </button>
            </form>
        </div>
    );
}