import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Receipt as ReceiptIcon } from "lucide-react";
import { Doughnut, Bar } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, CategoryScale, LinearScale, BarElement } from "chart.js";
import { useToast } from "../context/ToastContext.jsx";
import api, { getErrorMessage } from "../api/client";
import { Soup, Car, Wallet, Receipt, ClipboardList, Banknote, Pencil, Trash2 } from "lucide-react";
import ActionMenu from "../components/ActionMenu";
ChartJS.register(ArcElement, Tooltip, CategoryScale, LinearScale, BarElement);


const COLORS = {
  MATERIAL: "#eb6834",
  EMERGENCY: "#e34948",
  TRAVEL_EXPENSE: "#1baf7a",
  MANUAL: "#2a78d6",
  PAYOUT: "#2a78d6",
  OTHER: "#898781",
};
const ICON_META = {
  TRAVEL_EXPENSE: { Icon: Car, bg: "#E6F7EE", color: "#1A9C5B" },
  MANUAL: { Icon: Wallet, bg: "#E6F1FB", color: "#2A78D6" },
  ATTENDANCE: { Icon: Soup, bg: "#FBEAE0", color: "#B65B22" },
  REQUEST: { Icon: ClipboardList, bg: "#FCEBEB", color: "#D03B3B" },
  PAYOUT: { Icon: Banknote, bg: "#E6F1FB", color: "#2A78D6" },
  OTHER: { Icon: Receipt, bg: "#F1F1F0", color: "#6B7280" },
};

