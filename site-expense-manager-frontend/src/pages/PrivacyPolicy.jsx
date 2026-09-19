import { Link } from "react-router-dom";
import { ChevronLeft } from "lucide-react";

export default function PrivacyPolicy() {
    return (
        <div className="screen">
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 16 }}>
                <Link to="/more" style={{ display: "flex" }}>
                    <ChevronLeft size={20} color="var(--text-primary)" />
                </Link>
                <p style={{ fontSize: 20, fontWeight: 500, margin: 0 }}>Privacy Policy</p>
            </div>

            <div className="card" style={{ fontSize: 13, lineHeight: 1.7, color: "var(--text-secondary)" }}>
                <p style={{ fontSize: 12, color: "var(--text-muted)" }}>Last updated: July 2026</p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>What we collect</h4>
                <p>
                    Site Expense Manager collects your name, username, role, and site assignment
                    to operate your account. It also stores the expense requests, attendance
                    records, travel expenses, and ledger entries you or your team submit.
                </p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>How we use it</h4>
                <p>
                    This data is used only to run the approval workflow, calculate site balances,
                    and generate payouts for your organization. It is not sold or shared with
                    third parties.
                </p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>Who can see your data</h4>
                <p>
                    Only users within your organization (Supervisor, Operations, Accounts, Director)
                    can access data relevant to their role. Access is controlled by role-based
                    permissions.
                </p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>Data storage</h4>
                <p>
                    Your data is stored securely on our servers and protected using encrypted
                    authentication (JWT) and password hashing. We do not store your password in
                    plain text.
                </p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>Your rights</h4>
                <p>
                    You can request access to, correction of, or deletion of your account data by
                    contacting your organization administrator or the support contact listed in
                    the app.
                </p>

                <h4 style={{ color: "var(--text-primary)", marginBottom: 4 }}>Contact</h4>
                <p style={{ marginBottom: 0 }}>
                    For any privacy-related questions, reach out via the contact details on the
                    More screen.
                </p>
            </div>
        </div>
    );
}