# React Frontend - Vite

This is the frontend application for the Smart Highway Tolling System.

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/        # Reusable components
│   │   └── Header.jsx
│   ├── pages/            # Page components
│   │   ├── Home.jsx
│   │   ├── Users.jsx
│   │   ├── Vehicles.jsx
│   │   ├── Highways.jsx
│   │   └── Admin.jsx
│   ├── services/         # API service functions
│   │   └── api.js
│   ├── App.jsx          # Main app component
│   ├── main.jsx         # Entry point
│   ├── App.css          # App styles
│   └── index.css        # Global styles
├── index.html           # HTML template
├── package.json         # Dependencies
└── vite.config.js       # Vite configuration
```

## 🚀 How to Run

### Install Dependencies
```bash
cd frontend
npm install
```

### Start Development Server
```bash
npm run dev
```

The app will run on: **http://localhost:3000**

### Build for Production
```bash
npm run build
```

## 🎯 Features

- React 18 with Vite
- React Router for navigation
- Clean and minimal CSS styling
- Responsive design
- API service placeholder (ready for backend integration)

## 📝 Pages

1. **Home** - Project overview and navigation
2. **Users** - User registration and management
3. **Vehicles** - Vehicle registration
4. **Highways** - Highway configuration
5. **Admin** - Dashboard with statistics

## 🔗 Backend Integration

The frontend is configured to proxy API calls to the Spring Boot backend running on `http://localhost:8080`.

API services are defined in `src/services/api.js` and are ready to be connected.
