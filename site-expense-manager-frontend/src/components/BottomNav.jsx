import { NavLink } from "react-router-dom";
import { Home, FileText, PlusCircle, PieChart, Settings } from "lucide-react";

const items = [
    { to: "/", label: "Home", Icon: Home },
    { to: "/requests", label: "Requests", Icon: FileText },
    { to: "/requests/new", label: "Add", Icon: PlusCircle },
    { to: "/summary", label: "Summary", Icon: PieChart },
    { to: "/more", label: "More", Icon: Settings },
];

export default function BottomNav() {
    return (
        <nav
            style={{
                position: "sticky",
                bottom: 0,
                left: 0,
                right: 0,
                background: "var(--surface)",
                borderTop: "1px solid var(--border)",
                display: "flex",
                justifyContent: "space-around",
                padding: "8px 0 calc(8px + env(safe-area-inset-bottom))",
            }}
        >
            {items.map(({ to, label, Icon }) => (
                <NavLink
                    key={to}
                    to={to}
                    end={to === "/" || to === "/requests"}
                    style={({ isActive }) => ({
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        gap: 3,
                        fontSize: 11,
                        color: isActive ? "var(--accent)" : "var(--text-muted)",
                    })}
                >
                    <Icon size={20} strokeWidth={1.8} />
                    {label}
                </NavLink>

            ))}
        </nav>
    );
}