import { useEffect, useState, useCallback } from "react";
import api, { getErrorMessage } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import StatusBadge from "../components/StatusBadge";
import Spinner from "../components/Spinner";
import { Search, Pencil, Trash2, Power } from "lucide-react";
import ActionMenu from "../components/ActionMenu";
import { useNavigate } from "react-router-dom";


const PAGE_SIZE = 5;

export default function RequestsList() {
  const { user } = useAuth();
  const { showToast } = useToast();

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const navigate = useNavigate();

  const canForward = user?.role === "OPERATIONS" || user?.role === "DIRECTOR";
  const canApprove = user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";
  const canReject =
      user?.role === "OPERATIONS" || user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";
  const canToggleActive = user?.role === "ACCOUNTS" || user?.role === "DIRECTOR";

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (search) params.set("search", search);
      if (statusFilter !== "ALL") params.set("status", statusFilter);
      params.set("page", currentPage);
      params.set("size", PAGE_SIZE);

      const res = await api.get(`/requests/search?${params.toString()}`);
      setRequests(res.data.content);
      setTotalPages(res.data.totalPages);
      setTotalElements(res.data.totalElements);
    } catch (err) {
      console.error(err);
      showToast("Couldn't load requests. Try again.", "error");
    } finally {
      setLoading(false);
    }
  }, [search, statusFilter, currentPage, showToast]);

  useEffect(() => {
    load();
  }, [load]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearch(searchInput);
      setCurrentPage(0);
    }, 400);
    return () => clearTimeout(timer);
  }, [searchInput]);

  async function act(id, action, extraParams = {}) {
    setBusyId(id);
    try {
      const params = new URLSearchParams(extraParams).toString();
      await api.patch(`/requests/${id}/${action}${params ? `?${params}` : ""}`);
      showToast(`Request ${action}d successfully`, "success");
      await load();
    } catch (err) {
      showToast(getErrorMessage(err), "error");
    } finally {
      setBusyId(null);
    }
  }

  async function handleDelete(id) {
    const confirmed = window.confirm("Delete this request?");
    if (!confirmed) return;

    setBusyId(id);
    try {
      await api.patch(`/requests/${id}`);
      showToast("Request deleted", "success");
      await load();
    } catch (err) {
      showToast(getErrorMessage(err), "error");
    } finally {
      setBusyId(null);
    }
  }

  function handleReject(id) {
    const reason = window.prompt("Reason for rejection:");
    if (!reason) return;
    act(id, "reject", { rejectionReason: reason });
  }

  return (
      <div className="screen">
        <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Requests</p>

        <div className="input-group" style={{ position: "relative" }}>
          <Search
              size={16}
              color="var(--text-muted)"
              style={{ position: "absolute", left: 12, top: 12 }}
          />
          <input
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              placeholder="Search by description or requester"
              style={{ paddingLeft: 36 }}
          />
        </div>

        <div style={{ display: "flex", gap: 6, overflowX: "auto", marginBottom: 16, paddingBottom: 4 }}>
          {["ALL", "PENDING", "APPROVED", "REJECTED"].map((s) => (
              <button
                  key={s}
                  onClick={() => {
                    setStatusFilter(s);
                    setCurrentPage(0);
                  }}
                  style={{
                    flexShrink: 0,
                    padding: "6px 14px",
                    borderRadius: 100,
                    fontSize: 12,
                    border: "1px solid var(--border-strong)",
                    background: statusFilter === s ? "var(--accent)" : "var(--surface)",
                    color: statusFilter === s ? "#fff" : "var(--text-primary)",
                    cursor: "pointer",
                  }}
              >
                {s === "ALL" ? "All" : s.charAt(0) + s.slice(1).toLowerCase()}
              </button>
          ))}
        </div>

        {loading && (
            <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
              <Spinner size={22} color="var(--accent)" />
            </div>
        )}

        {!loading && requests.length === 0 && (
            <p style={{ fontSize: 13, color: "var(--text-secondary)", textAlign: "center", padding: 24 }}>
              No matching requests.
            </p>
        )}

        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>

          {requests.map((r) => {
            const canEdit = r.status === "PENDING" && r.requestedBy === user?.fullName;

            return (
                <div key={r.id} className="card">
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                    <div>
                      <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>{r.description}</p>
                      <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                        {r.requestType} · {r.requestedBy} · {r.requestDate}
                        {r.amount ? ` · ₹${r.amount.toLocaleString("en-IN")}` : ""}
                      </p>
                      {r.approvalStage && (
                          <p style={{ fontSize: 11, color: "var(--text-muted)", margin: "2px 0 0" }}>
                            Stage: {r.approvalStage}
                          </p>
                      )}
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: 4 }}>
                      <StatusBadge status={r.status} />
                      <ActionMenu
                          actions={[
                            ...(canEdit
                                ? [
                                  { icon: <Pencil size={14} />, label: "Edit", onClick: () => navigate(`/requests/${r.id}/edit`) },
                                  { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDelete(r.id) },
                                ]
                                : []),
                            ...(canToggleActive
                                ? [{
                                  icon: <Power size={14} />,
                                  label: r.active ? "Deactivate" : "Activate",
                                  onClick: () => act(r.id, r.active ? "deactivate" : "activate"),
                                }]
                                : []),
                          ]}
                      />
                    </div>
                  </div>


                  {r.status === "PENDING" && (canForward || canApprove || canReject) && (
                      <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
                        {canForward && r.approvalStage === "PENDING_OPERATIONS" && (
                            <button
                                className="btn"
                                disabled={busyId === r.id}
                                onClick={() => act(r.id, "forward")}
                                style={{ display: "flex", alignItems: "center", gap: 6 }}
                            >
                              {busyId === r.id ? <Spinner size={14} color="var(--text-primary)" /> : "Forward"}
                            </button>
                        )}

                        {canApprove &&
                            (r.approvalStage === "PENDING_ACCOUNTS_DIRECTOR" || !r.approvalStage) && (
                                <button
                                    className="btn btn-primary"
                                    disabled={busyId === r.id}
                                    onClick={() => act(r.id, "approve")}
                                    style={{ display: "flex", alignItems: "center", gap: 6 }}
                                >
                                  {busyId === r.id ? <Spinner size={14} color="#fff" /> : "Approve"}
                                </button>
                            )}
                        {canReject && (
                            <button
                                className="btn"
                                disabled={busyId === r.id}
                                onClick={() => handleReject(r.id)}
                                style={{ display: "flex", alignItems: "center", gap: 6 }}
                            >
                              {busyId === r.id ? <Spinner size={14} color="var(--text-primary)" /> : "Reject"}
                            </button>
                        )}
                      </div>
                  )}
                </div>
            );
          })}
        </div>

        {!loading && totalElements > 0 && (
            <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: 12, marginTop: 16 }}>
              <button
                  className="btn"
                  disabled={currentPage === 0}
                  onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
                  style={{ padding: "6px 14px" }}
              >
                Previous
              </button>
              <span style={{ fontSize: 13, color: "var(--text-secondary)" }}>
            Page {currentPage + 1} of {totalPages}
          </span>
              <button
                  className="btn"
                  disabled={currentPage + 1 >= totalPages}
                  onClick={() => setCurrentPage((p) => Math.min(totalPages - 1, p + 1))}
                  style={{ padding: "6px 14px" }}
              >
                Next
              </button>
            </div>
        )}
      </div>
  );
}