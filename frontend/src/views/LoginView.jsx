import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export const LoginView = ({ onSwitchToRegister }) => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
    } catch (err) {
      setError(err.message || 'Failed to login');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoFill = () => {
    setEmail('student@smartstudy.edu');
    setPassword('StudyPass123!');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="text-center mb-4">
          <div
            className="rounded-3 bg-primary text-white d-inline-flex align-items-center justify-content-center mb-2"
            style={{ width: '48px', height: '48px' }}
          >
            <i className="bi-shield-check fs-3"></i>
          </div>
          <h3 className="fw-bold text-dark">Welcome Back</h3>
          <p className="text-muted small">Sign in to manage your study load and prevent burnout</p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2" role="alert">
            <i className="bi-exclamation-triangle-fill flex-shrink-0"></i>
            <div>{error}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label small fw-semibold text-secondary">Email Address</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-envelope"></i>
              </span>
              <input
                type="email"
                className="form-control border-start-0 ps-0"
                placeholder="name@university.edu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label className="form-label small fw-semibold text-secondary">Password</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-lock"></i>
              </span>
              <input
                type="password"
                className="form-control border-start-0 ps-0"
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary w-100 py-2 mb-3" disabled={loading}>
            {loading ? (
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ) : (
              <i className="bi-box-arrow-in-right me-2"></i>
            )}
            Sign In
          </button>

          <button
            type="button"
            className="btn btn-outline-secondary w-100 py-2 small mb-4"
            onClick={handleDemoFill}
          >
            <i className="bi-magic me-1"></i> Quick Fill Demo Account
          </button>
        </form>

        <div className="text-center small text-muted border-top pt-3">
          Don't have an account yet?{' '}
          <a
            href="#register"
            className="text-primary fw-semibold text-decoration-none"
            onClick={(e) => {
              e.preventDefault();
              onSwitchToRegister();
            }}
          >
            Create an account
          </a>
        </div>
      </div>
    </div>
  );
};
