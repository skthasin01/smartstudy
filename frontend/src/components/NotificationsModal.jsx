import React from 'react';

export const NotificationsModal = ({ isOpen, onClose, notifications, onMarkAsRead, onMarkAllRead, onDelete }) => {
  if (!isOpen) return null;

  return (
    <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
      <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div className="modal-content shadow border-0" style={{ borderRadius: '1rem' }}>
          <div className="modal-header border-bottom py-3 px-4">
            <h5 className="modal-title fw-bold d-flex align-items-center gap-2">
              <i className="bi-bell-fill text-primary"></i>
              Notifications
            </h5>
            <button type="button" className="btn-close" onClick={onClose}></button>
          </div>

          <div className="modal-body p-3">
            <div className="d-flex justify-content-between align-items-center mb-3 px-2">
              <span className="small text-muted fw-semibold">Recent Alerts</span>
              {notifications.length > 0 && (
                <button className="btn btn-sm btn-link text-primary p-0 text-decoration-none" onClick={onMarkAllRead}>
                  Mark all as read
                </button>
              )}
            </div>

            {notifications.length === 0 ? (
              <div className="text-center py-5 text-muted">
                <i className="bi-check-circle fs-1 text-success d-block mb-2"></i>
                <p className="mb-0 fw-medium">All caught up!</p>
                <small className="text-muted">No new reminders or burnout warnings.</small>
              </div>
            ) : (
              <div className="d-flex flex-column gap-2">
                {notifications.map((n) => (
                  <div
                    key={n.id}
                    className={`p-3 rounded-3 border d-flex justify-content-between align-items-start gap-2 ${
                      n.read ? 'bg-light text-muted' : 'bg-white border-primary border-opacity-25 shadow-sm'
                    }`}
                  >
                    <div className="flex-grow-1">
                      <div className="d-flex align-items-center gap-2 mb-1">
                        <span className={`badge ${n.type.includes('BURNOUT') || n.type.includes('MISSED') ? 'bg-danger' : 'bg-primary'}`}>
                          {n.type.replace('_', ' ')}
                        </span>
                        <span className="fw-semibold small">{n.title}</span>
                      </div>
                      <p className="mb-1 small">{n.message}</p>
                      <small className="text-muted" style={{ fontSize: '0.75rem' }}>
                        {new Date(n.createdAt).toLocaleString()}
                      </small>
                    </div>

                    <div className="d-flex gap-1">
                      {!n.read && (
                        <button
                          className="btn btn-sm btn-outline-primary border-0"
                          onClick={() => onMarkAsRead(n.id)}
                          title="Mark as read"
                        >
                          <i className="bi-check2"></i>
                        </button>
                      )}
                      <button
                        className="btn btn-sm btn-outline-danger border-0"
                        onClick={() => onDelete(n.id)}
                        title="Delete alert"
                      >
                        <i className="bi-trash3"></i>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="modal-footer border-top py-2 px-4">
            <button type="button" className="btn btn-sm btn-secondary" onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
