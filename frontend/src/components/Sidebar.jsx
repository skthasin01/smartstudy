import React from 'react';
import { useAuth } from '../context/AuthContext';

export const Sidebar = ({ currentView, setCurrentView }) => {
  const { user, logout } = useAuth();

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-grid-1x2-fill' },
    { id: 'tasks', label: 'Tasks', icon: 'bi-check2-square' },
    { id: 'courses', label: 'Courses', icon: 'bi-journal-bookmark-fill' },
    { id: 'study-plan', label: 'Study Planner', icon: 'bi-calendar-week-fill' },
    { id: 'timer', label: 'Study Timer', icon: 'bi-stopwatch-fill' },
    { id: 'wellness', label: 'Wellness Check-in', icon: 'bi-heart-pulse-fill' },
    { id: 'settings', label: 'Settings & Privacy', icon: 'bi-gear-fill' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <i className="bi-shield-check text-indigo-400 fs-4" style={{ color: '#818cf8' }}></i>
        <span>SmartStudy</span>
      </div>

      <ul className="sidebar-nav">
        {navItems.map((item) => (
          <li key={item.id} className="nav-item">
            <a
              href={`#${item.id}`}
              className={`nav-link ${currentView === item.id ? 'active' : ''}`}
              onClick={(e) => {
                e.preventDefault();
                setCurrentView(item.id);
              }}
            >
              <i className={`bi ${item.icon} fs-5`}></i>
              <span className="nav-text">{item.label}</span>
            </a>
          </li>
        ))}
      </ul>

      <div className="sidebar-footer">
        <div className="d-flex align-items-center justify-content-between">
          <div className="d-flex align-items-center gap-2 overflow-hidden">
            <div
              className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
              style={{ width: '36px', height: '36px', flexShrink: 0 }}
            >
              {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
            </div>
            <div className="text-truncate">
              <div className="fw-semibold text-white small text-truncate">{user?.fullName || 'Student'}</div>
              <div className="text-muted" style={{ fontSize: '0.75rem' }}>{user?.email}</div>
            </div>
          </div>
          <button
            onClick={logout}
            className="btn btn-sm btn-outline-light border-0"
            title="Log Out"
          >
            <i className="bi-box-arrow-right fs-5"></i>
          </button>
        </div>
      </div>
    </aside>
  );
};
