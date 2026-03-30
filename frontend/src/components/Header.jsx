import { getSession } from '../services/auth';
import './Header.css'

function Header() {
    const session = getSession();
    const isAdmin = session?.role === 'admin';
    const userName = session?.name;

    return (
        <header className={`header ${isAdmin ? 'header-admin' : ''}`}>
            {isAdmin && <div className="admin-watermark">ADMIN</div>}
            <div className="header-content">
                <h1 className="header-title">🚦 Smart Highway Usage-Based Tolling System</h1>
                <p className="header-subtitle">MCA Final Year Project</p>
                {userName && (
                    <div className="user-badge-header">
                         {isAdmin ? '🛡️' : '👤'} {userName}
                    </div>
                )}
            </div>
        </header>
    )
}

export default Header
