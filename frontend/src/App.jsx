import { useState, lazy, Suspense, useRef, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom'
import Header from './components/Header'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Users from './pages/Users'
const AdminReports = lazy(() => import('./pages/AdminReports'));
const DbViewer = lazy(() => import('./pages/DbViewer'));
const LoadingFallback = lazy(() => import('./components/LoadingFallback'));
import { getSession, clearSession } from './services/auth';
import { getRequest } from './services/api';
import NotificationBell from './components/NotificationBell';
import './App.css';

// Lazy load major pages for code splitting and performance
const UserDashboard = lazy(() => import('./pages/UserDashboard'));
const Vehicles = lazy(() => import('./pages/Vehicles'));
const WalletBills = lazy(() => import('./pages/WalletBills'));
const AdminDashboard = lazy(() => import('./pages/AdminDashboard'));
const AdminUsers = lazy(() => import('./pages/AdminUsers'));
const AdminVehicles = lazy(() => import('./pages/AdminVehicles'));
const AdminHighways = lazy(() => import('./pages/AdminHighways'));
const TollHistory = lazy(() => import('./pages/TollHistory'));
const AdminProfileRequests = lazy(() => import('./pages/AdminProfileRequests'));


function App() {
    const [userRole, setUserRole] = useState(null);
    const [dbOpen, setDbOpen] = useState(false);
    const [isAuthChecking, setIsAuthChecking] = useState(true);
    const [pendingRequestCount, setPendingRequestCount] = useState(0);

    // Restore session on mount
    useEffect(() => {
        const session = getSession();
        if (session && session.role) {
            setUserRole(session.role);
            if (session.role === 'admin') fetchPendingCount();
        }
        setIsAuthChecking(false);
    }, []);

    const fetchPendingCount = async () => {
        try {
            const [profileData, vehicleData] = await Promise.all([
                getRequest('/profile-requests/pending/count'),
                getRequest('/vehicle-requests/pending/count')
            ]);
            setPendingRequestCount((profileData?.count || 0) + (vehicleData?.count || 0));
        } catch { setPendingRequestCount(0); }
    };

    const handleLogout = () => {
        clearSession();
        setUserRole(null);
    };


    if (isAuthChecking) {
        return <LoadingFallback />;
    }

    return (
        <Router>
            <div className="app">
                <Header />

                {/* Conditional Navigation */}
                {userRole === 'admin' ? (
                    <nav className="admin-horizontal-nav">
                        <div className="admin-nav-inner">
                            <Link to="/admin" className="admin-nav-link">🛡️ Dashboard</Link>
                            <Link to="/admin/users" className="admin-nav-link">👥 Users</Link>
                            <Link to="/admin/vehicles" className="admin-nav-link">🚗 Vehicles</Link>
                            <Link to="/admin/highways" className="admin-nav-link">🛣️ Highways</Link>
                            <Link to="/admin/reports" className="admin-nav-link">📋 Reports</Link>
                            <Link to="/admin/profile-requests" className="admin-nav-link"
                                onClick={fetchPendingCount}
                                style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                                🔔 Requests
                                {pendingRequestCount > 0 && (
                                    <span style={{
                                        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
                                        background: '#e74c3c', color: 'white',
                                        borderRadius: '10px', fontSize: '11px', fontWeight: '800',
                                        minWidth: '18px', height: '18px', padding: '0 4px',
                                        lineHeight: 1, flexShrink: 0
                                    }}>{pendingRequestCount}</span>
                                )}
                            </Link>
                            <button onClick={handleLogout} className="admin-nav-link admin-nav-logout">🚪 Logout</button>
                        </div>
                    </nav>
                ) : (
                    <nav className="user-horizontal-nav">
                        <div className="user-nav-inner">
                            <Link to="/" className="user-nav-link">🏠 Home</Link>
                            
                            {/* User-specific navigation */}
                            {userRole === 'user' && (
                                <>
                                    <Link to="/user-dashboard" className="user-nav-link">📊 Dashboard</Link>
                                    <Link to="/vehicles" className="user-nav-link">🚗 My Vehicles</Link>
                                    <Link to="/toll-history" className="user-nav-link">📊 Toll History</Link>
                                    <Link to="/wallet-bills" className="user-nav-link">💳 Wallet &amp; Bills</Link>
                                    <Link to="/users" className="user-nav-link">👤 Profile</Link>
                                    <NotificationBell />
                                    <button onClick={handleLogout} className="user-nav-link user-nav-logout">🚪 Logout</button>
                                </>
                            )}

                            {/* Guest navigation */}
                            {!userRole && (
                                <Link to="/login" className="user-nav-link user-nav-login">🔑 Login</Link>
                            )}
                        </div>
                    </nav>
                )}

                <div className="container">
                    <Suspense fallback={<LoadingFallback />}>
                        <Routes>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<LoginWrapper setUserRole={setUserRole} />} />
                            <Route path="/register" element={<Register />} />
                            <Route path="/users" element={<ProtectedRoute userRole={userRole} allowedRoles={['user', 'admin']}><Users /></ProtectedRoute>} />
                            
                            {/* Protected Routes */}
                            <Route path="/user-dashboard" element={<ProtectedRoute userRole={userRole} allowedRoles={['user']}><UserDashboard /></ProtectedRoute>} />
                            <Route path="/vehicles" element={<ProtectedRoute userRole={userRole} allowedRoles={['user', 'admin']}><Vehicles /></ProtectedRoute>} />
                            <Route path="/admin/reports" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminReports /></ProtectedRoute>} />
                            <Route path="/wallet-bills" element={<ProtectedRoute userRole={userRole} allowedRoles={['user']}><WalletBills /></ProtectedRoute>} />
                            <Route path="/toll-history" element={<ProtectedRoute userRole={userRole} allowedRoles={['user']}><TollHistory /></ProtectedRoute>} />
                            <Route path="/admin" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminDashboard /></ProtectedRoute>} />
                            <Route path="/admin/users" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminUsers /></ProtectedRoute>} />
                            <Route path="/admin/vehicles" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminVehicles /></ProtectedRoute>} />
                            <Route path="/admin/highways" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminHighways /></ProtectedRoute>} />
                            <Route path="/admin/profile-requests" element={<ProtectedRoute userRole={userRole} allowedRoles={['admin']}><AdminProfileRequests onCountChange={setPendingRequestCount} /></ProtectedRoute>} />
                        </Routes>
                    </Suspense>
                </div>

                <footer className="footer">
                    <p>&copy; 2026 Smart Highway Tolling System - MCA Final Year Project</p>
                </footer>

                {/* ── Floating DB Viewer Panel ── */}
                <DbViewer open={dbOpen} onClose={() => setDbOpen(false)} />

                {/* ── DB Toggle Button ── */}
                <button
                    className={`db-float-btn${dbOpen ? ' db-float-active' : ''}`}
                    title={dbOpen ? 'Close DB Viewer' : 'Open DB Viewer'}
                    onClick={() => setDbOpen(prev => !prev)}
                >
                    🗄️
                    <span className="db-float-label">{dbOpen ? 'Close' : 'DB'}</span>
                </button>
            </div>
        </Router>
    )
}

function LoginWrapper({ setUserRole }) {
    return <Login setUserRole={setUserRole} />;
}

function ProtectedRoute({ userRole, allowedRoles, children }) {
    if (!userRole) {
        return <Navigate to="/login" replace />;
    }
    if (allowedRoles && !allowedRoles.includes(userRole)) {
        return <Navigate to="/" replace />;
    }
    return children;
}

export default App

