// State
let allData = [];       // live positions and metadata from IoT simulator H2 DB
let trackingId = null;

// Clock
function updateClock() {
    const now = new Date();
    document.getElementById('clock').textContent =
        now.toLocaleDateString('en-IN') + '  ' + now.toLocaleTimeString('en-IN');
}
setInterval(updateClock, 1000);
updateClock();

// Fetch all data
async function fetchData() {
    try {
        // Fetch live positions and metadata from this simulator H2 DB
        const simRes = await fetch('/api/simulation/vehicles');
        if (simRes.ok) {
            allData = await simRes.json();
        }

        renderStats();
        renderTable();
        document.getElementById('loading').style.display = 'none';
        document.getElementById('stat-sync').textContent = new Date().toLocaleTimeString('en-IN');
    } catch (e) {
        console.error('Fetch error:', e);
    }
}


function renderStats() {
    const running = allData.filter(v => v.iotStatus === 'RUNNING').length;
    const parked = allData.filter(v => v.iotStatus !== 'RUNNING').length;
    const onHW = allData.filter(v => v.isOnHighway).length;
    const speeds = allData.filter(v => v.speedKmH != null && v.iotStatus === 'RUNNING').map(v => v.speedKmH);
    const avgSpeed = speeds.length ? (speeds.reduce((a, b) => a + b, 0) / speeds.length).toFixed(1) : '—';

    document.getElementById('stat-total').textContent = allData.length;
    document.getElementById('stat-running').textContent = running;
    document.getElementById('stat-parked').textContent = parked;
    document.getElementById('stat-avg-speed').textContent = avgSpeed + ' km/h';
    document.getElementById('stat-highway').textContent = onHW;
}

function speedColor(s) {
    if (s == null) return '';
    if (s < 30) return 'speed-low';
    if (s < 80) return 'speed-mid';
    if (s < 120) return 'speed-high';
    return 'speed-danger';
}

function statusBadge(st) {
    if (!st || st === 'PARKED') return `<span class="status-badge status-parked"><span class="status-dot"></span>Parked</span>`;
    if (st === 'RUNNING' || st === 'DRIVING' || st === 'TRAFFIC' || st === 'STOPPED') return `<span class="status-badge status-running"><span class="status-dot"></span>Running</span>`;
    return `<span class="status-badge status-break"><span class="status-dot"></span>Break</span>`;
}

function renderTable() {
    const search = document.getElementById('search').value.toLowerCase();
    const filterStatus = document.getElementById('filter-status').value;

    const filtered = allData.filter(v => {
        const matchSearch = v.vehicleNumber.toLowerCase().includes(search)
            || v.ownerName.toLowerCase().includes(search)
            || (v.routeName || '').toLowerCase().includes(search);
        const matchStatus = filterStatus === 'all' || v.iotStatus === filterStatus
            || (filterStatus === 'RUNNING' && ['RUNNING','DRIVING','TRAFFIC','STOPPED'].includes(v.iotStatus));
        return matchSearch && matchStatus;
    });

    const tbody = document.getElementById('vehicle-tbody');
    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="11" class="no-data">No vehicles match your filter.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map((v, i) => `
        <tr>
            <td style="color:#475569">${i + 1}</td>
            <td class="vehicle-num">${v.vehicleNumber}</td>
            <td><span class="vehicle-type">${v.vehicleType}</span></td>
            <td class="owner-cell">${v.ownerName}</td>
            <td>${statusBadge(v.iotStatus)}</td>
            <td class="speed-val ${speedColor(v.speedKmH)}">
                ${v.speedKmH != null ? Math.round(v.speedKmH) + ' km/h' : '—'}
            </td>
            <td class="route-name" title="${v.routeName}">${v.routeName}</td>
            <td class="coords">
                ${v.latitude != null ? v.latitude.toFixed(4) + ', ' + v.longitude.toFixed(4) : '—'}
            </td>
            <td class="${v.isOnHighway ? 'hw-yes' : 'hw-no'}">
                ${v.isOnHighway ? '🛣️ Yes' : 'No'}
            </td>
            <td class="ts-cell">
                ${v.timestamp ? new Date(v.timestamp).toLocaleTimeString('en-IN') : '—'}
            </td>
            <td>
                <button class="btn btn-primary btn-sm" onclick="openTrack(${v.vehicleId})">📡 Track</button>
            </td>
        </tr>
    `).join('');
}

