import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Building2, Car, UtensilsCrossed, Banknote, ArrowRight } from "lucide-react";

const SLIDES = [
    {
        icon: Building2,
        colorVar: "--accent",
        bgVar: "--accent-bg",
        title: "Raise and approve requests",
        description: "Supervisors raise material and expense requests, approvers act in a few taps.",
    },
    {
        icon: Car,
        colorVar: "--success",
        bgVar: "--success-bg",
        title: "Log attendance and travel",
        description: "Mark daily site attendance and travel expenses in seconds, from your phone.",
    },
    {
        icon: null,
        title: "See where money goes",
        description: "Live balances and spend breakdown, per site.",
    },
];

const BREAKDOWN = [
    { label: "Material", pct: 43, icon: Building2, colorVar: "--accent", bgVar: "--accent-bg" },
    { label: "Travel", pct: 29, icon: Car, colorVar: "--success", bgVar: "--success-bg" },
    { label: "Food accrual", pct: 23, icon: UtensilsCrossed, colorVar: "--warning", bgVar: "--warning-bg" },
    { label: "Advance", pct: 17, icon: Banknote, colorVar: "--danger", bgVar: "--danger-bg" },
];

function DonutChart() {
    const circumference = 2 * Math.PI * 46;
    let offset = 0;

    return (
        <svg viewBox="0 0 120 120" width="120" height="120" style={{ display: "block", margin: "0 auto" }}>
            <circle cx="60" cy="60" r="46" fill="none" stroke="var(--border)" strokeWidth="16" />
            {BREAKDOWN.map((seg) => {
                const dash = (seg.pct / 100) * circumference;
                const circle = (
                    <circle
                        key={seg.label}
                        cx="60"
                        cy="60"
                        r="46"
                        fill="none"
                        stroke={`var(${seg.colorVar})`}
                        strokeWidth="16"
                        strokeDasharray={`${dash} ${circumference}`}
                        strokeDashoffset={-offset}
                        transform="rotate(-90 60 60)"
                    />
                );
                offset += dash;
                return circle;
            })}
            <text x="60" y="56" textAnchor="middle" fontSize="18" fontWeight="600" fill="var(--text-primary)">4</text>
            <text x="60" y="72" textAnchor="middle" fontSize="9" fill="var(--text-secondary)">sites</text>
        </svg>
    );
}

export default function Welcome() {
    const navigate = useNavigate();
    const [index, setIndex] = useState(0);
    const isLast = index === SLIDES.length - 1;
    const slide = SLIDES[index];

    function next() {
        if (isLast) {
            localStorage.setItem("hasSeenOnboarding", "true");
            navigate("/login");
        } else {
            setIndex((i) => i + 1);
        }
    }

    function skip() {
        localStorage.setItem("hasSeenOnboarding", "true");
        navigate("/login");
    }

    return (
        <div
            style={{
                flex: 1,
                minHeight: "100vh",
                display: "flex",
                flexDirection: "column",
                padding: "24px 24px 32px",
                boxSizing: "border-box",
            }}
        >
            <div style={{ display: "flex", justifyContent: "flex-end", minHeight: 20 }}>
                {!isLast && (
                    <button
                        onClick={skip}
                        style={{ background: "none", border: "none", fontSize: 13, color: "var(--text-secondary)", cursor: "pointer" }}
                    >
                        Skip
                    </button>
                )}
            </div>

            <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                {slide.icon ? (
                    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center" }}>
                        <div
                            style={{
                                width: 88,
                                height: 88,
                                borderRadius: 22,
                                background: `var(${slide.bgVar})`,
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                marginBottom: 24,
                            }}
                        >
                            <slide.icon size={40} color={`var(${slide.colorVar})`} strokeWidth={1.7} />
                        </div>
                        <p style={{ fontSize: 19, fontWeight: 500, color: "var(--text-primary)", margin: "0 0 10px" }}>
                            {slide.title}
                        </p>
                        <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: 0, maxWidth: 260, lineHeight: 1.6 }}>
                            {slide.description}
                        </p>
                    </div>
                ) : (
                    <div>
                        <p style={{ fontSize: 18, fontWeight: 500, color: "var(--text-primary)", margin: "0 0 4px", textAlign: "center" }}>
                            {slide.title}
                        </p>
                        <p style={{ fontSize: 12, color: "var(--text-secondary)", margin: "0 0 18px", textAlign: "center" }}>
                            {slide.description}
                        </p>

                        <DonutChart />

                        <div style={{ display: "flex", flexDirection: "column", gap: 8, marginTop: 16 }}>
                            {BREAKDOWN.map((seg) => (
                                <div key={seg.label} style={{ display: "flex", alignItems: "center", gap: 10 }}>
                                    <div
                                        style={{
                                            width: 28, height: 28, borderRadius: 8,
                                            background: `var(${seg.bgVar})`,
                                            display: "flex", alignItems: "center", justifyContent: "center",
                                            flexShrink: 0,
                                        }}
                                    >
                                        <seg.icon size={14} color={`var(${seg.colorVar})`} strokeWidth={2} />
                                    </div>
                                    <span style={{ fontSize: 12, color: "var(--text-primary)", flex: 1 }}>{seg.label}</span>
                                    <span style={{ fontSize: 12, color: "var(--text-secondary)" }}>{seg.pct}%</span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>

            <div style={{ display: "flex", flexDirection: "column", gap: 16, alignItems: "center" }}>
                <div style={{ display: "flex", gap: 6 }}>
                    {SLIDES.map((_, i) => (
                        <div
                            key={i}
                            style={{
                                width: i === index ? 20 : 6,
                                height: 6,
                                borderRadius: 100,
                                background: i === index ? "var(--accent)" : "var(--border-strong)",
                                transition: "width 0.2s ease",
                            }}
                        />
                    ))}
                </div>

                <button
                    className="btn btn-primary btn-block"
                    onClick={next}
                    style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 8, padding: "13px" }}
                >
                    {isLast ? "Get started" : "Next"}
                    {!isLast && <ArrowRight size={16} />}
                </button>
            </div>
        </div>
    );
}