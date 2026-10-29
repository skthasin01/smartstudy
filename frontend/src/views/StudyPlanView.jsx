import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const StudyPlanView = () => {
  const [currentPlan, setCurrentPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showGenModal, setShowGenModal] = useState(false);

  // Default to today and next 7 days
  const todayStr = new Date().toISOString().split('T')[0];
  const nextWeek = new Date();
  nextWeek.setDate(nextWeek.getDate() + 7);
  const nextWeekStr = nextWeek.toISOString().split('T')[0];

  const [form, setForm] = useState({
    startDate: todayStr,
    endDate: nextWeekStr,
    dailyAvailableMinutes: 240,
    maxBlockMinutes: 50,
    breakMinutes: 10,
  });

  const [error, setError] = useState('');

  const fetchPlan = async () => {
    try {
      setLoading(true);
      const data = await api.getCurrentPlan();
      setCurrentPlan(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlan();
  }, []);

  const handleGenerate = async (e) => {
    e.preventDefault();
    setError('');
    setGenerating(true);

    try {
      const data = await api.generatePlan(form);
      setCurrentPlan(data);
      setShowGenModal(false);
    } catch (err) {
      setError(err.message || 'Failed to generate study plan');
    } finally {
      setGenerating(false);
    }
  };

  const handleItemStatus = async (itemId, newStatus) => {
    try {
      await api.updatePlanItemStatus(itemId, { status: newStatus });
      fetchPlan();
    } catch (err) {
      alert(err.message);
    }
  };

  // Group items by scheduledDate
  const groupedItems = {};
  if (currentPlan?.items) {
    currentPlan.items.forEach((item) => {
      const d = item.scheduledDate;
      if (!groupedItems[d]) groupedItems[d] = [];
      groupedItems[d].push(item);
    });
  }

  return (
    <div className="d-flex flex-column gap-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
        <div>
          <h4 className="fw-bold text-dark mb-1">Rule-Based Study Planner</h4>
          <p className="text-muted small mb-0">Automatic study schedule generator balancing task deadlines and daily capacity</p>
        </div>
        <button className="btn btn-primary d-flex align-items-center gap-2" onClick={() => setShowGenModal(true)}>
          <i className="bi-magic"></i> Generate New Schedule
        </button>
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
        </div>
      ) : !currentPlan ? (
        <div className="text-center py-5 bg-white rounded-4 border p-5">
          <i className="bi-calendar2-range fs-1 text-muted d-block mb-3"></i>
          <h5 className="fw-bold text-dark">No active study plan generated</h5>
          <p className="text-muted small">
            Click below to generate a transparent, rule-based schedule tailored to your pending assignments and deadlines.
          </p>
          <button className="btn btn-primary" onClick={() => setShowGenModal(true)}>
            Generate Study Schedule
          </button>
        </div>
      ) : (
        <div className="d-flex flex-column gap-4">
          {/* Plan Header Info */}
          <div className="bg-white p-4 rounded-4 border shadow-sm">
            <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-3">
              <div>
                <span className="badge bg-success-subtle text-success fw-semibold mb-1">Active Plan</span>
                <h5 className="fw-bold text-dark mb-0">{currentPlan.planName}</h5>
                <small className="text-muted">
                  Window: {currentPlan.startDate} to {currentPlan.endDate} • Generated on {new Date(currentPlan.generatedAt).toLocaleString()}
                </small>
              </div>

              <div className="text-md-end">
                <span className="display-6 fw-bold text-primary">
                  {Math.round(currentPlan.totalAllocatedMinutes / 60)}h {currentPlan.totalAllocatedMinutes % 60}m
                </span>
                <span className="text-muted d-block small">Total Allocated Study Time</span>
              </div>
            </div>

            {/* Explanation / Transparent Logic Box */}
            {currentPlan.explanation && (
              <div className="p-3 bg-light rounded-3 border small">
                <h6 className="fw-bold text-dark d-flex align-items-center gap-2 mb-2">
                  <i className="bi-info-circle-fill text-primary"></i> Scheduling Decisions & Analysis
                </h6>
                <pre className="mb-0 text-secondary" style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', fontSize: '0.85rem' }}>
                  {currentPlan.explanation}
                </pre>
              </div>
            )}
          </div>

          {/* Schedule by Day */}
          <div className="d-flex flex-column gap-3">
            {Object.keys(groupedItems).length === 0 ? (
              <div className="alert alert-info">No study items scheduled for this period.</div>
            ) : (
              Object.entries(groupedItems).map(([dateStr, items]) => {
                const totalDayMins = items.reduce((acc, it) => acc + it.durationMinutes, 0);
                const dayDate = new Date(dateStr + 'T00:00:00');
                const dayDisplay = dayDate.toLocaleDateString(undefined, { weekday: 'long', month: 'short', day: 'numeric' });

                return (
                  <div key={dateStr} className="bg-white rounded-4 border shadow-sm p-4">
                    <div className="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
                      <div>
                        <h6 className="fw-bold text-dark mb-0">{dayDisplay}</h6>
                        <small className="text-muted">{dateStr}</small>
                      </div>
                      <span className="badge bg-primary-subtle text-primary fw-bold">
                        {totalDayMins} mins scheduled
                      </span>
                    </div>

                    <div className="d-flex flex-column gap-2">
                      {items.map((item) => (
                        <div
                          key={item.id}
                          className={`p-3 rounded-3 border d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 ${
                            item.status === 'COMPLETED' ? 'bg-light text-muted' : 'bg-white'
                          }`}
                        >
                          <div className="d-flex align-items-center gap-3">
                            <div className="text-center p-2 rounded bg-light border" style={{ minWidth: '95px' }}>
                              <span className="fw-bold text-dark small d-block">{item.startTime?.slice(0, 5)}</span>
                              <span className="text-muted" style={{ fontSize: '0.75rem' }}>to {item.endTime?.slice(0, 5)}</span>
                            </div>

                            <div>
                              <div className="d-flex align-items-center gap-2 mb-1">
                                {item.courseCode && (
                                  <span
                                    className="badge text-white"
                                    style={{ backgroundColor: item.courseColor || '#4F46E5', fontSize: '0.7rem' }}
                                  >
                                    {item.courseCode}
                                  </span>
                                )}
                                <span className={`fw-semibold ${item.status === 'COMPLETED' ? 'text-decoration-line-through' : 'text-dark'}`}>
                                  {item.title}
                                </span>
                              </div>
                              <small className="text-muted">{item.notes}</small>
                            </div>
                          </div>

                          <div className="d-flex align-items-center gap-2">
                            <span className="badge bg-light text-secondary border me-2">
                              {item.durationMinutes} mins
                            </span>
                            <select
                              className="form-select form-select-sm"
                              style={{ width: '130px' }}
                              value={item.status}
                              onChange={(e) => handleItemStatus(item.id, e.target.value)}
                            >
                              <option value="PLANNED">Planned</option>
                              <option value="IN_PROGRESS">In Progress</option>
                              <option value="COMPLETED">Completed</option>
                              <option value="SKIPPED">Skipped</option>
                            </select>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* Generate Schedule Modal */}
      {showGenModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow border-0" style={{ borderRadius: '1rem' }}>
              <div className="modal-header py-3 px-4 border-bottom">
                <h5 className="modal-title fw-bold">Generate Automatic Study Schedule</h5>
                <button type="button" className="btn-close" onClick={() => setShowGenModal(false)}></button>
              </div>

              <form onSubmit={handleGenerate}>
                <div className="modal-body p-4">
                  {error && <div className="alert alert-danger py-2 px-3 small mb-3">{error}</div>}

                  <div className="row g-3 mb-3">
                    <div className="col-6">
                      <label className="form-label small fw-semibold">Start Date</label>
                      <input
                        type="date"
                        className="form-control"
                        value={form.startDate}
                        onChange={(e) => setForm({ ...form, startDate: e.target.value })}
                        required
                      />
                    </div>
                    <div className="col-6">
                      <label className="form-label small fw-semibold">End Date</label>
                      <input
                        type="date"
                        className="form-control"
                        value={form.endDate}
                        onChange={(e) => setForm({ ...form, endDate: e.target.value })}
                        required
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <div className="d-flex justify-content-between mb-1">
                      <label className="form-label small fw-semibold">Daily Available Study Cap</label>
                      <span className="small fw-bold text-primary">{form.dailyAvailableMinutes} minutes ({Math.round(form.dailyAvailableMinutes / 60)} hrs)</span>
                    </div>
                    <input
                      type="range"
                      className="form-range"
                      min="60"
                      max="600"
                      step="30"
                      value={form.dailyAvailableMinutes}
                      onChange={(e) => setForm({ ...form, dailyAvailableMinutes: Number(e.target.value) })}
                    />
                  </div>

                  <div className="mb-3">
                    <div className="d-flex justify-content-between mb-1">
                      <label className="form-label small fw-semibold">Max Continuous Block</label>
                      <span className="small fw-bold text-primary">{form.maxBlockMinutes} minutes</span>
                    </div>
                    <input
                      type="range"
                      className="form-range"
                      min="25"
                      max="90"
                      step="5"
                      value={form.maxBlockMinutes}
                      onChange={(e) => setForm({ ...form, maxBlockMinutes: Number(e.target.value) })}
                    />
                  </div>

                  <div className="mb-2">
                    <div className="d-flex justify-content-between mb-1">
                      <label className="form-label small fw-semibold">Break Interval</label>
                      <span className="small fw-bold text-primary">{form.breakMinutes} minutes</span>
                    </div>
                    <input
                      type="range"
                      className="form-range"
                      min="5"
                      max="30"
                      step="5"
                      value={form.breakMinutes}
                      onChange={(e) => setForm({ ...form, breakMinutes: Number(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="modal-footer py-3 px-4 border-top">
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setShowGenModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={generating}>
                    {generating ? 'Calculating Schedule...' : 'Generate Plan'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
