import { useState, useEffect } from 'react';
import { getRequest } from '../services/api';
import './DbViewerStyles.css';

const DB_TABLES = [
    {
        key: 'users',
        label: '👤 Users',
        endpoint: '/users',
    },
    {
        key: 'vehicles',
        label: '🚗 Vehicles',
        endpoint: '/vehicles',
    },
    {
        key: 'highways',
        label: '🛣️ Highways',
        endpoint: '/highways',
    },
    {
        key: 'locations',
        label: '📍 Locations',
        endpoint: '/locations',
    },
];

function DbViewer({ open, onClose }) {
    const [activeTab, setActiveTab] = useState('users');
    const [tableData, setTableData] = useState({});
    const [loadingTab, setLoadingTab] = useState({});
    const [errorTab, setErrorTab] = useState({});
    const [minimized, setMinimized] = useState(false);
    const [fullscreen, setFullscreen] = useState(false);

    // Auto-load table when tab changes
    useEffect(() => {
        if (!open) return;
        if (tableData[activeTab]) return;
        fetchTable(activeTab);
    }, [activeTab, open]);

    const fetchTable = async (key) => {
        const table = DB_TABLES.find(t => t.key === key);
        if (!table) return;
        setLoadingTab(prev => ({ ...prev, [key]: true }));
        setErrorTab(prev => ({ ...prev, [key]: null }));
        try {
            const data = await getRequest(table.endpoint);
            setTableData(prev => ({ ...prev, [key]: Array.isArray(data) ? data : [] }));
        } catch {
            setErrorTab(prev => ({ ...prev, [key]: 'Failed to load. Is the backend running?' }));
        } finally {
            setLoadingTab(prev => ({ ...prev, [key]: false }));
        }
    };

    const handleRefresh = () => {
        setTableData(prev => ({ ...prev, [activeTab]: undefined }));
        fetchTable(activeTab);
    };

    if (!open) return null;

    const rows = tableData[activeTab] || [];
    const displayCols = rows.length > 0 ? Object.keys(rows[0]) : [];

    const formatCell = (val) => {
        if (val === null || val === undefined) return <span className="dbv-null">null</span>;
        if (typeof val === 'object') return <span className="dbv-json">{JSON.stringify(val)}</span>;
        return String(val);
    };

    return (
        <div className={`dbv-panel${minimized ? ' minimized' : ''}${fullscreen ? ' fullscreen' : ''}`}>
            {/* Panel Header */}
            <div className="dbv-panel-header">
                <span className="dbv-panel-title">🗄️ DB Viewer</span>
                <div className="dbv-panel-actions">
                    <button
                        className="dbv-action-btn"
                        onClick={handleRefresh}
                        title="Refresh current table"
                    >🔄</button>
                    <button
                        className="dbv-action-btn"
                        onClick={() => setFullscreen(f => !f)}
                        title={fullscreen ? 'Exit fullscreen' : 'Fullscreen'}
                    >{fullscreen ? '⊡' : '⛶'}</button>
                    <button
                        className="dbv-action-btn"
                        onClick={() => setMinimized(m => !m)}
                        title={minimized ? 'Expand' : 'Minimize'}
                    >{minimized ? '▲' : '▼'}</button>
                    <button
                        className="dbv-action-btn dbv-close"
                        onClick={onClose}
                        title="Close"
                    >✕</button>
                </div>
            </div>

            {/* Collapsible Body */}
            {!minimized && (
                <div className="dbv-panel-body">
                    {/* Tabs */}
                    <div className="dbv-tabs">
                        {DB_TABLES.map(t => (
                            <button
                                key={t.key}
                                className={`dbv-tab${activeTab === t.key ? ' active' : ''}`}
                                onClick={() => setActiveTab(t.key)}
                            >
                                {t.label}
                                {tableData[t.key] && (
                                    <span className="dbv-count">{tableData[t.key].length}</span>
                                )}
                            </button>
                        ))}
                    </div>

                    {/* Table Area */}
                    <div className="dbv-table-area">
                        {loadingTab[activeTab] && (
                            <p className="dbv-msg">⏳ Loading...</p>
                        )}
                        {errorTab[activeTab] && (
                            <div className="dbv-error">
                                <p>⚠️ {errorTab[activeTab]}</p>
                                <button onClick={handleRefresh} className="dbv-retry">Retry</button>
                            </div>
                        )}
                        {!loadingTab[activeTab] && !errorTab[activeTab] && rows.length === 0 && (
                            <p className="dbv-msg">📭 No records found.</p>
                        )}
                        {!loadingTab[activeTab] && rows.length > 0 && (
                            <div className="dbv-scroll">
                                <p className="dbv-row-count">{rows.length} record{rows.length !== 1 ? 's' : ''}</p>
                                <table className="dbv-table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            {displayCols.map(col => <th key={col}>{col}</th>)}
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {rows.map((row, i) => (
                                            <tr key={i}>
                                                <td className="dbv-row-num">{i + 1}</td>
                                                {displayCols.map(col => (
                                                    <td key={col}>{formatCell(row[col])}</td>
                                                ))}
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default DbViewer;
