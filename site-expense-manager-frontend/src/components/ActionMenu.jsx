import { useEffect, useRef, useState } from "react";
import { MoreVertical } from "lucide-react";

export default function ActionMenu({ actions }) {
    const [open, setOpen] = useState(false);
    const ref = useRef(null);

    useEffect(() => {
        function handleClickOutside(e) {
            if (ref.current && !ref.current.contains(e.target)) {
                setOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        document.addEventListener("touchstart", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
            document.removeEventListener("touchstart", handleClickOutside);
        };
    }, []);

    if (!actions || actions.length === 0) return null;

    return (
        <div ref={ref} style={{ position: "relative" }}>
            <button
                onClick={() => setOpen((o) => !o)}
                style={{
                    background: "none",
                    border: "none",
                    cursor: "pointer",
                    padding: 6,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    borderRadius: 8,
                }}
            >
                <MoreVertical size={18} color="var(--text-muted)" />
            </button>

            {open && (
                <div
                    style={{
                        position: "absolute",
                        top: "100%",
                        right: 0,
                        marginTop: 4,
                        background: "var(--surface)",
                        border: "1px solid var(--border)",
                        borderRadius: 10,
                        boxShadow: "0 4px 16px rgba(0,0,0,0.2)",
                        minWidth: 170,
                        zIndex: 50,
                        overflow: "hidden",
                    }}
                >
                    {actions.map((action, i) => (
                        <button
                            key={i}
                            onClick={() => {
                                setOpen(false);
                                action.onClick();
                            }}
                            style={{
                                width: "100%",
                                display: "flex",
                                alignItems: "center",
                                gap: 10,
                                padding: "10px 14px",
                                background: "none",
                                border: "none",
                                borderBottom: i < actions.length - 1 ? "1px solid var(--border)" : "none",
                                cursor: "pointer",
                                fontSize: 13,
                                color: action.color || "var(--text-primary)",
                                textAlign: "left",
                            }}
                        >
                            {action.icon}
                            {action.label}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}