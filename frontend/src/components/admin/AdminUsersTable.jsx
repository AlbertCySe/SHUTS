import { usePagination } from '../../hooks/usePagination';
import Paginator from '../Paginator';

export default function AdminUsersTable({
    users,
    handleAddClick, fetchUsers, setViewingUser, 
    handleEditClick, handleDelete
}) {
    const { page, setPage, totalPages, paged: pagedUsers, rangeLabel } = usePagination(users, 10);

    return (
        <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h3 style={{ margin: 0 }}>Registered Users ({users.length})</h3>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button className="admin-btn-add" onClick={handleAddClick}>➕ Add User</button>
                    <button className="admin-btn-refresh" onClick={fetchUsers}>🔄 Refresh</button>
                </div>
            </div>

            <div className="table-responsive">
                <table className="custom-data-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone Number</th>
                            <th>Vehicles</th>
                            <th>Joined Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.length === 0 ? (
                            <tr>
                                <td colSpan="7" style={{ textAlign: 'center', padding: '24px' }}>
                                    No users found.
                                </td>
                            </tr>
                        ) : (
                            pagedUsers.map((user, i) => (
                                <tr key={user.userId}>
                                    <td><span className="admin-id-pill">#{(page - 1) * 10 + i + 1}</span></td>
                                    <td style={{ fontWeight: '500' }}>{user.name}</td>
                                    <td><a href={`mailto:${user.email}`} style={{ color: '#3498db', textDecoration: 'none' }}>{user.email}</a></td>
                                    <td>{user.phoneNumber}</td>
                                    <td>
                                        {user.vehicles && user.vehicles.length > 0 ? (
                                            <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
                                                {user.vehicles.map(v => (
                                                    <span key={v.vehicleId} style={{ 
                                                        background: '#e8f4f8', color: '#2980b9', 
                                                        padding: '3px 6px', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' 
                                                    }}>
                                                        {v.vehicleNumber}
                                                    </span>
                                                ))}
                                            </div>
                                        ) : (
                                            <span style={{ color: '#95a5a6', fontSize: '12px', fontStyle: 'italic' }}>None</span>
                                        )}
                                    </td>
                                    <td><span style={{ color: '#7f8c8d', fontSize: '13px' }}>{new Date(user.createdAt).toLocaleDateString()}</span></td>
                                    <td>
                                        <div className="admin-action-group">
                                            <button 
                                                className="admin-btn-sm" 
                                                style={{ background: '#3498db', color: 'white', border: 'none' }}
                                                onClick={() => setViewingUser(user)}
                                                title="View Profile"
                                            >
                                                👁️ View
                                            </button>
                                            <button 
                                                className="admin-btn-sm admin-btn-edit" 
                                                onClick={() => handleEditClick(user)}
                                                title="Edit User"
                                            >
                                                ✏️ Edit
                                            </button>
                                            <button 
                                                className="admin-btn-sm admin-btn-delete" 
                                                onClick={() => handleDelete(user.userId, user.name)}
                                                title="Delete User"
                                            >
                                                🗑️ Delete
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
            <Paginator page={page} totalPages={totalPages} rangeLabel={rangeLabel} onPageChange={setPage} />
        </div>
    );
}
