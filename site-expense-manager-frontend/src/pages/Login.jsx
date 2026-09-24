import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Spinner from "../components/Spinner";
import BuildingSkyscraperIcon from "../components/icons/BuildingSkyscraperIcon";
import { Eye, EyeOff } from "lucide-react";
import {getErrorMessage} from "../api/client.js";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
        await login(username.trim(), password);
      navigate("/");
    } catch (err) {
      setError(
          getErrorMessage(err)
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      style={{
        flex: 1,
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        padding: "24px",
      }}
    >
      <div style={{ textAlign: "center", marginBottom: 32 }}>
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
              <BuildingSkyscraperIcon size={26} color="var(--accent)" strokeWidth={1.8} />
          </div>
        <p style={{ fontSize: 20, fontWeight: 500, margin: 0 }}>Site expense manager</p>
        <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "4px 0 0" }}>
          Sign in to your account
        </p>
      </div>

      <form onSubmit={handleSubmit} className="card">
        <div className="input-group">
          <label>Username</label>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter your username"
            autoComplete="username"
            required
          />
        </div>
          <div className="input-group">
              <label>Password</label>
              <div style={{ position: "relative" }}>
                  <input
                      type={showPassword ? "text" : "password"}
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      placeholder="Enter your password"
                      autoComplete="current-password"
                      required
                      style={{ paddingRight: 40 }}
                  />
                  <button
                      type="button"
                      onClick={() => setShowPassword((s) => !s)}
                      style={{
                          position: "absolute",
                          right: 10,
                          top: "50%",
                          transform: "translateY(-50%)",
                          background: "none",
                          border: "none",
                          padding: 4,
                          cursor: "pointer",
                          display: "flex",
                          alignItems: "center",
                      }}
                  >
                      {showPassword ? (
                          <EyeOff size={18} color="var(--text-muted)" />
                      ) : (
                          <Eye size={18} color="var(--text-muted)" />
                      )}
                  </button>
              </div>
          </div>

        {error && <p className="error-text">{String(error)}</p>}
          <button type="submit" className="btn btn-primary btn-block"
                  disabled={loading}
                  style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}>
              {loading ? <Spinner size={16} color="#fff" /> : "Sign in"}
          </button>
      </form>
    </div>
  );
}
