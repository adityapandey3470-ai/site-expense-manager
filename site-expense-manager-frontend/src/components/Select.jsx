import { useState, useRef, useEffect } from "react";
import { ChevronDown } from "lucide-react";

export default function Select({ label, value, onChange, options, formatLabel }) {
    const [open, setOpen] = useState(false);
    const ref = useRef(null);

    useEffect(() => {
        function handleClickOutside(e) {
            if (ref.current && !ref.current.contains(e.target)) {
                setOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const display = formatLabel ? formatLabel(value) : value;

    return (
        <div className="input-group" ref={ref} style={{ position: "relative" }}>
            {label && <label>{label}</label>}

            <div
                onClick={() => setOpen((o) => !o)}
                style={{
                    width: "100%",
                    border: "1px solid var(--border-strong)",
                    borderRadius: "var(--radius-sm)",
                    padding: "10px 12px",
                    fontSize: 14,
                    background: "var(--surface)",
                    color: "var(--text-primary)",
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    cursor: "pointer",
                    borderColor: open ? "var(--accent)" : "var(--border-strong)",
                    boxShadow: open ? "0 0 0 3px var(--accent-bg)" : "none",
                }}
            >
                <span>{display}</span>
                <ChevronDown
                    size={16}
                    color="var(--text-muted)"
                    style={{ transform: open ? "rotate(180deg)" : "none", transition: "transform 0.15s" }}
                />
            </div>

            {open && (
                <div
                    style={{
                        position: "absolute",
                        top: "calc(100% + 4px)",
                        left: 0,
                        right: 0,
                        background: "var(--surface)",
                        border: "1px solid var(--border)",
                        borderRadius: "var(--radius-sm)",
                        boxShadow: "0 4px 16px rgba(0,0,0,0.12)",
                        zIndex: 20,
                        maxHeight: 220,
                        overflowY: "auto",
                    }}
                >
                    {options.map((opt) => (
                        <div
                            key={opt}
                            onClick={() => {
                                onChange(opt);
                                setOpen(false);
                            }}
                            style={{
                                padding: "10px 12px",
                                fontSize: 14,
                                cursor: "pointer",
                                background: opt === value ? "var(--accent-bg)" : "transparent",
                                color: opt === value ? "var(--accent)" : "var(--text-primary)",
                            }}
                            onMouseEnter={(e) => {
                                if (opt !== value) e.currentTarget.style.background = "var(--surface-muted)";
                            }}
                            onMouseLeave={(e) => {
                                if (opt !== value) e.currentTarget.style.background = "transparent";
                            }}
                        >
                            {formatLabel ? formatLabel(opt) : opt}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}