// Track modal
function openTrack(id) {
    trackingId = id;
    renderModal();
    document.getElementById('modal').classList.add('open');
}
function closeModal() {
    document.getElementById('modal').classList.remove('open');
    trackingId = null;
}

function renderModal() {
    const v = allData.find(x => x.vehicleId === trackingId);
    if (!v) return;
    document.getElementById('modal-title').textContent = `📡 Tracking: ${v.vehicleNumber}`;
    document.getElementById('modal-body').innerHTML = `
        <div class="modal-stat">
            <div class="modal-stat-label">Vehicle Number</div>
            <div class="modal-stat-val">${v.vehicleNumber}</div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">Owner</div>
            <div class="modal-stat-val" style="color:#a78bfa">${v.ownerName}</div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">Type</div>
            <div class="modal-stat-val">${v.vehicleType}</div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">IoT Status</div>
            <div class="modal-stat-val">${statusBadge(v.iotStatus)}</div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">Current Speed</div>
            <div class="modal-stat-val ${speedColor(v.speedKmH)}" style="font-size:28px">
                ${v.speedKmH != null ? Math.round(v.speedKmH) + ' km/h' : '—'}
            </div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">Route</div>
            <div class="modal-stat-val" style="font-size:14px; color:#cbd5e1">${v.routeName}</div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">On Highway</div>
            <div class="modal-stat-val ${v.isOnHighway ? 'hw-yes' : 'hw-no'}">
                ${v.isOnHighway ? '🛣️ Yes — National Highway' : '🏙️ Off-Highway'}
            </div>
        </div>
        <div class="modal-stat">
            <div class="modal-stat-label">Last Broadcast</div>
            <div class="modal-stat-val" style="font-size:13px; color:#94a3b8">
                ${v.timestamp ? new Date(v.timestamp).toLocaleString('en-IN') : '—'}
            </div>
        </div>
        <div class="modal-stat" style="grid-column: span 2">
            <div class="modal-stat-label">GPS Coordinates</div>
            <div class="modal-stat-val coords" style="font-size:15px; color:#fff">
                ${v.latitude != null ? `📍 ${v.latitude.toFixed(6)}, ${v.longitude.toFixed(6)}` : '—'}
            </div>
            ${v.latitude != null ? `<a href="https://www.google.com/maps?q=${v.latitude},${v.longitude}" target="_blank"
                style="display:inline-block; margin-top:8px; color:#667eea; font-size:12px; font-weight:600;">
                ↗ Open in Google Maps</a>` : ''}
        </div>
    `;
}

// Control buttons
async function startAll() {
    await fetch('/api/simulation/start-all', { method: 'POST' });
    await fetchData();
}
async function stopAll() {
    await fetch('/api/simulation/stop-all', { method: 'POST' });
    await fetchData();
}

// ── Settings Drawer ────────────────────────────────────
let currentSettings = null;

function openSettings() {
    document.getElementById('settings-drawer').classList.add('open');
    document.getElementById('drawer-overlay').classList.add('open');
    loadSettings();
}
function closeSettings() {
    document.getElementById('settings-drawer').classList.remove('open');
    document.getElementById('drawer-overlay').classList.remove('open');
}

async function loadSettings() {
    document.getElementById('settings-form').innerHTML = '<div style="color:#64748b; font-size:13px;">Loading…</div>';
    try {
        const res = await fetch('/api/iot/settings');
        currentSettings = await res.json();
        renderSettingsForm();
    } catch {
        document.getElementById('settings-form').innerHTML = '<div style="color:#e74c3c; font-size:13px;">⚠️ Could not load settings.</div>';
    }
}

function numField(section, key, label, step) {
    const val = currentSettings[section]?.[key] ?? '';
    return `<label class="settings-field">
        <span>${label}</span>
        <input type="number" step="${step || 1}" value="${val}"
            onchange="setSetting('${section}','${key}',this.value)" />
    </label>`;
}
function selField(section, key, label, options) {
    const val = currentSettings[section]?.[key] ?? '';
    const opts = options.map(o => `<option value="${o}"${val===o?' selected':''}>${o}</option>`).join('');
    return `<label class="settings-field">
        <span>${label}</span>
        <select onchange="setSetting('${section}','${key}',this.value)">${opts}</select>
    </label>`;
}
function togField(section, key, label) {
    const checked = currentSettings[section]?.[key] ? 'checked' : '';
    return `<label class="settings-toggle-row">
        <input type="checkbox" ${checked} onchange="setSetting('${section}','${key}',this.checked)" />
        <span>${label}</span>
    </label>`;
}
function section(title, fields) {
    return `<div class="settings-section">
        <div class="settings-section-title">${title}</div>
        <div class="settings-grid">${fields}</div>
    </div>`;
}

