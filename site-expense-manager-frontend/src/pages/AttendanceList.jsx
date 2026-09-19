import { useEffect, useState } from "react";
import api, { getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import { CheckSquare, Trash2 } from "lucide-react";
import ActionMenu from "../components/ActionMenu";
import { useAuth } from "../context/AuthContext";



export default function AttendanceList() {
    const { showToast } = useToast();
    const [records, setRecords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [busyId, setBusyId] = useState(null);
    const { user } = useAuth();

    function load() {
        setLoading(true);
        const url = user?.siteId ? `/attendances/site/${user.siteId}` : "/attendances";
        api
            .get(url)
            .then((res) => setRecords(res.data))
            .catch(() => showToast("Couldn't load attendance.", "error"))
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        load();
    }, []);

    async function handleDelete(record) {
        const confirmed = window.confirm(
            `Delete attendance for ${record.attendanceDate}? This will reverse the food accrual ledger entry.`
        );
        if (!confirmed) return;

        setBusyId(record.id);
        try {
            await api.delete(`/attendances/${record.id}`);
            setRecords((prev) => prev.filter((r) => r.id !== record.id));
            showToast("Attendance record deleted", "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setBusyId(null);
        }
    }

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Attendance</p>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && records.length === 0 && (
                <div style={{ textAlign: "center", padding: "32px 16px", color: "var(--text-muted)" }}>
                    <CheckSquare size={32} strokeWidth={1.5} style={{ marginBottom: 8, opacity: 0.5 }} />
                    <p style={{ fontSize: 13, margin: 0 }}>No attendance records yet</p>
                </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
                {records.map((a) => (
                    <div key={a.id} className="card">
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                            <div>
                                <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>
                                    {a.siteName || `Site #${a.siteId}`}
                                </p>
                                <p style={{ fontSize: 12, color: "var(--text-muted)", margin: "4px 0 0" }}>
                                    {a.attendanceDate}
                                </p>
                            </div>
                            <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                                <span style={{ fontSize: 16, fontWeight: 500 }}>{a.presentCount} present</span>
                                <ActionMenu
                                    actions={[
                                        { icon: <Trash2 size={14} />, label: "Delete", color: "var(--danger)", onClick: () => handleDelete(a) },
                                    ]}
                                />
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}