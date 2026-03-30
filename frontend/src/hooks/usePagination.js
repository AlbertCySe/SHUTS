import { useState, useMemo } from 'react';

/**
 * usePagination – generic client-side pagination hook.
 * @param {Array} items   Full list of items to paginate
 * @param {number} perPage Rows per page (default 10)
 * @returns { page, setPage, totalPages, paged, rangeLabel }
 */
export function usePagination(items = [], perPage = 10) {
    const [page, setPage] = useState(1);

    const totalPages = Math.max(1, Math.ceil(items.length / perPage));

    // Auto-clamp page when data/filter changes
    const safePage = Math.min(page, totalPages);
    if (safePage !== page) setPage(safePage);

    const paged = useMemo(() =>
        items.slice((safePage - 1) * perPage, safePage * perPage),
        [items, safePage, perPage]
    );

    const start = items.length === 0 ? 0 : (safePage - 1) * perPage + 1;
    const end = Math.min(safePage * perPage, items.length);
    const rangeLabel = items.length === 0 ? 'No records' : `${start}–${end} of ${items.length}`;

    return { page: safePage, setPage, totalPages, paged, rangeLabel };
}