function renderSettingsForm() {
    const s = currentSettings;
    document.getElementById('settings-form').innerHTML = [
        section('Lifecycle', [
            numField('lifecycle','lifecycleIntervalMs','Lifecycle interval (ms)'),
            numField('lifecycle','parkedStartProbability','Parked start prob.',0.01),
            numField('lifecycle','runningPauseProbability','Running pause prob.',0.01),
            numField('lifecycle','breakResumeProbability','Break resume prob.',0.01),
        ].join('')),
        section('Movement', [
            numField('movement','movementTickIntervalMs','Tick interval (ms)'),
            numField('movement','backendBroadcastIntervalMs','Broadcast interval (ms)'),
            numField('movement','stateSaveIntervalMs','State save interval (ms)'),
        ].join('')),
        section('Routes', [
            selField('route','routeSelectionMode','Route selection',['RANDOM','FIRST_ROUTE']),
            numField('route','completionWaypointOffset','Completion offset'),
            togField('route','useOsrmRouteGeometry','Use OSRM geometry') +
            togField('route','fallbackToWaypointsOnOsrmFailure','Fallback to waypoints'),
        ].join('')),
        section('Driving Behavior', [
            numField('drivingBehavior','wanderOffRouteProbability','Wander probability',0.01),
            numField('drivingBehavior','wanderMaxOffsetDegrees','Max wander offset°',0.001),
            numField('drivingBehavior','wanderSpeedMinKmH','Wander speed min',0.1),
            numField('drivingBehavior','wanderSpeedMaxKmH','Wander speed max',0.1),
            numField('drivingBehavior','highwayStayTicksMin','Highway stay min ticks'),
            numField('drivingBehavior','highwayStayTicksMax','Highway stay max ticks'),
            numField('drivingBehavior','wanderTicksMin','Wander ticks min'),
            numField('drivingBehavior','wanderTicksMax','Wander ticks max'),
        ].join('')),
        section('Speed & Status', [
            numField('speedStatus','drivingProbability','Driving prob.',0.01),
            numField('speedStatus','trafficProbability','Traffic prob.',0.01),
            numField('speedStatus','drivingSpeedMinKmH','Driving speed min',0.1),
            numField('speedStatus','drivingSpeedMaxKmH','Driving speed max',0.1),
            numField('speedStatus','trafficSpeedMinKmH','Traffic speed min',0.1),
            numField('speedStatus','trafficSpeedMaxKmH','Traffic speed max',0.1),
            numField('speedStatus','stoppedSpeedKmH','Stopped speed',0.1),
        ].join('')),
        section('Highway Detection', [
            numField('highwayDetection','highwayToleranceKm','Tolerance (km)',0.1),
        ].join('')),
    ].join('');
}

function setSetting(section, key, value) {
    if (!currentSettings[section]) currentSettings[section] = {};
    if (value === true || value === false) {
        currentSettings[section][key] = value;
    } else if (value !== '' && !isNaN(Number(value))) {
        currentSettings[section][key] = Number(value);
    } else {
        currentSettings[section][key] = value;
    }
}

async function saveSettings() {
    showDrawerMsg('', '');
    try {
        const res = await fetch('/api/iot/settings', {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(currentSettings)
        });
        if (res.ok) {
            currentSettings = await res.json();
            showDrawerMsg('✅ Settings saved successfully.', 'success');
        } else {
            showDrawerMsg('⚠️ Failed to save settings.', 'error');
        }
    } catch { showDrawerMsg('⚠️ Could not reach simulator.', 'error'); }
}

async function resetSettings() {
    showDrawerMsg('', '');
    try {
        const res = await fetch('/api/iot/settings/reset', { method: 'POST' });
        if (res.ok) {
            currentSettings = await res.json();
            renderSettingsForm();
            showDrawerMsg('✅ Defaults restored.', 'success');
        }
    } catch { showDrawerMsg('⚠️ Could not reset settings.', 'error'); }
}

function showDrawerMsg(text, type) {
    const el = document.getElementById('drawer-msg');
    el.innerHTML = text ? `<div class="drawer-msg ${type}">${text}</div>` : '';
    if (text) setTimeout(() => el.innerHTML = '', 4000);
}

// Auto-refresh every 3 seconds
fetchData();
setInterval(() => {
    fetchData();
    if (trackingId) renderModal();
}, 3000);
