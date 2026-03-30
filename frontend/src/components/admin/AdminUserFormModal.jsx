export default function AdminUserFormModal({
    showModal, setShowModal, modalMode, formData, setFormData, handleModalSubmit, actionLoading
}) {
    if (!showModal) return null;

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '20px'
        }}>
            <div className="card" style={{ width: '450px', maxWidth: '100%', margin: 0, maxHeight: '90vh', overflowY: 'auto' }}>
                <h3 style={{ marginTop: 0 }}>{modalMode === 'add' ? '➕ Add New User' : '✏️ Edit User Details'}</h3>
                
                <form onSubmit={handleModalSubmit} className="form" style={{ marginTop: '20px' }}>
                    <div className="form-group" style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Full Name: *</label>
                        <input 
                            type="text" 
                            value={formData.name} 
                            onChange={e => setFormData({...formData, name: e.target.value})} 
                            style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }}
                            placeholder="Enter full name"
                            required
                            disabled={actionLoading}
                        />
                    </div>

                    <div className="form-group" style={{ marginBottom: '16px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Email Address: *</label>
                        <input 
                            type="email" 
                            value={formData.email} 
                            onChange={e => setFormData({...formData, email: e.target.value})} 
                            style={{ 
                                width: '100%', 
                                padding: '12px', 
                                borderRadius: '6px', 
                                border: '1px solid #ddd',
                                backgroundColor: modalMode === 'edit' ? '#f5f5f5' : 'white',
                                color: modalMode === 'edit' ? '#888' : 'inherit'
                            }}
                            placeholder="e.g. john@example.com"
                            required
                            disabled={actionLoading || modalMode === 'edit'}
                        />
                        {modalMode === 'edit' && (
                            <small style={{ color: '#7f8c8d', display: 'block', marginTop: '4px' }}>
                                Email serves as the login ID and cannot be changed.
                            </small>
                        )}
                    </div>

                    <div className="form-group" style={{ marginBottom: '24px' }}>
                        <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Phone Number: *</label>
                        <input 
                            type="tel" 
                            value={formData.phoneNumber} 
                            onChange={e => setFormData({...formData, phoneNumber: e.target.value})} 
                            style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid #ddd' }}
                            placeholder="e.g. 9876543210"
                            required
                            disabled={actionLoading}
                        />
                        <small style={{ color: '#7f8c8d', display: 'block', marginTop: '4px' }}>
                            Required for user authentication.
                        </small>
                    </div>

                    <div style={{ display: 'flex', gap: '12px' }}>
                        <button type="submit" className="btn btn-primary" style={{ flex: 1, padding: '12px' }} disabled={actionLoading}>
                            {actionLoading ? 'Saving...' : '💾 Save Details'}
                        </button>
                        <button type="button" className="btn btn-secondary" style={{ flex: 1, padding: '12px' }} onClick={() => setShowModal(false)} disabled={actionLoading}>
                            ✕ Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
