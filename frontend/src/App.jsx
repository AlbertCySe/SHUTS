import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'
import Header from './components/Header'
import Home from './pages/Home'
import Users from './pages/Users'
import Vehicles from './pages/Vehicles'
import Highways from './pages/Highways'
import LocationTracking from './pages/LocationTracking'
import WalletBills from './pages/WalletBills'
import AdminDashboard from './pages/AdminDashboard'
import './App.css'

function App() {
    return (
        <Router>
            <div className="app">
                <Header />

                <nav className="navbar">
                    <Link to="/" className="nav-link">Home</Link>
                    <Link to="/users" className="nav-link">Users</Link>
                    <Link to="/vehicles" className="nav-link">Vehicles</Link>
                    <Link to="/highways" className="nav-link">Highways</Link>
                    <Link to="/locations" className="nav-link">GPS Tracking</Link>
                    <Link to="/wallet-bills" className="nav-link">Wallet & Bills</Link>
                    <Link to="/admin" className="nav-link">Admin</Link>
                </nav>

                <div className="container">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/users" element={<Users />} />
                        <Route path="/vehicles" element={<Vehicles />} />
                        <Route path="/highways" element={<Highways />} />
                        <Route path="/locations" element={<LocationTracking />} />
                        <Route path="/wallet-bills" element={<WalletBills />} />
                        <Route path="/admin" element={<AdminDashboard />} />
                    </Routes>
                </div>

                <footer className="footer">
                    <p>&copy; 2026 Smart Highway Tolling System - MCA Final Year Project</p>
                </footer>
            </div>
        </Router>
    )
}

export default App