export default function Summary() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [ledgers, setLedgers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        const res = user.siteId
            ? await api.get(`/ledgers/site/${user.siteId}`)
            : await api.get("/ledgers");
        setLedgers(res.data);
      } catch (err) {
        showToast("Failed to load ledger entries.", "error");
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [user]);

  const canManage = user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";

  async function handleDelete(l) {
    const confirmed = window.confirm("Delete this ledger entry? This will affect the site balance.");
    if (!confirmed) return;

    setBusyId(l.ledgerId);
    try {
      await api.patch(`/ledgers/${l.ledgerId}`);
      setLedgers((prev) => prev.filter((x) => x.ledgerId !== l.ledgerId));
      showToast("Ledger entry deleted", "success");
    } catch (err) {
      showToast(getErrorMessage(err), "error");
    } finally {
      setBusyId(null);
    }
  }

  const debits = ledgers.filter((l) => l.entryType === "DEBIT");
  const credits = ledgers.filter((l) => l.entryType === "CREDIT");
  const totalOutflow = debits.reduce((sum, l) => sum + Number(l.amount), 0);
  const totalInflow = credits.reduce((sum, l) => sum + Number(l.amount), 0);
  const netBalance = totalInflow - totalOutflow;
  const monthlyTotals = (() => {
    const now = new Date();
    const months = [];
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push({
        key: `${d.getFullYear()}-${d.getMonth()}`,
        label: d.toLocaleDateString("en-IN", { month: "short" }),
        total: 0,
      });
    }

    debits.forEach((l) => {
      const d = new Date(l.transactionDate);
      const key = `${d.getFullYear()}-${d.getMonth()}`;
      const match = months.find((m) => m.key === key);
      if (match) match.total += Number(l.amount);
    });

    return months;
  })();


  const bySource = debits.reduce((acc, l) => {
    acc[l.sourceType] = (acc[l.sourceType] || 0) + Number(l.amount);
    return acc;
  }, {});

  const labels = Object.keys(bySource);
  const values = labels.map((k) => bySource[k]);
  const colors = labels.map((k) => COLORS[k] || COLORS.OTHER);

  return (
      <div className="screen">
        <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Summary</p>

        <div className="card" style={{ marginBottom: 12 }}>
          <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "0 0 4px" }}>
            Net balance
          </p>
          <p
              style={{
                fontSize: 24,
                fontWeight: 500,
                margin: 0,
                color: netBalance < 0 ? "var(--danger)" : "var(--text-primary)",
              }}
          >
            {netBalance < 0 ? "-" : ""}₹{Math.abs(netBalance).toLocaleString("en-IN")}
          </p>
          <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
            {ledgers.length} total transactions
          </p>
        </div>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8, marginBottom: 12 }}>
          <div className="card">
            <p style={{ fontSize: 12, color: "var(--text-secondary)", margin: "0 0 4px" }}>
              Total inflow
            </p>
            <p style={{ fontSize: 18, fontWeight: 500, margin: 0, color: "var(--success)" }}>
              +₹{totalInflow.toLocaleString("en-IN")}
            </p>
            <p style={{ fontSize: 11, color: "var(--text-muted)", margin: "4px 0 0" }}>
              {credits.length} entries
            </p>
          </div>

          <div className="card">
            <p style={{ fontSize: 12, color: "var(--text-secondary)", margin: "0 0 4px" }}>
              Total outflow
            </p>
            <p style={{ fontSize: 18, fontWeight: 500, margin: 0, color: "var(--danger)" }}>
              -₹{totalOutflow.toLocaleString("en-IN")}
            </p>
            <p style={{ fontSize: 11, color: "var(--text-muted)", margin: "4px 0 0" }}>
              {debits.length} entries
            </p>
          </div>
        </div>

        {loading && <p style={{ fontSize: 13, color: "var(--text-secondary)" }}>Loading…</p>}

        {!loading && labels.length > 0 && (
            <div className="card" style={{ marginBottom: 12 }}>
              <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "0 0 8px" }}>
                Breakdown
              </p>
              <div style={{ position: "relative", height: 200 }}>
                <Doughnut
                    data={{
                      labels,
                      datasets: [{ data: values, backgroundColor: colors, borderColor: "#fff", borderWidth: 2 }],
                    }}
                    options={{ responsive: true, maintainAspectRatio: false, cutout: "65%" }}
                />
              </div>
              <div
                  style={{
                    display: "flex",
                    flexWrap: "wrap",
                    gap: 10,
                    justifyContent: "center",
                    marginTop: 10,
                    fontSize: 12,
                    color: "var(--text-secondary)",
                  }}
              >
                {labels.map((l, i) => (
                    <span key={l} style={{ display: "flex", alignItems: "center", gap: 4 }}>
                <span
                    style={{
                      width: 9,
                      height: 9,
                      borderRadius: 2,
                      background: colors[i],
                      display: "inline-block",
                    }}
                />
                      {l.replace("_", " ")} · ₹{values[i].toLocaleString("en-IN")}
              </span>
                ))}
              </div>
            </div>
        )}

        {!loading && (
            <div className="card" style={{ marginBottom: 12 }}>
              <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "0 0 8px" }}>
                Last 6 months
              </p>
              <div style={{ position: "relative", height: 180 }}>
                <Bar
                    data={{
                      labels: monthlyTotals.map((m) => m.label),
                      datasets: [
                        {
                          data: monthlyTotals.map((m) => m.total),
                          backgroundColor: "#2a78d6",
                          borderRadius: 4,
                          maxBarThickness: 28,
                        },
                      ],
                    }}
                    options={{
                      responsive: true,
                      maintainAspectRatio: false,
                      plugins: { legend: { display: false } },
                      scales: {
                        y: {
                          beginAtZero: true,
                          ticks: { font: { size: 10 }, color: "#9ca3af" },
                          grid: { color: "#e5e7eb" },
                        },
                        x: {
                          ticks: { font: { size: 11 }, color: "#6b7280" },
                          grid: { display: false },
                        },
                      },
                    }}
                />
              </div>
            </div>
        )}

        <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "0 0 8px" }}>
          All transactions
        </p>
        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
          {ledgers.map((l) => {
            const meta = ICON_META[l.sourceType] || ICON_META.OTHER;
            const canEdit = canManage && l.sourceType === "MANUAL";

            return (
                <div
                    key={l.ledgerId}
                    className="card"
                    style={{ display: "flex", alignItems: "center", gap: 12 }}
                >
                  <div
                      style={{
                        width: 36,
                        height: 36,
                        borderRadius: "50%",
                        background: meta.bg,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        flexShrink: 0,
                      }}
                  >
                    <meta.Icon size={18} color={meta.color} strokeWidth={2} />
                  </div>

                  <div style={{ flex: 1 }}>
                    <p style={{ fontSize: 14, margin: 0 }}>{l.description || l.sourceType}</p>
                    <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                      {l.transactionDate}
                    </p>
                  </div>

                  <span
                      style={{
                        fontSize: 14,
                        fontWeight: 500,
                        color: l.entryType === "CREDIT" ? "var(--success)" : "var(--text-primary)",
                        marginRight: 4,
                      }}
                  >
                    {l.entryType === "CREDIT" ? "+" : "-"}₹{Number(l.amount).toLocaleString("en-IN")}
                  </span>

                  <ActionMenu
                      actions={
                        canEdit
                            ? [
                              { icon: <Pencil size={14} />, label: "Edit", onClick: () => navigate(`/ledger/${l.ledgerId}/edit`) },
                              { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDelete(l) },
                            ]
                            : []
                      }
                  />
                </div>
            );
          })}
          {!loading && ledgers.length === 0 && (
              <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                <ReceiptIcon size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                <p style={{ fontSize: 13, margin: 0 }}>No transactions yet</p>
              </div>
          )}
        </div>
      </div>
  );
}