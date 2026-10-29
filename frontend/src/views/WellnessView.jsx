import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const WellnessView = ({ onNavigate }) => {
  const [todayCheckin, setTodayCheckin] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [form, setForm] = useState({
    stressLevel: 3,
    energyLevel: 3,
    moodLevel: 'NEUTRAL',
    sleepHours: 7.0,
    notes: '',
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      setError('');
      const [today, hist] = await Promise.all([
        api.getTodayCheckin(),
        api.getCheckinHistory(0, 10),
      ]);
      setTodayCheckin(today);
      if (today) {
        setForm({
          stressLevel: today.stressLevel,
          energyLevel: today.energyLevel,
          moodLevel: today.moodLevel || 'NEUTRAL',
          sleepHours: today.sleepHours,
          notes: today.notes || '',
        });
      }
      setHistory(hist?.content || []);
    } catch (err) {
      setError(err.message || 'Error loading check-in data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);

    try {
      const res = await api.submitCheckin({
        stressLevel: Number(form.stressLevel),
        energyLevel: Number(form.energyLevel),
        moodLevel: form.moodLevel,
        sleepHours: Number(form.sleepHours),
        notes: form.notes,
      });
      setTodayCheckin(res);
      setSuccess('Daily wellness check-in saved successfully!');
      fetchData();
    } catch (err) {
      setError(err.message || 'Failed to submit check-in');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="d-flex flex-column gap-4">
      <div>
        <h4 className="fw-bold text-dark mb-1">Daily Wellness Check-in</h4>
        <p className="text-muted small mb-0">
          Optional self-reflection tracking stress, sleep, and energy levels to inform burnout prevention
        </p>
      </div>

      {error && error.includes('privacy settings') ? (
        <div className="alert alert-warning p-4 rounded-4 shadow-sm border-0 d-flex justify-content-between align-items-center">
          <div>
            <h6 className="fw-bold mb-1">Wellness Tracking Disabled</h6>
            <p className="small mb-0 text-secondary">
              You currently have wellness tracking disabled in your Privacy Settings.
            </p>
          </div>
          <button className="btn btn-outline-dark btn-sm" onClick={() => onNavigate('settings')}>
            Go to Settings
          </button>
        </div>
      ) : (
        <div className="row g-4">
          {/* Checkin Form */}
          <div className="col-12 col-lg-6">
            <div className="bg-white p-4 rounded-4 border shadow-sm">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold mb-0 text-dark d-flex align-items-center gap-2">
                  <i className="bi-heart-pulse-fill text-danger"></i>
                  {todayCheckin ? "Update Today's Check-in" : "Log Today's Wellness"}
                </h5>
                {todayCheckin && (
                  <span className="badge bg-success-subtle text-success">Submitted</span>
                )}
              </div>

              {success && <div className="alert alert-success py-2 px-3 small mb-3">{success}</div>}
              {error && <div className="alert alert-danger py-2 px-3 small mb-3">{error}</div>}

              <form onSubmit={handleSubmit}>
                {/* Stress Level */}
                <div className="mb-4">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <label className="form-label small fw-semibold mb-0">Perceived Stress Level</label>
                    <span className="badge bg-light text-dark border">
                      {form.stressLevel} / 5 ({form.stressLevel >= 4 ? 'High' : form.stressLevel === 3 ? 'Moderate' : 'Low'})
                    </span>
                  </div>
                  <div className="d-flex justify-content-between gap-2">
                    {[1, 2, 3, 4, 5].map((lvl) => (
                      <button
                        key={lvl}
                        type="button"
                        className={`btn flex-grow-1 ${form.stressLevel === lvl ? 'btn-primary' : 'btn-outline-secondary'}`}
                        onClick={() => setForm({ ...form, stressLevel: lvl })}
                      >
                        {lvl}
                      </button>
                    ))}
                  </div>
                  <div className="d-flex justify-content-between text-muted small mt-1" style={{ fontSize: '0.75rem' }}>
                    <span>1 (Calm / Relaxed)</span>
                    <span>5 (Extremely Stressed)</span>
                  </div>
                </div>

                {/* Energy Level */}
                <div className="mb-4">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <label className="form-label small fw-semibold mb-0">Energy & Focus Level</label>
                    <span className="badge bg-light text-dark border">
                      {form.energyLevel} / 5 ({form.energyLevel <= 2 ? 'Low Energy' : 'Good Focus'})
                    </span>
                  </div>
                  <div className="d-flex justify-content-between gap-2">
                    {[1, 2, 3, 4, 5].map((lvl) => (
                      <button
                        key={lvl}
                        type="button"
                        className={`btn flex-grow-1 ${form.energyLevel === lvl ? 'btn-info text-white' : 'btn-outline-secondary'}`}
                        onClick={() => setForm({ ...form, energyLevel: lvl })}
                      >
                        {lvl}
                      </button>
                    ))}
                  </div>
                  <div className="d-flex justify-content-between text-muted small mt-1" style={{ fontSize: '0.75rem' }}>
                    <span>1 (Exhausted / Drained)</span>
                    <span>5 (Fully Energized)</span>
                  </div>
                </div>

                {/* Mood Level */}
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Mood State</label>
                  <select
                    className="form-select"
                    value={form.moodLevel}
                    onChange={(e) => setForm({ ...form, moodLevel: e.target.value })}
                  >
                    <option value="VERY_LOW">Very Low / Depressed</option>
                    <option value="LOW">Low / Anxious</option>
                    <option value="NEUTRAL">Neutral / Okay</option>
                    <option value="GOOD">Good / Positive</option>
                    <option value="EXCELLENT">Excellent / Motivated</option>
                  </select>
                </div>

                {/* Sleep Duration */}
                <div className="mb-3">
                  <label className="form-label small fw-semibold">Sleep Duration (Hours)</label>
                  <div className="input-group">
                    <span className="input-group-text bg-light text-muted">
                      <i className="bi-moon-stars"></i>
                    </span>
                    <input
                      type="number"
                      step="0.5"
                      min="0"
                      max="24"
                      className="form-control"
                      value={form.sleepHours}
                      onChange={(e) => setForm({ ...form, sleepHours: e.target.value })}
                      required
                    />
                    <span className="input-group-text bg-light text-muted">hours</span>
                  </div>
                </div>

                {/* Notes */}
                <div className="mb-4">
                  <label className="form-label small fw-semibold">Personal Reflection Notes (Optional)</label>
                  <textarea
                    className="form-control"
                    rows="2"
                    placeholder="How was today? Any external pressures or milestones?"
                    value={form.notes}
                    onChange={(e) => setForm({ ...form, notes: e.target.value })}
                  ></textarea>
                </div>

                <button type="submit" className="btn btn-primary w-100 py-2 fw-semibold" disabled={submitting}>
                  {submitting ? 'Saving...' : todayCheckin ? 'Update Daily Log' : 'Save Check-in'}
                </button>
              </form>
            </div>
          </div>

          {/* History */}
          <div className="col-12 col-lg-6">
            <div className="bg-white p-4 rounded-4 border shadow-sm h-100">
              <h5 className="fw-bold mb-3 text-dark d-flex align-items-center gap-2">
                <i className="bi-clock-history text-secondary"></i> Past Check-in Logs
              </h5>

              {history.length === 0 ? (
                <div className="text-center py-5 text-muted small">
                  No previous check-in records logged.
                </div>
              ) : (
                <div className="d-flex flex-column gap-2">
                  {history.map((c) => (
                    <div key={c.id} className="p-3 border rounded-3 bg-light">
                      <div className="d-flex justify-content-between align-items-center mb-1">
                        <span className="fw-bold text-dark small">{c.checkinDate}</span>
                        <span className="badge bg-light text-secondary border">
                          Mood: {c.moodDescription || c.moodLevel}
                        </span>
                      </div>
                      <div className="d-flex gap-3 small text-muted">
                        <span>Stress: <strong className={c.stressLevel >= 4 ? 'text-danger' : 'text-dark'}>{c.stressLevel}/5</strong></span>
                        <span>Energy: <strong className="text-dark">{c.energyLevel}/5</strong></span>
                        <span>Sleep: <strong className="text-dark">{c.sleepHours}h</strong></span>
                      </div>
                      {c.notes && <p className="small text-secondary mt-2 mb-0 fst-italic">"{c.notes}"</p>}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
