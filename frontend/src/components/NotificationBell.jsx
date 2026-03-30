import React, { useState, useEffect, useRef } from 'react';
import { getRequest, putRequest } from '../services/api';
import { getSession } from '../services/auth';

function NotificationBell() {
    const session = getSession();
    const userId = session?.userId;

    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [open, setOpen] = useState(false);
    const [dropdownPos, setDropdownPos] = useState({ top: 0, right: 0 });
    const bellRef = useRef(null);

    useEffect(() => {
        if (!userId) return;
        fetchUnreadCount();
        const interval = setInterval(fetchUnreadCount, 30000);
        return () => clearInterval(interval);
    }, [userId]);

    // Close on outside click
    useEffect(() => {
        const handler = (e) => {
            if (bellRef.current && !bellRef.current.contains(e.target)) setOpen(false);
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    const fetchUnreadCount = async () => {
        try {
            const data = await getRequest(`/notifications/user/${userId}/unread-count`);
            setUnreadCount(data?.count || 0);
        } catch { setUnreadCount(0); }
    };

    const fetchNotifications = async () => {
        try {
            const data = await getRequest(`/notifications/user/${userId}`);
            setNotifications(Array.isArray(data) ? data : []);
        } catch { setNotifications([]); }
    };

    const handleOpen = async () => {
        if (!open && bellRef.current) {
            // Calculate position from bell button's screen coordinates
            const rect = bellRef.current.getBoundingClientRect();
            setDropdownPos({
                top: rect.bottom + 8,
                right: window.innerWidth - rect.right
            });
        }
        const next = !open;
        setOpen(next);
        if (next) await fetchNotifications();
    };

    const markRead = async (id) => {
        try {
            await putRequest(`/notifications/${id}/read`, {});
            setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch { /* silent */ }
    };

    const markAllRead = async () => {
        try {
            await putRequest(`/notifications/user/${userId}/read-all`, {});
            setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
            setUnreadCount(0);
        } catch { /* silent */ }
    };

    return (
        <div ref={bellRef} style={{ position: 'relative', display: 'inline-flex', alignItems: 'center' }}>
            {/* Bell Button */}
            <button
                onClick={handleOpen}
                title="Notifications"
                style={{
                    background: 'none', border: 'none', cursor: 'pointer',
                    position: 'relative', fontSize: '20px', padding: '4px 6px', lineHeight: 1,
                    display: 'flex', alignItems: 'center'
                }}>
                🔔
                {unreadCount > 0 && (
                    <span style={{
                        position: 'absolute', top: '-2px', right: '-2px',
                        background: '#e74c3c', color: 'white', borderRadius: '50%',
                        fontSize: '10px', fontWeight: '800', minWidth: '16px', height: '16px',
                        display: 'flex', alignItems: 'center', justifyContent: 'center', lineHeight: 1
                    }}>{unreadCount}</span>
                )}
            </button>

            {/* Dropdown — position:fixed escapes the nav bar's stacking context */}
            {open && (
                <div style={{
                    position: 'fixed',
                    top: dropdownPos.top,
                    right: dropdownPos.right,
                    width: '340px',
                    background: 'white',
                    borderRadius: '12px',
                    boxShadow: '0 12px 40px rgba(0,0,0,0.18)',
                    zIndex: 99999,
                    border: '1px solid #eee',
                    overflow: 'hidden'
                }}>
                    {/* Header */}
                    <div style={{ padding: '12px 16px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#f8f9fa' }}>
                        <span style={{ fontWeight: '700', fontSize: '14px' }}>🔔 Notifications</span>
                        {unreadCount > 0 && (
                            <button onClick={markAllRead} style={{ fontSize: '12px', color: '#667eea', background: 'none', border: 'none', cursor: 'pointer', fontWeight: '600' }}>
                                Mark all read
                            </button>
                        )}
                    </div>

                    {/* List */}
                    <div style={{ maxHeight: '360px', overflowY: 'auto' }}>
                        {notifications.length === 0 ? (
                            <div style={{ padding: '30px', textAlign: 'center', color: '#7f8c8d' }}>
                                <div style={{ fontSize: '30px' }}>📭</div>
                                <p style={{ margin: '8px 0 0', fontSize: '13px' }}>No notifications yet</p>
                            </div>
                        ) : notifications.map(n => (
                            <div key={n.id}
                                onClick={() => !n.isRead && markRead(n.id)}
                                style={{
                                    padding: '12px 16px', borderBottom: '1px solid #f5f5f5',
                                    cursor: n.isRead ? 'default' : 'pointer',
                                    background: n.isRead ? 'white' : '#f0f4ff',
                                    transition: 'background 0.2s'
                                }}>
                                <div style={{ fontWeight: '600', fontSize: '13px', marginBottom: '4px' }}>{n.title}</div>
                                <div style={{ fontSize: '12px', color: '#555', lineHeight: 1.4 }}>{n.message}</div>
                                <div style={{ fontSize: '11px', color: '#aaa', marginTop: '6px', display: 'flex', gap: '8px' }}>
                                    <span>{new Date(n.createdAt).toLocaleString('en-IN')}</span>
                                    {!n.isRead && <span style={{ color: '#667eea', fontWeight: '700' }}>● Unread</span>}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}

export default NotificationBell;
