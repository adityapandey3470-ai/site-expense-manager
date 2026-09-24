import { useState, useEffect } from "react";
import {useNavigate, useParams} from "react-router-dom";
import api, {getErrorMessage} from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import Select from "../components/Select";


const ENTRY_TYPES = ["CREDIT", "DEBIT"];

export default function NewLedgerEntry() {
    const navigate = useNavigate();
    const { id } = useParams();
    const { showToast } = useToast();
    const [sites, setSites] = useState([]);
    const [loadingExisting, setLoadingExisting] = useState(!!id);
    const [form, setForm] = useState({
        siteId: "",
        entryType: "CREDIT",
        amount: "",
        description: "",
        transactionDate: new Date().toISOString().slice(0, 10),
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        api.get("/sites").then((res) => setSites(res.data)).catch(() => {});
    }, []);

    useEffect(() => {
        if (!id) return;
        api
            .get(`/ledgers/${id}`)
            .then((res) => {
                const l = res.data;
                update("siteId", String(l.siteId));
                update("entryType", l.entryType);
                update("amount", String(l.amount));
                update("description", l.description || "");
                update("transactionDate", l.transactionDate);
            })
            .catch((err) => showToast(getErrorMessage(err), "error"))
            .finally(() => setLoadingExisting(false));
    }, [id]);

    function update(field, value) {
        setForm((f) => ({ ...f, [field]: value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (!form.siteId) {
            showToast("Please select a site.", "error");
            return;
        }
        setLoading(true);
        try {
            const payload = {
                siteId: Number(form.siteId),
                entryType: form.entryType,
                sourceType: "MANUAL",
                amount: Number(form.amount),
                description: form.description,
                transactionDate: form.transactionDate,
            };

            if (id) {
                await api.put(`/ledgers/${id}`, payload);
                showToast("Ledger entry updated", "success");
            } else {
                await api.post("/ledgers", payload);
                showToast("Ledger entry added", "success");
            }
            navigate("/summary");
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
                {id ? "Edit ledger entry" : "New ledger entry"}
            </p>
            <form onSubmit={handleSubmit} className="card">
                <Select
                    label="Site"
                    value={form.siteId}
                    onChange={(val) => update("siteId", val)}
                    options={sites.map((s) => String(s.id))}
                    formatLabel={(id) => sites.find((s) => String(s.id) === id)?.siteName || "Select site"}
                />

                <Select
                    label="Entry type"
                    value={form.entryType}
                    onChange={(val) => update("entryType", val)}
                    options={ENTRY_TYPES}
                />

                <div className="input-group">
                    <label>Amount (₹)</label>
                    <input
                        type="number"
                        min="1"
                        value={form.amount}
                        onChange={(e) => update("amount", e.target.value)}
                        required
                    />
                </div>

                <div className="input-group">
                    <label>Description</label>
                    <textarea
                        rows={3}
                        value={form.description}
                        onChange={(e) => update("description", e.target.value)}
                        placeholder="Reason for this entry"
                        required
                    />
                </div>

                <div className="input-group">
                    <label>Date</label>
                    <input
                        type="date"
                        value={form.transactionDate}
                        onChange={(e) => update("transactionDate", e.target.value)}
                        required
                    />
                </div>

                <button
                    type="submit"
                    className="btn btn-primary btn-block"
                    disabled={loading}
                    style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
                >
                    {loading ? <Spinner size={16} color="#fff" /> : id ? "Save changes" : "Add entry"}
                </button>
            </form>
        </div>
    );
}