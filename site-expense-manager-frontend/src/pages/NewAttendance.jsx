import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, {getErrorMessage} from "../api/client";
import { useAuth } from "../context/AuthContext";
import Spinner from "../components/Spinner";

export default function NewAttendance() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [presentCount, setPresentCount] = useState("");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await api.post("/attendances", {
        siteId: user.siteId,
        attendanceDate: date,
        presentCount: Number(presentCount),
      });
      navigate("/");
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="screen">
      <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Mark attendance</p>
      <form onSubmit={handleSubmit} className="card">
        <div className="input-group">
          <label>Date</label>
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
        </div>
        <div className="input-group">
          <label>Workers present</label>
          <input
            type="number"
            min="1"
            value={presentCount}
            onChange={(e) => setPresentCount(e.target.value)}
            placeholder="Enter number of workers"
            required
          />
        </div>
        {error && <p className="error-text">{String(error)}</p>}
        <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
            style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
        >
          {loading ? <Spinner size={16} color="#fff" /> : "Save attendance"}
        </button>
      </form>
    </div>
  );
}
