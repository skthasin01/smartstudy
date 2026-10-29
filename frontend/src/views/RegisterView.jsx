import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export const RegisterView = ({ onSwitchToLogin }) => {
  const { register } = useAuth();
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_STUDENT');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      await register(fullName, email, password, role);
    } catch (err) {
      setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="text-center mb-4">
          <div
            className="rounded-3 bg-primary text-white d-inline-flex align-items-center justify-content-center mb-2"
            style={{ width: '48px', height: '48px' }}
          >
            <i className="bi-person-plus fs-3"></i>
          </div>
          <h3 className="fw-bold text-dark">Create Account</h3>
          <p className="text-muted small">Join Smart Study to track load and prevent academic fatigue</p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2" role="alert">
            <i className="bi-exclamation-triangle-fill flex-shrink-0"></i>
            <div>{error}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label small fw-semibold text-secondary">Full Name</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-person"></i>
              </span>
              <input
                type="text"
                className="form-control border-start-0 ps-0"
                placeholder="e.g. Alex Johnson"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label small fw-semibold text-secondary">Email Address</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-envelope"></i>
              </span>
              <input
                type="email"
                className="form-control border-start-0 ps-0"
                placeholder="alex@university.edu"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label small fw-semibold text-secondary">Password</label>
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-lock"></i>
              </span>
              <input
                type="password"
                className="form-control border-start-0 ps-0"
                placeholder="At least 6 characters"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label className="form-label small fw-semibold text-secondary">Account Role</label>
            <select
              className="form-select"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <option value="ROLE_STUDENT">Student (Self-Paced & Wellness Tracking)</option>
              <option value="ROLE_ADMIN">Academic Administrator</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary w-100 py-2 mb-3" disabled={loading}>
            {loading ? (
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ) : (
              <i className="bi-arrow-right-circle me-2"></i>
            )}
            Register Now
          </button>
        </form>

        <div className="text-center small text-muted border-top pt-3">
          Already have an account?{' '}
          <a
            href="#login"
            className="text-primary fw-semibold text-decoration-none"
            onClick={(e) => {
              e.preventDefault();
              onSwitchToLogin();
            }}
          >
            Sign In
          </a>
        </div>
      </div>
    </div>
  );
};
