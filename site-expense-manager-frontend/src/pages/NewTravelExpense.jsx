import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Select from "../components/Select";
import Spinner from "../components/Spinner";
import api, { uploadFile, getErrorMessage } from "../api/client";


const MODES = ["BIKE", "CAR", "BUS", "TRAIN", "FLIGHT", "AUTO", "CAB", "OTHER"];

export default function NewTravelExpense() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    employeeName: "",
    employeeId: "",
    travelDate: new Date().toISOString().slice(0, 10),
    fromLocation: "",
    toLocation: "",
    travelMode: "CAR",
    travelCost: "",
    travelPurpose: "",
    billAttached: false,
    billUrl: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const { id } = useParams();
  const [loadingExisting, setLoadingExisting] = useState(!!id);

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }
  useEffect(() => {
    if (!id) return;
    api
        .get(`/travel-expenses/${id}`)
        .then((res) => {
          const t = res.data;
          setForm({
            employeeName: t.employeeName,
            employeeId: t.employeeId,
            travelDate: t.travelDate,
            fromLocation: t.fromLocation,
            toLocation: t.toLocation,
            travelMode: t.travelMode,
            travelCost: String(t.travelCost),
            travelPurpose: t.travelPurpose,
            remarks: t.remarks || "",
            billAttached: t.billAttached,
            billUrl: t.billUrl || "",
          });
        })
        .catch((err) => setError(getErrorMessage(err)))
        .finally(() => setLoadingExisting(false));
  }, [id]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    if (!form.billUrl) {
      setError("Please upload the bill photo/PDF before submitting.");
      setLoading(false);
      return;
    }
    try {
      if (id) {
        await api.put(`/travel-expenses/${id}`, {
          siteId: user.siteId,
          ...form,
          travelCost: Number(form.travelCost),
          billAttached: Boolean(form.billUrl),
        });
      } else {
        await api.post("/travel-expenses", {
          siteId: user.siteId,
          ...form,
          travelCost: Number(form.travelCost),
          billAttached: Boolean(form.billUrl),
        });
      }
      navigate("/");
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
      <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Travel expense</p>
      <form onSubmit={handleSubmit} className="card">
        <div className="input-group">
          <label>Employee name</label>
          <input value={form.employeeName} onChange={(e) => update("employeeName", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>Employee ID</label>
          <input value={form.employeeId} onChange={(e) => update("employeeId", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>Travel Date</label>
            <input type="date" value={form.travelDate} onChange={(e) => update("travelDate", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>From</label>
          <input value={form.fromLocation} onChange={(e) => update("fromLocation", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>To</label>
          <input value={form.toLocation} onChange={(e) => update("toLocation", e.target.value)} required />
        </div>
          <Select
              label ="Mode"
            value={form.travelMode}
            onChange={(val) => update("travelMode", val)}
            options={MODES}
          />

        <div className="input-group">
          <label>Cost (₹)</label>
          <input type="number" min="1" value={form.travelCost} onChange={(e) => update("travelCost", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>Purpose</label>
          <input value={form.travelPurpose} onChange={(e) => update("travelPurpose", e.target.value)} required />
        </div>
        <div className="input-group">
          <label>Bill / invoice</label>
          <input
              type="file"
              accept="image/*,application/pdf"
              capture="environment"
              onChange={async (e) => {
                const file = e.target.files[0];
                if (!file) return;
                setUploading(true);
                setError("");
                try {
                  const url = await uploadFile(file);
                  update("billUrl", url);
                  update("billAttached", true);
                } catch (err) {
                  setError("Couldn't upload the file. Try again.");
                } finally {
                  setUploading(false);
                }
              }}
          />
          {uploading && (
              <p style={{ fontSize: 12, color: "var(--text-secondary)", marginTop: 6 }}>Uploading…</p>
          )}
          {form.billUrl && !uploading && (
              <div style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 8 }}>
                <img
                    src={form.billUrl}
                    alt="Bill preview"
                    style={{ width: 48, height: 48, objectFit: "cover", borderRadius: 6, border: "1px solid var(--border)" }}
                    onError={(e) => (e.target.style.display = "none")}
                />
                <span style={{ fontSize: 12, color: "var(--success)" }}>Bill uploaded ✓</span>
              </div>
          )}
        </div>

        {error && <p className="error-text">{String(error)}</p>}

        <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
            style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
        >
          {loading ? <Spinner size={16} color="#fff" /> : "Submit"}
        </button>
      </form>
    </div>
  );
}
