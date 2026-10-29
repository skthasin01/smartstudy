import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const StudySessionView = ({ onSessionStateChange }) => {
  const [activeSession, setActiveSession] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [selectedTaskId, setSelectedTaskId] = useState('');
  const [sessionNotes, setSessionNotes] = useState('');
  const [summary, setSummary] = useState(null);
  const [history, setHistory] = useState([]);
  const [elapsedSeconds, setElapsedSeconds] = useState(0);

  // End Modal state
  const [showEndModal, setShowEndModal] = useState(false);
  const [breakMinutes, setBreakMinutes] = useState(5);
  const [markCompleted, setMarkCompleted] = useState(false);
  const [endNotes, setEndNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const fetchActive = async () => {
    try {
      const active = await api.getActiveSession();
      setActiveSession(active);
      if (onSessionStateChange) onSessionStateChange(active);

      if (active?.startTime) {
        const start = new Date(active.startTime).getTime();
        const now = new Date().getTime();
        const diff = Math.max(0, Math.floor((now - start) / 1000));
        setElapsedSeconds(diff);
      } else {
        setElapsedSeconds(0);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchSummaryAndHistory = async () => {
    try {
      const [sumData, histData] = await Promise.all([
        api.getSessionSummary(),
        api.getSessionHistory(0, 8),
      ]);
      setSummary(sumData);
      setHistory(histData.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchPendingTasks = async () => {
    try {
      const res = await api.getTasks({ status: 'TODO', size: 50 });
      setTasks(res.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchActive();
    fetchSummaryAndHistory();
    fetchPendingTasks();
  }, []);

  // Timer interval
  useEffect(() => {
    let timer = null;
    if (activeSession) {
      timer = setInterval(() => {
        setElapsedSeconds((prev) => prev + 1);
      }, 1000);
    }
    return () => {
      if (timer) clearInterval(timer);
    };
  }, [activeSession]);

  const handleStart = async () => {
    try {
      setSubmitting(true);
      const res = await api.startSession({
        taskId: selectedTaskId ? Number(selectedTaskId) : null,
        notes: sessionNotes,
      });
      setActiveSession(res);
      setElapsedSeconds(0);
      if (onSessionStateChange) onSessionStateChange(res);
      fetchSummaryAndHistory();
    } catch (err) {
      alert(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleEnd = async () => {
    try {
      setSubmitting(true);
      await api.endSession({
        breakDurationMinutes: Number(breakMinutes),
        notes: endNotes,
        markTaskCompleted: markCompleted,
      });
      setActiveSession(null);
      setShowEndModal(false);
      setElapsedSeconds(0);
      if (onSessionStateChange) onSessionStateChange(null);
      fetchSummaryAndHistory();
      fetchPendingTasks();
    } catch (err) {
      alert(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const formatTimer = (totalSec) => {
    const hrs = Math.floor(totalSec / 3600);
    const mins = Math.floor((totalSec % 3600) / 60);
    const secs = totalSec % 60;
    const pad = (n) => String(n).padStart(2, '0');
    if (hrs > 0) {
      return `${pad(hrs)}:${pad(mins)}:${pad(secs)}`;
    }
    return `${pad(mins)}:${pad(secs)}`;
  };

  return (
    <div className="d-flex flex-column gap-4">
      <div>
        <h4 className="fw-bold text-dark mb-1">Study Session & Timer</h4>
        <p className="text-muted small mb-0">Track real-time study intervals, Pomodoro breaks, and update task progress</p>
      </div>

      {/* Main Timer Display Card */}
      <div className="bg-white p-5 rounded-4 border shadow-sm text-center">
        <div className="mb-2">
          {activeSession ? (
            <span className="badge bg-danger pulsing-badge py-2 px-3 fs-6">
              <span className="spinner-grow spinner-grow-sm me-2"></span>
              Live Focus Session in Progress
            </span>
          ) : (
            <span className="badge bg-light text-secondary border py-2 px-3 fs-6">
              Timer Idle
            </span>
          )}
        </div>

        {activeSession?.taskTitle && (
          <div className="mb-3">
            <span className="small text-muted">Focused Task:</span>
            <h5 className="fw-bold text-primary mb-0">{activeSession.taskTitle}</h5>
          </div>
        )}

        <div className="timer-display my-4">
          {formatTimer(elapsedSeconds)}
        </div>

        {!activeSession ? (
          <div className="row justify-content-center">
            <div className="col-12 col-md-6 col-lg-5 text-start">
              <div className="mb-3">
                <label className="form-label small fw-semibold">Link to Task (Optional)</label>
                <select
                  className="form-select"
                  value={selectedTaskId}
                  onChange={(e) => setSelectedTaskId(e.target.value)}
                >
                  <option value="">No task linked (General Study)</option>
                  {tasks.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.courseCode ? `[${t.courseCode}] ` : ''}{t.title}
                    </option>
                  ))}
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold">Session Notes (Optional)</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Chapter 4 problem set"
                  value={sessionNotes}
                  onChange={(e) => setSessionNotes(e.target.value)}
                />
              </div>

              <button
                className="btn btn-primary w-100 py-3 fw-bold fs-5 shadow-sm"
                onClick={handleStart}
                disabled={submitting}
              >
                <i className="bi-play-circle-fill me-2"></i> Start Study Session
              </button>
            </div>
          </div>
        ) : (
          <div className="d-flex justify-content-center gap-3">
            <button
              className="btn btn-danger py-3 px-5 fw-bold fs-5 shadow-sm"
              onClick={() => setShowEndModal(true)}
            >
              <i className="bi-stop-circle-fill me-2"></i> Conclude Session
            </button>
          </div>
        )}
      </div>

      {/* Stats Summary */}
      {summary && (
        <div className="row g-3">
          <div className="col-12 col-sm-4">
            <div className="stat-card text-center p-3">
              <div className="text-muted small fw-medium">Today's Focus</div>
              <h4 className="fw-bold mb-0 text-dark">
                {Math.floor(summary.todayMinutes / 60)}h {summary.todayMinutes % 60}m
              </h4>
            </div>
          </div>
          <div className="col-12 col-sm-4">
            <div className="stat-card text-center p-3">
              <div className="text-muted small fw-medium">Weekly Total</div>
              <h4 className="fw-bold mb-0 text-dark">
                {String((summary.weekMinutes / 60).toFixed(1))} hrs
              </h4>
            </div>
          </div>
          <div className="col-12 col-sm-4">
            <div className="stat-card text-center p-3">
              <div className="text-muted small fw-medium">Logged Breaks</div>
              <h4 className="fw-bold mb-0 text-dark">{summary.totalBreakMinutes} mins</h4>
            </div>
          </div>
        </div>
      )}

      {/* Session History */}
      <div className="bg-white p-4 rounded-4 border shadow-sm">
        <h5 className="fw-bold mb-3 text-dark d-flex align-items-center gap-2">
          <i className="bi-clock-history text-primary"></i> Recent Study Sessions
        </h5>

        {history.length === 0 ? (
          <div className="text-center py-4 text-muted small">No past sessions recorded yet.</div>
        ) : (
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light small">
                <tr>
                  <th>Task / Topic</th>
                  <th>Date & Time</th>
                  <th>Actual Duration</th>
                  <th>Break Taken</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody className="small">
                {history.map((s) => (
                  <tr key={s.id}>
                    <td>
                      <span className="fw-bold text-dark">{s.taskTitle || 'General Focus Session'}</span>
                      {s.courseCode && (
                        <span className="badge bg-light text-primary border ms-2">{s.courseCode}</span>
                      )}
                      {s.notes && <div className="text-muted small">{s.notes}</div>}
                    </td>
                    <td>{new Date(s.startTime).toLocaleString()}</td>
                    <td>
                      <span className="fw-semibold text-dark">{s.actualDurationMinutes || 0} mins</span>
                    </td>
                    <td>{s.breakDurationMinutes || 0} mins</td>
                    <td>
                      <span className={`badge ${s.status === 'COMPLETED' ? 'bg-success' : 'bg-secondary'}`}>
                        {s.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Conclude Session Modal */}
      {showEndModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow border-0" style={{ borderRadius: '1rem' }}>
              <div className="modal-header py-3 px-4 border-bottom">
                <h5 className="modal-title fw-bold">Conclude Study Session</h5>
                <button type="button" className="btn-close" onClick={() => setShowEndModal(false)}></button>
              </div>

              <div className="modal-body p-4">
                <div className="text-center mb-4">
                  <span className="display-6 fw-bold text-dark">{formatTimer(elapsedSeconds)}</span>
                  <div className="text-muted small">Elapsed Focus Duration</div>
                </div>

                <div className="mb-3">
                  <label className="form-label small fw-semibold">Break Duration Taken (Minutes)</label>
                  <input
                    type="number"
                    min="0"
                    max="180"
                    className="form-control"
                    value={breakMinutes}
                    onChange={(e) => setBreakMinutes(e.target.value)}
                  />
                  <div className="form-text small">Log any pause or Pomodoro breaks during this session.</div>
                </div>

                {activeSession?.taskId && (
                  <div className="form-check mb-3">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="completeCheck"
                      checked={markCompleted}
                      onChange={(e) => setMarkCompleted(e.target.checked)}
                    />
                    <label className="form-check-label small fw-semibold" htmlFor="completeCheck">
                      Mark task <span className="text-primary">"{activeSession.taskTitle}"</span> as Completed
                    </label>
                  </div>
                )}

                <div className="mb-2">
                  <label className="form-label small fw-semibold">Concluding Notes (Optional)</label>
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Progress made, follow up items..."
                    value={endNotes}
                    onChange={(e) => setEndNotes(e.target.value)}
                  />
                </div>
              </div>

              <div className="modal-footer py-3 px-4 border-top">
                <button type="button" className="btn btn-outline-secondary" onClick={() => setShowEndModal(false)}>
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-success"
                  onClick={handleEnd}
                  disabled={submitting}
                >
                  {submitting ? 'Saving...' : 'Finish & Save'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
