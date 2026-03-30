import './LoadingFallback.css';

function LoadingFallback() {
    return (
        <div className="loading-fallback">
            <div className="loading-spinner"></div>
            <p className="loading-text">Loading...</p>
        </div>
    );
}

export default LoadingFallback;
