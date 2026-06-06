# Security Policy

The Smart Highway Usage-Based Tolling System is designed primarily as an academic and demonstration project. Please do not run it as a public, unauthenticated service without implementing proper security hardening.

## Supported Versions

Security fixes and updates are handled on the default `main` branch until formal releases are cut.

## Deployment Guidance

*   **Change Default Admin Credentials:** The system ships with a default admin account (`admin@highway.com` / `admin123`). **You must change this** by setting `ADMIN_EMAIL` and `ADMIN_PASSWORD` in your `.env` file before deploying.
*   **Disable Dev Tools:** The DB Explorer (`/api/db-explorer`) and the simulator's H2 Console (`/h2-console`) expose raw database access. These are for development only and must be disabled or strictly firewalled in production.
*   **Use HTTPS:** Serve the frontend and backend through a trusted reverse proxy (like Nginx, Traefik, or Cloudflare Tunnel) with HTTPS enabled when exposing the app beyond `localhost`.
*   **Internal-Only Ports:** Keep MySQL (3306), the Main Backend (8080), and the IoT Simulator (8082) internal-only. Only expose the frontend or your reverse proxy to the outside world.
*   **Protect Secrets:** Strictly protect your `.env` file. It contains your `DB_PASSWORD` and email credentials. 
*   **Protect Data Files:** Secure the MySQL data directory and the IoT simulator's embedded files (`iot-simulator/data/iot_simulator_db.mv.db` and `vehicle-state.json`).
*   **Session Storage:** The frontend currently relies on `sessionStorage` for role management. If deploying as a real financial system, transition authentication to strictly use `HttpOnly` cookies.

## Publishing A Fork

Before pushing a public fork of this repository, ensure you have not accidentally staged sensitive information.

Run the following checks:
```bash
git status --short
git check-ignore -v .env iot-simulator/data/iot_simulator_db.mv.db
```

Only `.env.example`, documentation, source code, and static assets should be committed. **Never commit:**
*   Live `.env` values
*   The `iot-simulator/data/` directory contents
*   Any generated `.jar` files or `node_modules/`

## Reporting

If you discover a security vulnerability within this project, please report it privately via a GitHub security advisory if available, or by opening a minimal issue that describes the context without disclosing direct exploit details. You may also contact the maintainer directly.
