import React from 'react';

function BillGenerator({ vehicles, genMode, setGenMode, genVehicle, setGenVehicle,
    genCustomFrom, setGenCustomFrom, genCustomTo, setGenCustomTo,
    genLoading, genMessage, onGenerate }) {
    return (
        <div className="card" style={{ marginBottom: '20px' }}>
            <h3>🧾 Generate Bill</h3>
            <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'flex-end' }}>

                {/* Period Selector */}
                <div>
                    <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>Period</label>
                    <select value={genMode} onChange={e => setGenMode(e.target.value)}
                        style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd', background: 'white' }}>
                        <option value="daily">📅 Today (Daily)</option>
                        <option value="weekly">📆 This Week (Weekly)</option>
                        <option value="monthly">🗓️ This Month (Monthly)</option>
                        <option value="custom">🎯 Custom Range</option>
                    </select>
                </div>

                {/* Vehicle Selector */}
                {vehicles.length > 0 && (
                    <div>
                        <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>Vehicle</label>
                        <select value={genVehicle} onChange={e => setGenVehicle(e.target.value)}
                            style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd', background: 'white' }}>
                            <option value="">All Vehicles</option>
                            {vehicles.map(v => (
                                <option key={v.vehicleId} value={v.vehicleId}>{v.vehicleNumber}</option>
                            ))}
                        </select>
                    </div>
                )}

                {/* Custom Date Range */}
                {genMode === 'custom' && (
                    <>
                        <div>
                            <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>From</label>
                            <input type="date" value={genCustomFrom} onChange={e => setGenCustomFrom(e.target.value)}
                                style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd' }} />
                        </div>
                        <div>
                            <label style={{ display: 'block', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>To</label>
                            <input type="date" value={genCustomTo} onChange={e => setGenCustomTo(e.target.value)}
                                style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd' }} />
                        </div>
                    </>
                )}

                <button className="btn btn-primary" onClick={onGenerate} disabled={genLoading}>
                    {genLoading ? 'Generating...' : '⚡ Generate Bill'}
                </button>
            </div>

            {genMessage && (
                <div style={{ marginTop: '12px', color: genMessage.type === 'success' ? '#27ae60' : '#e74c3c', fontSize: '13px' }}>
                    {genMessage.text}
                </div>
            )}
        </div>
    );
}

export default BillGenerator;
