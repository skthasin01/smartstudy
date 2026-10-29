import React from 'react';
import { useAuth } from '../context/AuthContext';

export const Navbar = ({ pageTitle, unreadCount, onOpenNotifications, activeSession }) => {
  const { user } = useAuth();

  return (
    <header className="top-navbar d-flex align-items-center justify-content-between">
      <div className="d-flex align-items-center gap-3">
        <h4 className="fw-bold mb-0 text-dark">{pageTitle}</h4>
        {activeSession && (
          <span className="badge bg-danger pulsing-badge d-flex align-items-center gap-1 py-1 px-2">
            <span className="spinner-grow spinner-grow-sm" style={{ width: '8px', height: '8px' }}></span>
            Session Active
          </span>
        )}
      </div>

      <div className="d-flex align-items-center gap-3">
        <button
          className="btn btn-light position-relative border"
          onClick={onOpenNotifications}
          title="Notifications"
        >
          <i className="bi-bell fs-5 text-secondary"></i>
          {unreadCount > 0 && (
            <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
              {unreadCount}
              <span className="visually-hidden">unread messages</span>
            </span>
          )}
        </button>

        <div className="d-none d-sm-block text-end">
          <span className="badge bg-light text-primary border fw-semibold">
            {user?.role === 'ROLE_ADMIN' ? 'Administrator' : 'Student'}
          </span>
        </div>
      </div>
    </header>
  );
};
