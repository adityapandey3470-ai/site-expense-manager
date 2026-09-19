import { createContext, useContext, useState, useCallback } from "react";
import { CheckCircle2, XCircle, Info } from "lucide-react";

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);

    const showToast = useCallback((message, type = "info") => {
        const id = Date.now();
        setToasts((t) => [...t, { id, message, type }]);
        setTimeout(() => {
            setToasts((t) => t.filter((toast) => toast.id !== id));
        }, 3000);
    }, []);

    const icons = { success: CheckCircle2, error: XCircle, info: Info };
    const colors = {
        success: { bg: "var(--success-bg)", color: "var(--success)" },
        error: { bg: "var(--danger-bg)", color: "var(--danger)" },
        info: { bg: "var(--accent-bg)", color: "var(--accent)" },
    };

    return (
        <ToastContext.Provider value={{ showToast }}>
            {children}
            <div
                style={{
                    position: "fixed",
                    top: 16,
                    left: "50%",
                    transform: "translateX(-50%)",
                    zIndex: 1000,
                    display: "flex",
                    flexDirection: "column",
                    gap: 8,
                    width: "calc(100% - 32px)",
                    maxWidth: 440,
                }}
            >
                {toasts.map((t) => {
                    const Icon = icons[t.type];
                    const c = colors[t.type];
                    return (
                        <div
                            key={t.id}
                            style={{
                                background: c.bg,
                                color: c.color,
                                borderRadius: "var(--radius-sm)",
                                padding: "12px 14px",
                                display: "flex",
                                alignItems: "center",
                                gap: 8,
                                fontSize: 13,
                                boxShadow: "0 4px 16px rgba(0,0,0,0.12)",
                                animation: "fadeIn 0.2s ease",
                            }}
                        >
                            <Icon size={18} />
                            <span>{t.message}</span>
                        </div>
                    );
                })}
            </div>
        </ToastContext.Provider>
    );
}

export function useToast() {
    return useContext(ToastContext);
}