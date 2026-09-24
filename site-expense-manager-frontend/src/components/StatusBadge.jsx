export default function StatusBadge({ status }) {
  const cls =
    status === "APPROVED"
      ? "badge badge-approved"
      : status === "REJECTED"
      ? "badge badge-rejected"
      : "badge badge-pending";

  const label =
    status === "APPROVED" ? "Approved" : status === "REJECTED" ? "Rejected" : "Pending";

  return <span className={cls}>{label}</span>;
}
