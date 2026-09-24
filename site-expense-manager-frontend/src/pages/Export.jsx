import { useEffect, useState } from "react";
import api, { downloadReport, getErrorMessage } from "../api/client";
import { useToast } from "../context/ToastContext";
import Spinner from "../components/Spinner";
import { Download } from "lucide-react";

const REPORT_TYPES = [
    { value: "LEDGER", label: "Ledger" },
    { value: "TRAVEL_EXPENSE", label: "Travel expenses" },
    { value: "REQUEST", label: "Requests" },
];

export default function Export() {
    const { showToast } = useToast();
    const [reportType, setReportType] = useState("LEDGER");
    const [sites, setSites] = useState([]);
    const [selectedSites, setSelectedSites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [exporting, setExporting] = useState(null); // "csv" | "pdf" | null

    useEffect(() => {
        api
            .get("/ledgers/balance")
            .then((res) => {
                setSites(res.data);
                setSelectedSites(res.data.map((b) => b.siteId)); // default: all
            })
            .catch(() => showToast("Couldn't load sites.", "error"))
            .finally(() => setLoading(false));
    }, []);

    const allSelected = selectedSites.length === sites.length && sites.length > 0;

    function toggleSite(siteId) {
        setSelectedSites((prev) =>
            prev.includes(siteId) ? prev.filter((id) => id !== siteId) : [...prev, siteId]
        );
    }

    function toggleSelectAll() {
        setSelectedSites(allSelected ? [] : sites.map((b) => b.siteId));
    }

    async function handleExport(format) {
        if (selectedSites.length === 0) {
            showToast("Select at least one site to export.", "error");
            return;
        }

        setExporting(format);
        try {
            const siteParams = allSelected
                ? ""
                : "&" + selectedSites.map((id) => `siteIds=${id}`).join("&");

            const path = `/reports/export/${format}?type=${reportType}${siteParams}`;
            const filename = `${reportType.toLowerCase()}-export.${format}`;

            await downloadReport(path, filename);
            showToast(`${format.toUpperCase()} downloaded`, "success");
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        } finally {
            setExporting(null);
        }
    }

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>Export reports</p>

            <div className="card" style={{ marginBottom: 16 }}>
                <p style={{ fontSize: 13, fontWeight: 500, marginBottom: 8 }}>What do you want to export?</p>
                <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    {REPORT_TYPES.map((rt) => (
                        <label
                            key={rt.value}
                            style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13, cursor: "pointer" }}
                        >
                            <input
                                type="radio"
                                name="reportType"
                                checked={reportType === rt.value}
                                onChange={() => setReportType(rt.value)}
                            />
                            {rt.label}
                        </label>
                    ))}
                </div>
            </div>

            {loading && (
                <div style={{ display: "flex", justifyContent: "center", padding: 24 }}>
                    <Spinner size={22} color="var(--accent)" />
                </div>
            )}

            {!loading && sites.length > 0 && (
                <div className="card" style={{ marginBottom: 16 }}>
                    <label
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: 8,
                            fontSize: 13,
                            fontWeight: 500,
                            marginBottom: 10,
                            cursor: "pointer",
                        }}
                    >
                        <input type="checkbox" checked={allSelected} onChange={toggleSelectAll} />
                        Select all sites
                    </label>

                    <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                        {sites.map((s) => (
                            <label
                                key={s.siteId}
                                style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13, cursor: "pointer" }}
                            >
                                <input
                                    type="checkbox"
                                    checked={selectedSites.includes(s.siteId)}
                                    onChange={() => toggleSite(s.siteId)}
                                />
                                {s.siteName}
                            </label>
                        ))}
                    </div>
                </div>
            )}

            <div style={{ display: "flex", gap: 8 }}>
                <button
                    className="btn"
                    disabled={exporting !== null}
                    onClick={() => handleExport("csv")}
                    style={{ display: "flex", alignItems: "center", gap: 6, flex: 1, justifyContent: "center" }}
                >
                    {exporting === "csv" ? <Spinner size={14} color="var(--text-primary)" /> : <Download size={14} />}
                    Export CSV
                </button>
                <button
                    className="btn"
                    disabled={exporting !== null}
                    onClick={() => handleExport("pdf")}
                    style={{ display: "flex", alignItems: "center", gap: 6, flex: 1, justifyContent: "center" }}
                >
                    {exporting === "pdf" ? <Spinner size={14} color="var(--text-primary)" /> : <Download size={14} />}
                    Export PDF
                </button>
            </div>
        </div>
    );
}