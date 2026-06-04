import React, { useState, useCallback } from 'react';
import AdminVehicleRequests from '../components/admin/AdminVehicleRequests';
import AdminVehiclesTable from '../components/admin/AdminVehiclesTable';
import AdminVehicleModal from '../components/admin/AdminVehicleModal';
import AdminVehicleTrackingModal from '../components/admin/AdminVehicleTrackingModal';
import './AdminUsersStyles.css';

function AdminVehicles() {
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    
    // Modal State
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState('add');
    const [formData, setFormData] = useState({ vehicleId: null, vehicleNumber: '', vehicleType: 'CAR', userId: '' });
    const [trackingVehicle, setTrackingVehicle] = useState(null);

    const triggerRefreshAll = useCallback(() => {
        setRefreshTrigger(prev => prev + 1);
    }, []);

    const handleAddTrigger = useCallback(() => {
        setModalMode('add');
        setFormData({ vehicleId: null, vehicleNumber: '', vehicleType: 'CAR', userId: '' });
        setShowModal(true);
    }, []);

    const handleEditTrigger = useCallback((vehicle) => {
        setModalMode('edit');
        setFormData({ 
            vehicleId: vehicle.vehicleId, 
            vehicleNumber: vehicle.vehicleNumber, 
            vehicleType: vehicle.vehicleType,
            userId: vehicle.user?.userId || ''
        });
        setShowModal(true);
    }, []);

    return (
        <div className="page admin-users-page">
            <h2>🚗 Vehicle Management</h2>

            <AdminVehicleRequests 
                refreshTrigger={refreshTrigger} 
                onActionCompleted={triggerRefreshAll} 
            />

            <AdminVehiclesTable 
                refreshTrigger={refreshTrigger}
                onAddTrigger={handleAddTrigger}
                onEditTrigger={handleEditTrigger}
                onTrackTrigger={setTrackingVehicle}
            />

            <AdminVehicleModal 
                show={showModal}
                mode={modalMode}
                initialData={formData}
                onClose={() => setShowModal(false)}
                onSuccess={triggerRefreshAll}
            />

            <AdminVehicleTrackingModal
                vehicle={trackingVehicle}
                onClose={() => setTrackingVehicle(null)}
            />
        </div>
    );
}

export default AdminVehicles;
