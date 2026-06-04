import { useState, useEffect } from 'react';
import { getRequest, postRequest } from '../services/api';
import './DbViewerStyles.css';

function DbViewer({ open, onClose }) {
    const [dbTables, setDbTables] = useState([]);
    const [activeTab, setActiveTab] = useState('');
    const [tableData, setTableData] = useState({});
    const [loadingTab, setLoadingTab] = useState({});
    const [errorTab, setErrorTab] = useState({});
    const [minimized, setMinimized] = useState(false);
    const [fullscreen, setFullscreen] = useState(false);

    // Fetch dynamic tables when panel opens
    useEffect(() => {
        if (!open) return;
        if (dbTables.length > 0) return; // already fetched
        
        const fetchTables = async () => {
            try {
                const tables = await getRequest('/db-explorer/tables');
                setDbTables(tables);
                if (tables.length > 0 && !activeTab) {
                    setActiveTab(tables[0].key);
                }
            } catch (err) {
                console.error("Failed to fetch tables", err);
            }
        };
        fetchTables();
    }, [open]);

    // Auto-load table when tab changes
    useEffect(() => {
        if (!open) return;
        if (!activeTab) return;
        if (tableData[activeTab]) return;
        fetchTable(activeTab);
    }, [activeTab, open, dbTables]);

    const fetchTable = async (key) => {
        const table = dbTables.find(t => t.key === key);
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

    const handleSeedData = async () => {
        if (!window.confirm("This will generate sample highway usage for ALL vehicles for the last month and trigger bill generation. Proceed?")) return;
        
        setLoadingTab(prev => ({ ...prev, [activeTab]: true }));
        try {
            // 1. Populate usage
            const usageRes = await postRequest('/admin/populate-usage');
            alert(usageRes.message || "Usage data populated!");
            
            // 2. Generate bills
            const billRes = await postRequest('/admin/generate-bills');
            alert(billRes.message || "Bills generated!");
            
            // Refresh current view if it's bills or usage
            if (activeTab === 'bills' || activeTab === 'highway_usage') {
                handleRefresh();
            }
        } catch (err) {
            alert("Failed to seed data: " + (err.message || "Check console"));
        } finally {
            setLoadingTab(prev => ({ ...prev, [activeTab]: false }));
        }
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
                        onClick={handleSeedData}
                        title="Populate Sample Monthly Data"
                        style={{ background: 'rgba(78, 205, 196, 0.2)', color: '#4ecdc4' }}
                    >⚡ Seed</button>
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
                        {dbTables.map(t => (
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
