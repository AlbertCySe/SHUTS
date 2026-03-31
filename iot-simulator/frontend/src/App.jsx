import { useState, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import axios from 'axios'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet';

/**
 * 🛰️ Multi-Vehicle IoT Satellite Tracker
 * Visualizes 32+ vehicles simultaneously across Tamil Nadu
 */

// Custom Vehicle Icon
const createVehicleIcon = (status) => {
  let emoji = '🚗';
  if (status === 'TRAFFIC') emoji = '🚖';
  if (status === 'STOPPED') emoji = '🛑';
  
  return L.divIcon({
    html: `<div style="font-size: 24px; filter: drop-shadow(0 0 5px rgba(0,0,0,0.5))">${emoji}</div>`,
    className: 'vehicle-icon',
    iconSize: [30, 30],
    iconAnchor: [15, 15]
  });
};

// Target focus icon
const targetIcon = L.divIcon({
  html: '<div style="font-size: 28px; border: 2px solid #38bdf8; border-radius: 50%; padding: 2px; animation: pulse 1.5s infinite">🛰️</div>',
  className: 'target-icon',
  iconSize: [36, 36],
  iconAnchor: [18, 18]
});

// Component to smoothly pan the map tracking the selected vehicle
function MapTracker({ position, isTracking }) {
  const map = useMap();
  useEffect(() => {
    if (position && isTracking) {
      map.flyTo(position, map.getZoom(), { animate: true, duration: 1.0 });
    }
  }, [position, isTracking, map]);
  return null;
}

function App() {
  const [vehicles, setVehicles] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [isTracking, setIsTracking] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Poll the backend every 1.5 seconds for fresh coordinates of ALL vehicles
    const fetchTelemetry = () => {
      axios.get('http://localhost:8082/api/iot/live-locations')
        .then(response => {
          const data = Array.isArray(response.data) ? response.data : [response.data];
          setVehicles(data);
          
          // Auto-select first vehicle if none selected
          if (data.length > 0 && !selectedId) {
            setSelectedId(data[0].vehicleId);
          }
          setError(null);
        })
        .catch(err => {
          console.error("Backend unreachable", err);
          setError("Connection to IoT Hub Lost. Retrying...");
        });
    };

    fetchTelemetry();
    const intervalId = setInterval(fetchTelemetry, 1500);
    return () => clearInterval(intervalId);
  }, [selectedId]);

  const selectedVehicle = vehicles.find(v => v.vehicleId === selectedId) || (vehicles.length > 0 ? vehicles[0] : null);

  if (vehicles.length === 0 && !error) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#0f172a', color: 'white' }}>
        <div style={{ fontSize: '48px', marginBottom: '20px', animation: 'bounce 2s infinite' }}>🛰️</div>
        <h3>Scanning Tamil Nadu for Active Signals...</h3>
        <p style={{ color: '#94a3b8' }}>Awaiting data from IoT Cluster</p>
      </div>
    );
  }

  const defaultCenter = [11.1271, 78.6569]; // Tamil Nadu Center
  const trackingPos = selectedVehicle ? [selectedVehicle.latitude, selectedVehicle.longitude] : defaultCenter;

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-left">
          <h1>IoT Global Satellite Fleet</h1>
          <p style={{ margin: 0, fontSize: '0.8rem', color: '#64748b' }}>Active Sensors: {vehicles.length}</p>
        </div>
        
        {error ? (
          <div style={{ color: '#ef4444', fontWeight: 'bold' }}>⚠️ {error}</div>
        ) : selectedVehicle && (
          <div className="telemetry-panel">
            <div className="telemetry-box highlight">
              <div className="telemetry-label">Target ID</div>
              <div className="telemetry-value" style={{ color: '#38bdf8' }}>{selectedVehicle.vehicleId}</div>
            </div>
            <div className="telemetry-box">
              <div className="telemetry-label">Status</div>
              <div className="telemetry-value" style={{ 
                color: selectedVehicle.status === 'RUNNING' || selectedVehicle.status === 'DRIVING' ? '#4ade80' : 
                       selectedVehicle.status === 'TRAFFIC' ? '#facc15' : '#f87171' 
              }}>
                {selectedVehicle.status}
              </div>
            </div>
            <div className="telemetry-box">
              <div className="telemetry-label">Velocity</div>
              <div className="telemetry-value">{selectedVehicle.speedKmH?.toFixed(1) || selectedVehicle.speedKmH || 0} km/h</div>
            </div>
            <div className="telemetry-box hidden-mobile">
              <div className="telemetry-label">Position</div>
              <div className="telemetry-value">{selectedVehicle.latitude.toFixed(4)}, {selectedVehicle.longitude.toFixed(4)}</div>
            </div>
            <button 
              className={`track-btn ${isTracking ? 'active' : ''}`}
              onClick={() => setIsTracking(!isTracking)}
              title={isTracking ? "Unlock Camera" : "Lock to Target"}
            >
              {isTracking ? "🔒 LKD" : "🔓 UNL"}
            </button>
          </div>
        )}
      </header>
      
      <MapContainer center={defaultCenter} zoom={7} style={{ height: '100%', width: '100%' }}>
        <MapTracker position={trackingPos} isTracking={isTracking} />
        
        <TileLayer
          attribution='&copy; OpenStreetMap'
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        />
        
        {vehicles.map(v => (
          <Marker 
            key={v.vehicleId} 
            position={[v.latitude, v.longitude]} 
            icon={v.vehicleId === selectedId ? targetIcon : createVehicleIcon(v.status)}
            eventHandlers={{
              click: () => {
                setSelectedId(v.vehicleId);
                setIsTracking(true);
              }
            }}
          >
            <Popup className="custom-popup">
              <div style={{ color: '#1e293b' }}>
                <strong>Vehicle {v.vehicleId}</strong><br/>
                Speed: {v.speedKmH?.toFixed(1) || 0} km/h<br/>
                Status: {v.status}
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>

      <style dangerouslySetInnerHTML={{ __html: `
        @keyframes pulse {
          0% { transform: scale(1); opacity: 1; }
          50% { transform: scale(1.1); opacity: 0.8; }
          100% { transform: scale(1); opacity: 1; }
        }
        @keyframes bounce {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(-10px); }
        }
        .track-btn {
          background: #334155;
          border: 1px solid #475569;
          color: white;
          padding: 0 15px;
          border-radius: 8px;
          cursor: pointer;
          font-weight: bold;
          transition: all 0.2s;
        }
        .track-btn.active {
          background: #0ea5e9;
          border-color: #38bdf8;
          box-shadow: 0 0 10px rgba(14, 165, 233, 0.5);
        }
        .highlight {
          border-color: #0ea5e9 !important;
          background: rgba(14, 165, 233, 0.1) !important;
        }
        .custom-popup .leaflet-popup-content-wrapper {
          background: white;
          border-radius: 8px;
          padding: 5px;
        }
        @media (max-width: 768px) {
          .hidden-mobile { display: none; }
          .telemetry-panel { gap: 8px; }
          .telemetry-box { padding: 5px 10px; }
        }
      `}} />
    </div>
  )
}

export default App
