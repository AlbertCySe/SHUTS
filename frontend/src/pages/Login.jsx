import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/auth';
import './LoginStyles.css';

function Login({ setUserRole }) {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('user'); // 'user' or 'admin'
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // Form inputs
    const [email, setEmail] = useState('');
    const [passwordOrPhone, setPasswordOrPhone] = useState('');

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        setError('');
        setPasswordOrPhone('');
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');

        if (!email.trim() || !passwordOrPhone.trim()) {
            setError('Please fill in all fields');
            return;
        }

        try {
            setLoading(true);
            // Call backend auth
            const session = await login(email, passwordOrPhone, activeTab);

            // Update app state
            if (setUserRole) {
                setUserRole(session.role);
            }

            // Redirect appropriately
            if (session.role === 'admin') {
                navigate('/admin');
            } else {
                navigate('/user-dashboard');
            }
        } catch (err) {
            setError(err.message || 'Login failed. Please verify your credentials.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page login-page">
            <div className="login-container">
                <div className="login-card">
                    <h2 className="login-title">🔐 Secure Login</h2>

                    {/* Role Tabs */}
                    <div className="login-tabs">
                        <button
                            className={`login-tab ${activeTab === 'user' ? 'active' : ''}`}
                            onClick={() => handleTabChange('user')}
                            type="button"
                        >
                            👤 User
                        </button>
                        <button
                            className={`login-tab ${activeTab === 'admin' ? 'active' : ''}`}
                            onClick={() => handleTabChange('admin')}
                            type="button"
                        >
                            🛡️ Admin
                        </button>
                    </div>

                    {error && (
                        <div className="error-message login-error">
                            <p>⚠️ {error}</p>
                        </div>
                    )}

                    <form onSubmit={handleLogin} className="login-form">
                        <div className="form-group">
                            <label htmlFor="email">Email Address</label>
                            <input
                                type="email"
                                id="email"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="auth-secret">
                                {activeTab === 'user' ? 'Phone Number' : 'Admin Password'}
                            </label>
                            <input
                                type={activeTab === 'user' ? 'tel' : 'password'}
                                id="auth-secret"
                                placeholder={activeTab === 'user' ? 'Enter registered phone number' : 'Enter admin password'}
                                value={passwordOrPhone}
                                onChange={(e) => setPasswordOrPhone(e.target.value)}
                                disabled={loading}
                                required
                            />
                            {activeTab === 'user' && (
                                <small className="form-hint">No password required — login effortlessly with your registered phone number.</small>
                            )}
                        </div>

                        <button 
                            type="submit" 
                            className="btn btn-primary btn-login"
                            disabled={loading}
                        >
                            {loading ? 'Authenticating...' : `Login as ${activeTab === 'admin' ? 'Admin' : 'User'} →`}
                        </button>
                    </form>

                    {activeTab === 'user' && (
                        <div className="login-footer">
                            <p>New to the system?</p>
                            <button
                                className="btn-link"
                                onClick={() => navigate('/register')}
                                type="button"
                            >
                                Register your vehicle
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Login;

