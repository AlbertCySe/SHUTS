import React from 'react';

const BILLS_PER_PAGE = 6;

function BillsTable({ bills, vehicles, loading, onExportCSV, onPayBill }) {
    const [billFilter, setBillFilter] = React.useState('All');
    const [vehicleFilter, setVehicleFilter] = React.useState('All');
    const [currentPage, setCurrentPage] = React.useState(1);

    const filteredBills = bills.filter(b => {
        const statusMatch = billFilter === 'All' || (b.isPaid ? 'PAID' : 'PENDING') === billFilter || b.status === billFilter;
        const vehicleMatch = vehicleFilter === 'All' || String(b.vehicle?.vehicleId || b.vehicleId) === vehicleFilter;
        return statusMatch && vehicleMatch;
    });

    const totalPages = Math.ceil(filteredBills.length / BILLS_PER_PAGE);
    const pagedBills = filteredBills.slice((currentPage - 1) * BILLS_PER_PAGE, currentPage * BILLS_PER_PAGE);

    const changeFilter = (setter) => (e) => { setter(e.target.value); setCurrentPage(1); };

    const selectStyle = { padding: '8px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', fontSize: '13px' };

    return (
        <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px' }}>
                <h3 style={{ margin: 0 }}>📄 My Bills ({filteredBills.length})</h3>
                <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', alignItems: 'center' }}>
                    <select value={billFilter} onChange={changeFilter(setBillFilter)} style={selectStyle}>
                        <option value="All">All Status</option>
                        <option value="PAID">✅ Paid</option>
                        <option value="PENDING">⏳ Pending</option>
                    </select>
                    {vehicles.length > 0 && (
                        <select value={vehicleFilter} onChange={changeFilter(setVehicleFilter)} style={selectStyle}>
                            <option value="All">All Vehicles</option>
                            {vehicles.map(v => (
                                <option key={v.vehicleId} value={String(v.vehicleId)}>{v.vehicleNumber}</option>
                            ))}
                        </select>
                    )}
                    {filteredBills.length > 0 && (
                        <button onClick={() => onExportCSV(filteredBills)}
                            style={{ padding: '8px 14px', borderRadius: '8px', border: 'none', background: '#27ae60', color: 'white', cursor: 'pointer', fontSize: '13px' }}>
                            📥 Export CSV
                        </button>
                    )}
                </div>
            </div>

            {loading ? <p className="info-text" style={{ marginTop: '16px' }}>Loading bills...</p>
                : pagedBills.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '30px', color: '#7f8c8d' }}>
                        <div style={{ fontSize: '40px' }}>🧾</div>
                        <p>No bills found. Generate your first bill above!</p>
                    </div>
                ) : (
                    <>
                        <div className="table-responsive" style={{ marginTop: '16px' }}>
                            <table className="custom-data-table">
                                <thead>
                                    <tr>
                                        <th>#</th><th>Month</th><th>Vehicle</th>
                                        <th>Distance</th><th>Amount</th><th>Status</th><th>Due Date</th><th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {pagedBills.map((bill, i) => {
                                        const isPaid = bill.isPaid || bill.status === 'PAID';
                                        return (
                                            <tr key={bill.billId || i}>
                                                <td><span className="admin-id-pill">#{(currentPage - 1) * BILLS_PER_PAGE + i + 1}</span></td>
                                                <td>{bill.billMonth || 'N/A'}</td>
                                                <td>{
                                                    bill.vehicle?.vehicleNumber || 
                                                    bill.vehicleNumber || 
                                                    (bill.vehicleId && vehicles ? vehicles.find(v => v.vehicleId === bill.vehicleId || v.id === bill.vehicleId)?.number || vehicles.find(v => v.vehicleId === bill.vehicleId || v.id === bill.vehicleId)?.vehicleNumber : null) || 
                                                    (bill.vehicleId ? `Vehicle #${bill.vehicleId}` : 'Consolidated (All Vehicles)')
                                                }</td>
                                                <td>{parseFloat(bill.totalDistance || 0).toFixed(2)} km</td>
                                                <td style={{ fontWeight: 'bold', color: '#8e44ad' }}>₹{parseFloat(bill.totalAmount || 0).toFixed(2)}</td>
                                                <td>
                                                    <span style={{
                                                        padding: '3px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold',
                                                        background: isPaid ? 'rgba(39,174,96,0.12)' : 'rgba(231,76,60,0.1)',
                                                        color: isPaid ? '#27ae60' : '#e74c3c'
                                                    }}>
                                                        {isPaid ? '✅ Paid' : '⏳ Pending'}
                                                    </span>
                                                </td>
                                                <td style={{ color: '#7f8c8d', fontSize: '13px' }}>
                                                    {bill.dueDate ? new Date(bill.dueDate).toLocaleDateString('en-IN') : 'N/A'}
                                                </td>
                                                <td>
                                                    {!isPaid && (
                                                        <button 
                                                            onClick={() => onPayBill && onPayBill(bill.billId)}
                                                            className="btn btn-primary"
                                                            style={{ padding: '6px 12px', fontSize: '12px', borderRadius: '6px' }}
                                                        >
                                                            💸 Pay Now
                                                        </button>
                                                    )}
                                                </td>
                                            </tr>
                                        )
                                    })}
                                </tbody>
                            </table>
                        </div>
                        {totalPages > 1 && (
                            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '12px', marginTop: '16px' }}>
                                <button onClick={() => setCurrentPage(p => p - 1)} disabled={currentPage === 1}
                                    style={{ padding: '6px 14px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', cursor: 'pointer' }}>← Prev</button>
                                <span style={{ fontSize: '13px', color: '#555' }}>Page {currentPage} of {totalPages}</span>
                                <button onClick={() => setCurrentPage(p => p + 1)} disabled={currentPage === totalPages}
                                    style={{ padding: '6px 14px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', cursor: 'pointer' }}>Next →</button>
                            </div>
                        )}
                    </>
                )}
        </div>
    );
}

export default BillsTable;
