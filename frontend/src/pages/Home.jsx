import { useNavigate } from 'react-router-dom';
import './HomeStyles.css';

function Home() {
    const navigate = useNavigate();

    // Hardcoded road safety announcements
    const safetyAnnouncements = [
        { id: 1, text: "Speed limits strictly enforced on NH-44. Drive safely!", icon: "⚠️" },
        { id: 2, text: "Monsoon alert: Reduced visibility on coastal highways", icon: "🌧️" },
        { id: 3, text: "Maintain 2-second following distance for safer travel", icon: "🚗" },
        { id: 4, text: "Emergency services: Dial 1073 for highway assistance", icon: "🚨" }
    ];

    return (
        <div className="page home-page">

            {/* CTA Buttons — moved above announcements */}
            <div className="cta-section">
                <div className="cta-card">
                    <h3>New User?</h3>
                    <p>Register your vehicle and start using our digital toll system today</p>
                    <button
                        className="btn btn-primary btn-cta"
                        onClick={() => navigate('/users')}
                    >
                        Register Now →
                    </button>
                </div>

                <div className="cta-card">
                    <h3>Existing User?</h3>
                    <p>Access your dashboard to view bills and manage your wallet</p>
                    <button
                        className="btn btn-primary btn-cta"
                        onClick={() => navigate('/login')}
                    >
                        Login Dashboard →
                    </button>
                </div>
            </div>

            {/* Safety Announcements */}
            <div className="card announcements-card">
                <h3>📢 Road Safety Announcements</h3>
                <div className="announcements-list">
                    {safetyAnnouncements.map(announcement => (
                        <div key={announcement.id} className="announcement-item">
                            <span className="announcement-icon">{announcement.icon}</span>
                            <span className="announcement-text">{announcement.text}</span>
                        </div>
                    ))}
                </div>
            </div>

            {/* Usage-Based Tolling Explanation */}
            <div className="card info-card">
                <h3>💡 How Usage-Based Tolling Works</h3>
                <p>
                    Our system tracks your vehicle's actual distance traveled on national highways using GPS technology.
                    You only pay for the distance you use, eliminating fixed toll booth charges and reducing queues.
                    Receive consolidated monthly bills via email for transparent, hassle-free payment.
                </p>
            </div>

            {/* Quick Features */}
            <div className="features-grid">
                <div className="feature-box">
                    <div className="feature-icon">📍</div>
                    <h4>GPS Tracking</h4>
                    <p>Real-time location monitoring</p>
                </div>
                <div className="feature-box">
                    <div className="feature-icon">💳</div>
                    <h4>Digital Wallet</h4>
                    <p>Cashless, automated payments</p>
                </div>
                <div className="feature-box">
                    <div className="feature-icon">📊</div>
                    <h4>Transparent Billing</h4>
                    <p>Detailed monthly statements</p>
                </div>
                <div className="feature-box">
                    <div className="feature-icon">🔒</div>
                    <h4>Secure System</h4>
                    <p>Fraud detection enabled</p>
                </div>
            </div>
        </div>
    );
}

export default Home;

