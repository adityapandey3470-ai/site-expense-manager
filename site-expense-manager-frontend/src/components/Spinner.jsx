export default function Spinner({ size = 16, color = "currentColor" }) {
    return (
        <div
            style={{
                width: size,
                height: size,
                border: `2px solid ${color}33`,
                borderTopColor: color,
                borderRadius: "50%",
                animation: "spin 0.6s linear infinite",
            }}
        />
    );
}