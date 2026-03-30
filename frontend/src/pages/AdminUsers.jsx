import { useState, useEffect } from 'react';
import { getRequest, deleteRequest, postRequest, putRequest } from '../services/api';
import LoadingFallback from '../components/LoadingFallback';
import './AdminUsersStyles.css';

import AdminUsersTable from '../components/admin/AdminUsersTable';
import AdminUserFormModal from '../components/admin/AdminUserFormModal';
import AdminUserProfileModal from '../components/admin/AdminUserProfileModal';

function AdminUsers() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Modal State
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState('add');
    const [formData, setFormData] = useState({ userId: null, name: '', email: '', phoneNumber: '' });
    const [actionLoading, setActionLoading] = useState(false);

    // View Profile State
    const [viewingUser, setViewingUser] = useState(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const data = await getRequest('/users');
            setUsers(Array.isArray(data) ? data : []);
        } catch (err) {
            setError('Failed to load users from server.');
        } finally {
            setLoading(false);
        }
    };

    const handleAddClick = () => {
        setModalMode('add');
        setFormData({ userId: null, name: '', email: '', phoneNumber: '' });
        setShowModal(true);
    };

    const handleEditClick = (user) => {
        setModalMode('edit');
        setFormData({ ...user });
        setShowModal(true);
    };

    const handleModalSubmit = async (e) => {
        e.preventDefault();
        setActionLoading(true);
        try {
            if (modalMode === 'add') {
                await postRequest('/users', formData);
            } else {
                await putRequest(`/users/${formData.userId}`, formData);
            }
            setShowModal(false);
            fetchUsers(); // Refresh the list
        } catch (err) {
            alert(`Failed to ${modalMode} user. Please verify details and try again.`);
        } finally {
            setActionLoading(false);
        }
    };

    const handleDelete = async (id, name) => {
        if (!window.confirm(`Are you sure you want to delete user "${name}"? This action cannot be undone.`)) {
            return;
        }

        try {
            await deleteRequest(`/users/${id}`);
            // Remove user from local state to update UI immediately
            setUsers(users.filter(u => u.userId !== id));
        } catch (err) {
            alert('Failed to delete user. Please try again.');
        }
    };

    if (loading) return <LoadingFallback />;

    return (
        <div className="page admin-users-page">
            <h2>👥 User Management</h2>
            
            {error && (
                <div className="error-message">
                    <p>⚠️ {error}</p>
                    <button className="btn btn-secondary" onClick={fetchUsers}>Retry</button>
                </div>
            )}

            <AdminUsersTable 
                users={users}
                handleAddClick={handleAddClick}
                fetchUsers={fetchUsers}
                setViewingUser={setViewingUser}
                handleEditClick={handleEditClick}
                handleDelete={handleDelete}
            />

            <AdminUserFormModal 
                showModal={showModal}
                setShowModal={setShowModal}
                modalMode={modalMode}
                formData={formData}
                setFormData={setFormData}
                handleModalSubmit={handleModalSubmit}
                actionLoading={actionLoading}
            />

            <AdminUserProfileModal 
                viewingUser={viewingUser}
                setViewingUser={setViewingUser}
            />
        </div>
    );
}

export default AdminUsers;
