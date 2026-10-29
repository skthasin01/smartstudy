import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const DashboardView = ({ onNavigate }) => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reassessing, setReassessing] = useState(false);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const data = await api.getDashboardAnalytics();
      setDashboard(data);
    } catch (err) {
      setError(err.message || 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const handleReassessBurnout = async () => {
    try {
      setReassessing(true);
      await api.assessBurnout();
      await fetchDashboard();
    } catch (err) {
      alert(err.message || 'Failed to re-assess burnout');
    } finally {
      setReassessing(false);
    }
  };

  const handleDismissRec = async (id) => {
    try {
      await api.dismissRecommendation(id);
      fetchDashboard();
    } catch (err) {
      console.error(err);
    }
  };

  const handleTaskStatus = async (taskId, newStatus) => {
    try {
      await api.updateTaskStatus(taskId, newStatus);
      fetchDashboard();
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center py-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading dashboard...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger d-flex align-items-center justify-content-between">
        <div>{error}</div>
        <button className="btn btn-sm btn-outline-danger" onClick={fetchDashboard}>Retry</button>
      </div>
    );
  }

  const workload = dashboard?.currentWorkload;
  const burnout = dashboard?.currentBurnout;
  const weeklyDist = dashboard?.weeklyStudyDistribution || {};

  // Find max minutes for scaling weekly bar chart
  const maxWeeklyMins = Math.max(60, ...Object.values(weeklyDist));

  const getRiskColor = (cat) => {
    switch (cat) {
      case 'LOW': return 'var(--success-color)';
      case 'MODERATE': return 'var(--warning-color)';
      case 'HIGH':
      case 'SEVERE': return 'var(--danger-color)';
      default: return 'var(--primary-color)';
    }
  };

  return (
    <div className="d-flex flex-column gap-4">
      {/* Top Banner / Welcome & Quick Actions */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 bg-white p-4 rounded-4 border shadow-sm">
        <div>
          <h4 className="fw-bold text-dark mb-1">
            Hello, {dashboard?.profile?.fullName || 'Student'}! 👋
          </h4>
          <p className="text-muted small mb-0">
            Daily goal: {dashboard?.profile?.dailyStudyGoalMinutes || 240} mins • Wellness tracking: {dashboard?.profile?.allowWellnessTracking ? 'Active' : 'Disabled'}
          </p>
        </div>

        <div className="d-flex flex-wrap gap-2">
          <button className="btn btn-primary d-flex align-items-center gap-2" onClick={() => onNavigate('timer')}>
            <i className="bi-stopwatch"></i> Start Timer
          </button>
          <button className="btn btn-outline-primary d-flex align-items-center gap-2" onClick={() => onNavigate('tasks')}>
            <i className="bi-plus-circle"></i> Add Task
          </button>
          <button className="btn btn-outline-secondary d-flex align-items-center gap-2" onClick={() => onNavigate('wellness')}>
            <i className="bi-heart-pulse"></i> Check-in
          </button>
        </div>
      </div>

      {/* Top Level Metric Cards */}
      <div className="row g-3">
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="stat-card d-flex align-items-center gap-3">
            <div className="card-icon-box bg-primary-subtle text-primary">
              <i className="bi-clock-history"></i>
            </div>
            <div>
              <div className="text-muted small fw-medium">Today's Study</div>
              <h3 className="fw-bold mb-0 text-dark">
                {Math.floor((dashboard?.todayStudyMinutes || 0) / 60)}h {(dashboard?.todayStudyMinutes || 0) % 60}m
              </h3>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="stat-card d-flex align-items-center gap-3">
            <div className="card-icon-box bg-info-subtle text-info">
              <i className="bi-calendar-week"></i>
            </div>
            <div>
              <div className="text-muted small fw-medium">This Week</div>
              <h3 className="fw-bold mb-0 text-dark">
                {String(((dashboard?.weekStudyMinutes || 0) / 60).toFixed(1))}h
              </h3>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="stat-card d-flex align-items-center gap-3">
            <div className="card-icon-box bg-warning-subtle text-warning">
              <i className="bi-hourglass-split"></i>
            </div>
            <div>
              <div className="text-muted small fw-medium">Pending Tasks</div>
              <h3 className="fw-bold mb-0 text-dark">{dashboard?.pendingTasksCount || 0}</h3>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="stat-card d-flex align-items-center gap-3">
            <div className="card-icon-box bg-success-subtle text-success">
              <i className="bi-check2-circle"></i>
            </div>
            <div>
              <div className="text-muted small fw-medium">Completed Tasks</div>
              <h3 className="fw-bold mb-0 text-dark">{dashboard?.completedTasksCount || 0}</h3>
            </div>
          </div>
        </div>
      </div>

      {/* Core Analytic Section: Workload & Burnout Assessments */}
      <div className="row g-4">
        {/* Workload Index Card */}
        <div className="col-12 col-lg-6">
          <div className="bg-white p-4 rounded-4 border shadow-sm h-100 d-flex flex-column justify-content-between">
            <div>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold mb-0 text-dark d-flex align-items-center gap-2">
                  <i className="bi-speedometer2 text-primary"></i> Workload Index
                </h5>
                <span className={`badge ${
                  workload?.riskCategory === 'LOW' ? 'badge-low' :
                  workload?.riskCategory === 'MODERATE' ? 'badge-moderate' :
                  workload?.riskCategory === 'HIGH' ? 'badge-high' : 'badge-severe'
                } px-2 py-1`}>
                  {workload?.riskCategory || 'LOW'} LOAD
                </span>
              </div>

              <div className="d-flex align-items-baseline gap-2 mb-2">
                <span className="display-5 fw-bold text-dark">{workload?.workloadScore || 0}</span>
                <span className="text-muted fs-5">/ 100</span>
              </div>

              <div className="risk-meter mb-3">
                <div
                  className="risk-meter-bar"
                  style={{
                    width: `${Math.min(100, workload?.workloadScore || 0)}%`,
                    backgroundColor: getRiskColor(workload?.riskCategory),
                  }}
                ></div>
              </div>

              <p className="text-secondary small mb-3">
                {workload?.explanation || 'Workload is dynamically calculated using pending task weights, deadlines, and daily study capacity.'}
              </p>
            </div>

            <div className="p-3 bg-light rounded-3 small text-muted border">
              <div className="row text-center g-2">
                <div className="col-4">
                  <span className="d-block fw-bold text-dark">{workload?.totalEstimatedMinutes || 0}m</span>
                  <span>Est. Work</span>
                </div>
                <div className="col-4 border-start border-end">
                  <span className="d-block fw-bold text-dark">{workload?.missedTasks || 0}</span>
                  <span className="text-danger fw-semibold">Overdue</span>
                </div>
                <div className="col-4">
                  <span className="d-block fw-bold text-dark">{workload?.dailyAvailableMinutes || 240}m</span>
                  <span>Daily Cap</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Burnout Risk Assessment Card */}
        <div className="col-12 col-lg-6">
          <div className="bg-white p-4 rounded-4 border shadow-sm h-100 d-flex flex-column justify-content-between">
            <div>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-bold mb-0 text-dark d-flex align-items-center gap-2">
                  <i className="bi-shield-exclamation text-warning"></i> Burnout Risk Early-Warning
                </h5>
                <button
                  className="btn btn-sm btn-outline-secondary d-flex align-items-center gap-1"
                  onClick={handleReassessBurnout}
                  disabled={reassessing}
                >
                  <i className={`bi-arrow-repeat ${reassessing ? 'spin' : ''}`}></i>
                  {reassessing ? 'Assessing...' : 'Re-assess'}
                </button>
              </div>

              <div className="d-flex align-items-baseline gap-2 mb-2">
                <span className="display-5 fw-bold text-dark">{burnout?.riskScore || 0}</span>
                <span className="text-muted fs-5">/ 100</span>
                <span className={`badge ms-2 ${
                  burnout?.riskLevel === 'LOW' ? 'badge-low' :
                  burnout?.riskLevel === 'MODERATE' ? 'badge-moderate' : 'badge-high'
                }`}>
                  {burnout?.riskLevel || 'LOW'} RISK
                </span>
              </div>

              <div className="risk-meter mb-3">
                <div
                  className="risk-meter-bar"
                  style={{
                    width: `${Math.min(100, burnout?.riskScore || 0)}%`,
                    backgroundColor: getRiskColor(burnout?.riskLevel),
                  }}
                ></div>
              </div>

              <div className="mb-3">
                <strong className="d-block small text-dark mb-1">Key Contributing Indicators:</strong>
                <p className="small text-secondary mb-0">
                  {burnout?.contributingFactors || 'No high strain indicators logged.'}
                </p>
              </div>
            </div>

            <div className="p-3 bg-light rounded-3 small text-muted border">
              <i className="bi-info-circle me-1 text-primary"></i>
              Productivity pacing early-warning tool. Not a medical or diagnostic service.
            </div>
          </div>
        </div>
      </div>

      {/* Weekly Study Distribution Chart & Upcoming Deadlines */}
      <div className="row g-4">
        {/* Weekly Study Distribution Chart */}
        <div className="col-12 col-lg-7">
          <div className="bg-white p-4 rounded-4 border shadow-sm h-100">
            <h5 className="fw-bold mb-3 text-dark d-flex align-items-center gap-2">
              <i className="bi-bar-chart-line-fill text-primary"></i> Weekly Study Distribution (Minutes)
            </h5>
            <div className="d-flex align-items-end justify-content-between gap-2 pt-4 pb-2" style={{ height: '220px' }}>
              {Object.entries(weeklyDist).map(([day, mins]) => {
                const heightPercent = maxWeeklyMins > 0 ? (mins / maxWeeklyMins) * 100 : 0;
                return (
                  <div key={day} className="d-flex flex-column align-items-center flex-grow-1 h-100 justify-content-end">
                    <span className="small text-muted fw-bold mb-1" style={{ fontSize: '0.75rem' }}>
                      {mins > 0 ? `${mins}m` : ''}
                    </span>
                    <div
                      className="w-100 rounded-3 transition-all"
                      style={{
                        height: `${Math.max(6, heightPercent)}%`,
                        backgroundColor: mins > 0 ? 'var(--primary-color)' : '#e2e8f0',
                        maxWidth: '48px',
                        transition: 'height 0.4s ease',
                      }}
                      title={`${day}: ${mins} minutes`}
                    ></div>
                    <span className="small fw-semibold text-secondary mt-2">{day}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Upcoming Deadlines */}
        <div className="col-12 col-lg-5">
          <div className="bg-white p-4 rounded-4 border shadow-sm h-100">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="fw-bold mb-0 text-dark d-flex align-items-center gap-2">
                <i className="bi-alarm-fill text-danger"></i> Approaching Deadlines
              </h5>
              <button className="btn btn-sm btn-link text-primary p-0 text-decoration-none" onClick={() => onNavigate('tasks')}>
                View All
              </button>
            </div>

            {(!dashboard?.upcomingDeadlines || dashboard.upcomingDeadlines.length === 0) ? (
              <div className="text-center py-4 text-muted">
                <i className="bi-calendar-check fs-2 text-success d-block mb-1"></i>
                <small>No critical deadlines in the next 7 days.</small>
              </div>
            ) : (
              <div className="d-flex flex-column gap-2">
                {dashboard.upcomingDeadlines.slice(0, 4).map((task) => (
                  <div key={task.id} className="p-2 border rounded-3 d-flex justify-content-between align-items-center">
                    <div className="overflow-hidden">
                      <div className="d-flex align-items-center gap-2">
                        {task.courseCode && (
                          <span
                            className="badge text-white"
                            style={{ backgroundColor: task.colorCode || '#4F46E5', fontSize: '0.7rem' }}
                          >
                            {task.courseCode}
                          </span>
                        )}
                        <span className="fw-semibold text-truncate small text-dark">{task.title}</span>
                      </div>
                      <small className="text-muted d-block" style={{ fontSize: '0.75rem' }}>
                        Due: {new Date(task.deadline).toLocaleString()}
                      </small>
                    </div>
                    <button
                      className="btn btn-sm btn-outline-success py-1 px-2 border-0"
                      onClick={() => handleTaskStatus(task.id, 'COMPLETED')}
                      title="Mark as completed"
                    >
                      <i className="bi-check-lg fs-5"></i>
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Personalized Recommendations Section */}
      {dashboard?.recentRecommendations && dashboard.recentRecommendations.length > 0 && (
        <div className="bg-white p-4 rounded-4 border shadow-sm">
          <h5 className="fw-bold mb-3 text-dark d-flex align-items-center gap-2">
            <i className="bi-lightbulb-fill text-warning"></i> Personalized Action Recommendations
          </h5>
          <div className="row g-3">
            {dashboard.recentRecommendations.map((rec) => (
              <div key={rec.id} className="col-12 col-md-6 col-xl-4">
                <div className="p-3 rounded-3 border bg-light h-100 d-flex flex-column justify-content-between">
                  <div>
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <span className="badge bg-primary-subtle text-primary fw-semibold">{rec.type}</span>
                      <button
                        className="btn btn-sm btn-link text-muted p-0"
                        onClick={() => handleDismissRec(rec.id)}
                        title="Dismiss"
                      >
                        <i className="bi-x-lg"></i>
                      </button>
                    </div>
                    <h6 className="fw-bold text-dark mb-1">{rec.title}</h6>
                    <p className="small text-secondary mb-3">{rec.message}</p>
                  </div>
                  <div className="text-muted" style={{ fontSize: '0.75rem' }}>
                    Priority: <span className="fw-semibold text-dark">{rec.priority}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
