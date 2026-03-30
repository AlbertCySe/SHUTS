import React from 'react';

/**
 * Paginator – reusable pagination control bar.
 * Props: page, totalPages, rangeLabel, onPageChange
 */
function Paginator({ page, totalPages, rangeLabel, onPageChange }) {
    if (totalPages <= 1) return null;

    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);
    // Show at most 5 page buttons around the current page
    const visible = pages.filter(p =>
        p === 1 || p === totalPages || Math.abs(p - page) <= 2
    );

    const btnBase = {
        padding: '5px 10px', borderRadius: '6px', border: '1px solid #ddd',
        background: 'white', cursor: 'pointer', fontSize: '13px', minWidth: '34px'
    };
    const activeBtn = { ...btnBase, background: 'linear-gradient(135deg, #667eea, #764ba2)', color: 'white', border: 'none', fontWeight: '700' };
    const disabledBtn = { ...btnBase, color: '#ccc', cursor: 'default' };

    return (
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '16px', flexWrap: 'wrap', gap: '8px' }}>
            <span style={{ fontSize: '13px', color: '#7f8c8d' }}>{rangeLabel}</span>
            <div style={{ display: 'flex', gap: '4px', alignItems: 'center' }}>
                {/* Prev */}
                <button style={page === 1 ? disabledBtn : btnBase}
                    onClick={() => onPageChange(page - 1)} disabled={page === 1}>‹</button>

                {/* Page number buttons with ellipsis */}
                {visible.map((p, idx) => {
                    const prev = visible[idx - 1];
                    const showEllipsis = prev && p - prev > 1;
                    return (
                        <React.Fragment key={p}>
                            {showEllipsis && <span style={{ padding: '0 4px', color: '#aaa' }}>…</span>}
                            <button style={p === page ? activeBtn : btnBase} onClick={() => onPageChange(p)}>{p}</button>
                        </React.Fragment>
                    );
                })}

                {/* Next */}
                <button style={page === totalPages ? disabledBtn : btnBase}
                    onClick={() => onPageChange(page + 1)} disabled={page === totalPages}>›</button>
            </div>
        </div>
    );
}

export default Paginator;
