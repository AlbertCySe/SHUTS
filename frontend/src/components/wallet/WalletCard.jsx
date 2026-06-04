import React, { useState } from 'react';

const QUICK_AMOUNTS = [100, 250, 500, 1000, 2000];
const LOW_BALANCE_THRESHOLD = 100;

function WalletCard({ wallet, loading, addAmount, setAddAmount, addLoading, addMessage, onRechargeRequested }) {
    const [showUpi, setShowUpi] = useState(false);

    if (loading) return <div className="card" style={{ marginBottom: '20px' }}><p className="info-text">Loading wallet...</p></div>;
    if (!wallet) return <div className="card" style={{ marginBottom: '20px' }}><p className="info-text">Wallet not available. Use highways first.</p></div>;

    const balance = wallet.balance || 0;
    const isLow = balance < LOW_BALANCE_THRESHOLD && !wallet.notInitialized;

    const handleUpiCheckout = (amt) => {
        if (amt) setAddAmount(amt);
        setShowUpi(true);
    };

    const confirmRecharge = () => {
        onRechargeRequested(addAmount);
        setShowUpi(false);
    };

    return (
        <div className="card" style={{ marginBottom: '20px' }}>
            <h3>🏦 My Wallet</h3>

            {isLow && (
                <div style={{ background: '#fff3cd', border: '1px solid #ffc107', borderRadius: '8px', padding: '10px 16px', marginBottom: '14px', color: '#856404' }}>
                    ⚠️ Low balance! Your wallet is below ₹{LOW_BALANCE_THRESHOLD}.
                </div>
            )}

            <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap', alignItems: 'center', marginBottom: '20px' }}>
                {/* Balance Display */}
                <div style={{ textAlign: 'center', padding: '16px 28px', background: 'linear-gradient(135deg, #667eea, #764ba2)', borderRadius: '12px', color: 'white', minWidth: '160px' }}>
                    <div style={{ fontSize: '13px', opacity: 0.8 }}>Current Balance</div>
                    <div style={{ fontSize: '28px', fontWeight: '800', marginTop: '4px' }}>
                        ₹{balance.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </div>
                    {wallet.notInitialized && <div style={{ fontSize: '11px', opacity: 0.7, marginTop: '4px' }}>Not initialized yet</div>}
                </div>

                {/* Recharge Request */}
                <div style={{ flex: 1 }}>
                    <p style={{ fontWeight: '600', marginBottom: '10px' }}>⚡ Recharge Wallet</p>
                    {!showUpi ? (
                        <>
                            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '12px' }}>
                                {QUICK_AMOUNTS.map(amt => (
                                    <button key={amt} onClick={() => handleUpiCheckout(amt)} disabled={addLoading}
                                        style={{ padding: '6px 14px', borderRadius: '20px', border: '1px solid #667eea', background: 'white', color: '#667eea', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}>
                                        +₹{amt}
                                    </button>
                                ))}
                            </div>
                            <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                                <input type="number" min="1" placeholder="Custom amount (₹)" value={addAmount}
                                    onChange={e => setAddAmount(e.target.value)}
                                    style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd', width: '180px' }} />
                                <button className="btn btn-primary" onClick={() => handleUpiCheckout(addAmount)} disabled={addLoading || !addAmount}>
                                    Recharge via UPI
                                </button>
                            </div>
                        </>
                    ) : (
                        <div style={{ border: '2px dashed #9b59b6', padding: '16px', borderRadius: '12px', textAlign: 'center', background: '#fcf3ff' }}>
                            <h4 style={{ margin: '0 0 10px 0', color: '#8e44ad' }}>Scan via UPI to pay ₹{addAmount}</h4>
                            <div style={{ margin: '0 auto 10px', width: '100px', height: '100px', background: '#fff', border: '1px solid #ddd', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <span style={{ fontSize: '40px' }}>📱</span>
                            </div>
                            <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                                <button onClick={() => setShowUpi(false)} style={{ padding: '8px 16px', borderRadius: '8px', border: 'none', background: '#ccc', cursor: 'pointer' }}>Cancel</button>
                                <button onClick={confirmRecharge} disabled={addLoading} style={{ padding: '8px 16px', borderRadius: '8px', border: 'none', background: '#27ae60', color: 'white', cursor: 'pointer', fontWeight: 'bold' }}>
                                    {addLoading ? 'Processing...' : 'I have paid!'}
                                </button>
                            </div>
                        </div>
                    )}
                    {addMessage && (
                        <div style={{ marginTop: '8px', color: addMessage.type === 'success' ? '#27ae60' : '#e74c3c', fontSize: '13px', fontWeight: 'bold' }}>
                            {addMessage.text}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default WalletCard;
