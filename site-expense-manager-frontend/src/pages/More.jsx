import { Link, useNavigate } from "react-router-dom";
import {HelpCircle, ShieldCheck, Mail, ChevronRight, KeyRound, SettingsIcon} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useTheme } from "../context/ThemeContext";
import { HardHat, Settings2, Calculator, Crown } from "lucide-react";



const APP_VERSION = "1.0.0";

const ROLE_ICONS = {
    SUPERVISOR: HardHat,
    OPERATIONS: Settings2,
    ACCOUNTS: Calculator,
    DIRECTOR: ShieldCheck,
};

export default function More() {
    const { user, logout } = useAuth();
    const { theme } = useTheme();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    const menuItems = [
        { to: "/change-password", icon: KeyRound, label: "Change password" },
        ...(user?.role === "DIRECTOR" ? [{ to: "/settings", icon: SettingsIcon, label: "System settings" }] : []),
        { to: "/help", icon: HelpCircle, label: "How to use" },
        { to: "/privacy", icon: ShieldCheck, label: "Privacy Policy" },
    ];

    return (
        <div className="screen">
            <p style={{ fontSize: 20, fontWeight: 500, margin: "0 0 16px" }}>More</p>

            <div className="card" style={{ marginBottom: 12, display: "flex", alignItems: "center", gap: 12 }}>
                <div
                    style={{
                        width: 44, height: 44, borderRadius: "50%",
                        background: "var(--accent-bg)", color: "var(--accent)",
                        display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0,
                    }}
                >
                    {(() => {
                        const RoleIcon = ROLE_ICONS[user?.role] || HardHat;
                        return <RoleIcon size={20} strokeWidth={2} />;
                    })()}
                </div>
                <div>
                    <p style={{ fontSize: 14, fontWeight: 500, margin: 0 }}>{user?.fullName}</p>
                    <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: "4px 0 0" }}>
                        {user?.role}
                    </p>
                </div>
            </div>

            <div className="card" style={{ padding: 0, marginBottom: 12, overflow: "hidden" }}>
                {menuItems.map((item, i) => (
                    <Link
                        key={item.to}
                        to={item.to}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: 12,
                            padding: "14px 16px",
                            borderBottom: i < menuItems.length - 1 ? "1px solid var(--border)" : "none",
                        }}
                    >
                        <item.icon size={18} color="var(--text-secondary)" />
                        <span style={{ flex: 1, fontSize: 14 }}>{item.label}</span>
                        <ChevronRight size={16} color="var(--text-muted)" />
                    </Link>
                ))}
            </div>


            <div className="card" style={{ marginBottom: 12 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <Mail size={18} color="var(--text-secondary)" />
                    <div>
                        <p style={{ fontSize: 13, margin: 0 }}>Need help?</p>
                     <a
                        href="mailto:support0912@gmail.com"
                        style={{ fontSize: 13, color: "var(--accent)" }}
                        >
                        support0912@gmail.com
                    </a>
                </div>
            </div>
        </div>

    <button className="btn btn-block" onClick={handleLogout}>
        Log out
    </button>

    <p style={{ textAlign: "center", fontSize: 12, color: "var(--text-muted)", marginTop: 16 }}>
        Version {APP_VERSION}
    </p>
</div>
);
}