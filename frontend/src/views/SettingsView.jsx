import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

export const SettingsView = () => {
  const { user, updateUser } = useAuth();
  const [settings, setSettings] = useState({
    allowWellnessTracking: true,
    shareAnonymousStats: false,
    reminderNotificationsEnabled: true,
    dailyStudyGoalMinutes: 240,
  });
  const [loading, setLoading] = useState(true);
  const [savingSettings, setSavingSettings] = useState(false);
  const [settingsMessage, setSettingsMessage] = useState('');

  // Password state
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [savingPassword, setSavingPassword] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState('');

  // Profile state
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [savingProfile, setSavingProfile] = useState(false);
  const [profileMessage, setProfileMessage] = useState('');

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await api.getSettings();
      if (data) {
        setSettings({
          allowWellnessTracking: data.allowWellnessTracking,
          shareAnonymousStats: data.shareAnonymousStats,
          reminderNotificationsEnabled: data.reminderNotificationsEnabled,
          dailyStudyGoalMinutes: data.dailyStudyGoalMinutes || 240,
        });
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSettings();
  }, []);

  const handleSaveSettings = async (e) => {
    e.preventDefault();
    setSettingsMessage('');
    setSavingSettings(true);

    try {
      const updated = await api.updateSettings(settings);
      setSettings(updated);
      setSettingsMessage('Preferences updated successfully.');
    } catch (err) {
      alert(err.message || 'Failed to update settings');
    } finally {
      setSavingSettings(false);
    }
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setProfileMessage('');
    setSavingProfile(true);

    try {
      const updated = await api.updateProfile({ fullName });
      updateUser({ fullName: updated.fullName });
      setProfileMessage('Profile details updated.');
    } catch (err) {
      alert(err.message);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setPasswordError('');
    setPasswordSuccess('');

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('New passwords do not match');
      return;
    }

    setSavingPassword(true);
    try {
      await api.changePassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      });
      setPasswordSuccess('Password changed successfully.');
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err) {
      setPasswordError(err.message || 'Failed to change password');
    } finally {
      setSavingPassword(false);
    }
  };

  const handleClearWellness = async () => {
    if (window.confirm('Are you sure you want to permanently delete all your daily check-in records? This action cannot be undone.')) {
      try {
        await api.clearWellnessData();
        alert('All wellness records have been permanently cleared.');
      } catch (err) {
        alert(err.message);
      }
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    );
  }

  return (
    <div className="d-flex flex-column gap-4">
      <div>
        <h4 className="fw-bold text-dark mb-1">Settings & Privacy</h4>
        <p className="text-muted small mb-0">Manage study preferences, privacy boundaries, and account credentials</p>
      </div>

      <div className="row g-4">
        {/* Privacy & Study Preferences */}
        <div className="col-12 col-lg-6">
          <div className="bg-white p-4 rounded-4 border shadow-sm h-100">
            <h5 className="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
              <i className="bi-sliders text-primary"></i> Study & Privacy Preferences
            </h5>

            {settingsMessage && <div className="alert alert-success py-2 px-3 small mb-3">{settingsMessage}</div>}

            <form onSubmit={handleSaveSettings}>
              <div className="mb-3">
                <div className="d-flex justify-content-between mb-1">
                  <label className="form-label small fw-semibold">Daily Study Target Goal</label>
                  <span className="small fw-bold text-primary">
                    {settings.dailyStudyGoalMinutes} minutes ({Math.round(settings.dailyStudyGoalMinutes / 60)} hrs)
                  </span>
                </div>
                <input
                  type="range"
                  className="form-range"
                  min="60"
                  max="600"
                  step="30"
                  value={settings.dailyStudyGoalMinutes}
                  onChange={(e) => setSettings({ ...settings, dailyStudyGoalMinutes: Number(e.target.value) })}
                />
                <div className="form-text small">Used to normalize your workload and schedule capacity.</div>
              </div>

              <div className="form-check form-switch mb-3">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="wellnessToggle"
                  checked={settings.allowWellnessTracking}
                  onChange={(e) => setSettings({ ...settings, allowWellnessTracking: e.target.checked })}
                />
                <label className="form-check-label small fw-semibold" htmlFor="wellnessToggle">
                  Enable Daily Wellness & Sleep Tracking
                </label>
                <div className="form-text small">When enabled, check-ins contribute to early burnout warnings.</div>
              </div>

              <div className="form-check form-switch mb-3">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="notificationsToggle"
                  checked={settings.reminderNotificationsEnabled}
                  onChange={(e) => setSettings({ ...settings, reminderNotificationsEnabled: e.target.checked })}
                />
                <label className="form-check-label small fw-semibold" htmlFor="notificationsToggle">
                  In-App Deadline & Strain Reminders
                </label>
                <div className="form-text small">Receive notifications when tasks approach due dates.</div>
              </div>

              <div className="form-check form-switch mb-4">
                <input
                  className="form-check-input"
                  type="checkbox"
                  id="statsToggle"
                  checked={settings.shareAnonymousStats}
                  onChange={(e) => setSettings({ ...settings, shareAnonymousStats: e.target.checked })}
                />
                <label className="form-check-label small fw-semibold" htmlFor="statsToggle">
                  Share Anonymous Aggregate Statistics
                </label>
                <div className="form-text small">Help improve institutional study load pacing research.</div>
              </div>

              <button type="submit" className="btn btn-primary" disabled={savingSettings}>
                {savingSettings ? 'Saving...' : 'Save Preferences'}
              </button>
            </form>

            <hr className="my-4" />

            {/* GDPR Data Erasure */}
            <div>
              <h6 className="fw-bold text-dark mb-1">Data Privacy Controls (GDPR)</h6>
              <p className="small text-muted mb-3">
                You have full control over your wellness records. You can delete all logged check-ins at any time.
              </p>
              <button className="btn btn-sm btn-outline-danger" onClick={handleClearWellness}>
                <i className="bi-trash3 me-1"></i> Clear All Logged Wellness Data
              </button>
            </div>
          </div>
        </div>

        {/* Profile & Password Controls */}
        <div className="col-12 col-lg-6">
          <div className="d-flex flex-column gap-4">
            {/* Profile Info */}
            <div className="bg-white p-4 rounded-4 border shadow-sm">
              <h5 className="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                <i className="bi-person-circle text-primary"></i> Profile Information
              </h5>

              {profileMessage && <div className="alert alert-success py-2 px-3 small mb-3">{profileMessage}</div>}

              <form onSubmit={handleUpdateProfile}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Email Address</label>
                  <input type="text" className="form-control bg-light" value={user?.email || ''} disabled />
                  <div className="form-text small">Email cannot be modified directly.</div>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Full Name</label>
                  <input
                    type="text"
                    className="form-control"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                  />
                </div>

                <button type="submit" className="btn btn-primary" disabled={savingProfile}>
                  {savingProfile ? 'Updating...' : 'Update Name'}
                </button>
              </form>
            </div>

            {/* Change Password */}
            <div className="bg-white p-4 rounded-4 border shadow-sm">
              <h5 className="fw-bold text-dark mb-3 d-flex align-items-center gap-2">
                <i className="bi-key-fill text-primary"></i> Change Password
              </h5>

              {passwordSuccess && <div className="alert alert-success py-2 px-3 small mb-3">{passwordSuccess}</div>}
              {passwordError && <div className="alert alert-danger py-2 px-3 small mb-3">{passwordError}</div>}

              <form onSubmit={handleChangePassword}>
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Current Password</label>
                  <input
                    type="password"
                    className="form-control"
                    value={passwordForm.currentPassword}
                    onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                    required
                  />
                </div>

                <div className="row g-2 mb-3">
                  <div className="col-6">
                    <label className="form-label small fw-semibold">New Password</label>
                    <input
                      type="password"
                      className="form-control"
                      value={passwordForm.newPassword}
                      onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                      required
                    />
                  </div>
                  <div className="col-6">
                    <label className="form-label small fw-semibold">Confirm Password</label>
                    <input
                      type="password"
                      className="form-control"
                      value={passwordForm.confirmPassword}
                      onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <button type="submit" className="btn btn-outline-primary" disabled={savingPassword}>
                  {savingPassword ? 'Updating...' : 'Change Password'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
