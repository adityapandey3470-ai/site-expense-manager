import { useEffect, useState } from "react";
import { Building2 } from "lucide-react";

export default function Splash({ onFinish }) {
    const [visible, setVisible] = useState(true);

    useEffect(() => {
        const showTimer = setTimeout(() => setVisible(false), 1400);
        const finishTimer = setTimeout(() => onFinish(), 1700);
        return () => {
            clearTimeout(showTimer);
            clearTimeout(finishTimer);
        };
    }, [onFinish]);

    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                gap: 16,
                background: "var(--page-bg)",
                zIndex: 9999,
                opacity: visible ? 1 : 0,
                transition: "opacity 0.3s ease",
                pointerEvents: visible ? "auto" : "none",
            }}
        >
            <div
                style={{
                    width: 80,
                    height: 80,
                    borderRadius: 20,
                    background: "var(--accent-bg)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    animation: "splash-icon-in 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards",
                }}
            >
                <Building2 size={36} color="var(--accent)" strokeWidth={1.7} />
            </div>

            <div
                style={{
                    textAlign: "center",
                    opacity: 0,
                    animation: "splash-text-in 0.5s ease 0.25s forwards",
                }}
            >
                <p style={{ fontSize: 16, fontWeight: 500, color: "var(--text-primary)", margin: 0 }}>
                    Site expense manager
                </p>
            </div>

            <div
                style={{
                    display: "flex",
                    gap: 5,
                    marginTop: 6,
                    opacity: 0,
                    animation: "splash-dots-in 0.4s ease 0.55s forwards",
                }}
            >
                {[0, 0.15, 0.3].map((delay) => (
                    <div
                        key={delay}
                        style={{
                            width: 6,
                            height: 6,
                            borderRadius: "50%",
                            background: "var(--accent)",
                            animation: `splash-dot-bounce 0.9s ease infinite`,
                            animationDelay: `${delay}s`,
                        }}
                    />
                ))}
            </div>

            <style>{`
                @keyframes splash-icon-in {
                    0% { transform: scale(0.4); opacity: 0; }
                    60% { transform: scale(1.08); opacity: 1; }
                    100% { transform: scale(1); opacity: 1; }
                }
                @keyframes splash-text-in {
                    0% { transform: translateY(8px); opacity: 0; }
                    100% { transform: translateY(0); opacity: 1; }
                }
                @keyframes splash-dots-in {
                    0% { opacity: 0; }
                    100% { opacity: 1; }
                }
                @keyframes splash-dot-bounce {
                    0%, 100% { transform: translateY(0); opacity: 0.4; }
                    50% { transform: translateY(-4px); opacity: 1; }
                }
            `}</style>
        </div>
    );
}