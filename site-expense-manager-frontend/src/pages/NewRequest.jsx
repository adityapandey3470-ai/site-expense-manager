import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import api, {getErrorMessage} from "../api/client";
import { useAuth } from "../context/AuthContext";
import Select from "../components/Select";
import Spinner from "../components/Spinner";


const REQUEST_TYPES = ["MATERIAL", "EMERGENCY", "TRAVEL_EXPENSE", "ADVANCE", "REIMBURSEMENT", "OTHER"];

export default function NewRequest() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loadingExisting, setLoadingExisting] = useState(!!id);
  const [form, setForm] = useState({
    requestType: "MATERIAL",
    description: "",
    amount: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const needsAmount = form.requestType === "EMERGENCY" || form.requestType === "MATERIAL" || form.requestType === "ADVANCE" || form.requestType === "REIMBURSEMENT";

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  useEffect(() => {
    if (!id) return;
    api
        .get(`/requests/${id}`)
        .then((res) => {
          const r = res.data;
          setForm({
            requestType: r.requestType,
            description: r.description,
            amount: r.amount ? String(r.amount) : "",
          });
        })
        .catch((err) => setError(getErrorMessage(err)))
        .finally(() => setLoadingExisting(false));
  }, [id]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = {
        siteId: user.siteId,
        requestType: form.requestType,
        description: form.description,
        amount: needsAmount ? Number(form.amount) : null,
      };

      if (id) {
        await api.put(`/requests/${id}`, payload);
      } else {
        await api.post("/requests", payload);
      }
      navigate("/requests");
    } catch (err) {
      setError(getErrorMessage(err));
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
      <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>New request</p>

      <form onSubmit={handleSubmit} className="card">
        <Select
            label="Request type"
            value={form.requestType}
            onChange={(val) => update("requestType", val)}
            options={REQUEST_TYPES}
            formatLabel={(t) => t.replace("_", " ")}
        />

        <div className="input-group">
          <label>Description</label>
          <textarea
            rows={3}
            value={form.description}
            onChange={(e) => update("description", e.target.value)}
            placeholder="What's this request for?"
            required
          />
        </div>

        {needsAmount && (
          <div className="input-group">
            <label>Amount (₹)</label>
            <input
              type="number"
              min="1"
              value={form.amount}
              onChange={(e) => update("amount", e.target.value)}
              placeholder="Enter amount"
              required
            />
          </div>
        )}

        {error && <p className="error-text">{String(error)}</p>}

        <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
            style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
        >
          {loading ? <Spinner size={16} color="#fff" /> : "Submit request"}
        </button>
      </form>
    </div>
  );
}
