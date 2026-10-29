import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const TasksView = () => {
  const [tasks, setTasks] = useState([]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filters
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [priority, setPriority] = useState('');
  const [courseId, setCourseId] = useState('');

  // Modal State
  const [showModal, setShowModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [taskForm, setTaskForm] = useState({
    title: '',
    description: '',
    courseId: '',
    taskType: 'ASSIGNMENT',
    difficulty: 'MEDIUM',
    estimatedMinutes: 60,
    deadline: '',
    priority: 'MEDIUM',
    status: 'TODO',
  });
  const [submitting, setSubmitting] = useState(false);
  const [formError, setFormError] = useState('');

  const fetchTasks = async () => {
    try {
      setLoading(true);
      const res = await api.getTasks({
        search,
        status: status || undefined,
        difficulty: difficulty || undefined,
        priority: priority || undefined,
        courseId: courseId || undefined,
        page,
        size: 9,
        sortBy: 'deadline',
        sortDirection: 'asc',
      });
      setTasks(res.content || []);
      setTotalPages(res.totalPages || 0);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCourses = async () => {
    try {
      const data = await api.getCourses();
      setCourses(data || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    fetchTasks();
  }, [page, search, status, difficulty, priority, courseId]);

  const handleOpenCreateModal = () => {
    setEditingTask(null);
    // Default deadline to tomorrow at 23:59:00
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(23, 59, 0, 0);

    const pad = (n) => String(n).padStart(2, '0');
    const defaultDeadlineStr = `${tomorrow.getFullYear()}-${pad(tomorrow.getMonth() + 1)}-${pad(tomorrow.getDate())}T${pad(tomorrow.getHours())}:${pad(tomorrow.getMinutes())}:${pad(tomorrow.getSeconds())}`;

    setTaskForm({
      title: '',
      description: '',
      courseId: courses[0]?.id ? String(courses[0].id) : '',
      taskType: 'ASSIGNMENT',
      difficulty: 'MEDIUM',
      estimatedMinutes: 60,
      deadline: defaultDeadlineStr,
      priority: 'MEDIUM',
      status: 'TODO',
    });
    setFormError('');
    setShowModal(true);
  };

  const handleOpenEditModal = (task) => {
    setEditingTask(task);
    setTaskForm({
      title: task.title,
      description: task.description || '',
      courseId: task.courseId ? String(task.courseId) : '',
      taskType: task.taskType,
      difficulty: task.difficulty,
      estimatedMinutes: task.estimatedMinutes,
      deadline: task.deadline ? task.deadline.slice(0, 19) : '',
      priority: task.priority,
      status: task.status,
    });
    setFormError('');
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    setSubmitting(true);

    try {
      const payload = {
        ...taskForm,
        courseId: taskForm.courseId ? Number(taskForm.courseId) : null,
        estimatedMinutes: Number(taskForm.estimatedMinutes),
      };

      if (editingTask) {
        await api.updateTask(editingTask.id, payload);
      } else {
        await api.createTask(payload);
      }

      setShowModal(false);
      fetchTasks();
    } catch (err) {
      setFormError(err.message || 'Error saving task');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this study task?')) {
      try {
        await api.deleteTask(id);
        fetchTasks();
      } catch (err) {
        alert(err.message);
      }
    }
  };

  const handleQuickStatus = async (taskId, newStatus) => {
    try {
      await api.updateTaskStatus(taskId, newStatus);
      fetchTasks();
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="d-flex flex-column gap-4">
      {/* Header with Search and Create Button */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
        <div>
          <h4 className="fw-bold text-dark mb-1">Study Tasks</h4>
          <p className="text-muted small mb-0">Organize assignments, exams, and projects with estimated study hours</p>
        </div>
        <button className="btn btn-primary d-flex align-items-center gap-2" onClick={handleOpenCreateModal}>
          <i className="bi-plus-lg"></i> New Task
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white p-3 rounded-4 border shadow-sm">
        <div className="row g-2">
          <div className="col-12 col-md-4">
            <div className="input-group">
              <span className="input-group-text bg-light text-muted border-end-0">
                <i className="bi-search"></i>
              </span>
              <input
                type="text"
                className="form-control border-start-0 ps-0"
                placeholder="Search tasks..."
                value={search}
                onChange={(e) => { setSearch(e.target.value); setPage(0); }}
              />
            </div>
          </div>

          <div className="col-6 col-md-2">
            <select
              className="form-select"
              value={status}
              onChange={(e) => { setStatus(e.target.value); setPage(0); }}
            >
              <option value="">All Statuses</option>
              <option value="TODO">To-Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="MISSED">Missed</option>
            </select>
          </div>

          <div className="col-6 col-md-2">
            <select
              className="form-select"
              value={difficulty}
              onChange={(e) => { setDifficulty(e.target.value); setPage(0); }}
            >
              <option value="">All Difficulties</option>
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </div>

          <div className="col-6 col-md-2">
            <select
              className="form-select"
              value={priority}
              onChange={(e) => { setPriority(e.target.value); setPage(0); }}
            >
              <option value="">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="URGENT">Urgent</option>
            </select>
          </div>

          <div className="col-6 col-md-2">
            <select
              className="form-select"
              value={courseId}
              onChange={(e) => { setCourseId(e.target.value); setPage(0); }}
            >
              <option value="">All Courses</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>{c.courseCode}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Task Cards Grid */}
      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
        </div>
      ) : tasks.length === 0 ? (
        <div className="text-center py-5 bg-white rounded-4 border p-5">
          <i className="bi-card-checklist fs-1 text-muted d-block mb-3"></i>
          <h5 className="fw-bold text-dark">No tasks found</h5>
          <p className="text-muted small">Create a new task or adjust your active search and filter criteria.</p>
          <button className="btn btn-outline-primary" onClick={handleOpenCreateModal}>
            Create First Task
          </button>
        </div>
      ) : (
        <div className="row g-3">
          {tasks.map((task) => (
            <div key={task.id} className="col-12 col-md-6 col-xl-4">
              <div className="bg-white p-4 rounded-4 border shadow-sm h-100 d-flex flex-column justify-content-between">
                <div>
                  <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                    <div className="d-flex align-items-center gap-2">
                      {task.courseCode ? (
                        <span
                          className="badge text-white"
                          style={{ backgroundColor: task.colorCode || '#4F46E5' }}
                        >
                          {task.courseCode}
                        </span>
                      ) : (
                        <span className="badge bg-secondary">General</span>
                      )}
                      <span className="badge bg-light text-secondary border">{task.taskType}</span>
                    </div>

                    <span className={`badge ${
                      task.status === 'COMPLETED' ? 'bg-success' :
                      task.status === 'IN_PROGRESS' ? 'bg-primary' :
                      task.overdue ? 'bg-danger' : 'bg-warning text-dark'
                    }`}>
                      {task.overdue && task.status !== 'COMPLETED' ? 'OVERDUE' : task.status}
                    </span>
                  </div>

                  <h6 className="fw-bold text-dark mb-1">{task.title}</h6>
                  {task.description && (
                    <p className="small text-muted mb-3 text-truncate-2" style={{ maxHeight: '42px', overflow: 'hidden' }}>
                      {task.description}
                    </p>
                  )}

                  <div className="p-2 bg-light rounded-3 small text-muted mb-3 border">
                    <div className="d-flex justify-content-between mb-1">
                      <span>Difficulty:</span>
                      <strong className="text-dark">{task.difficulty}</strong>
                    </div>
                    <div className="d-flex justify-content-between mb-1">
                      <span>Priority:</span>
                      <strong className={task.priority === 'URGENT' ? 'text-danger' : 'text-dark'}>
                        {task.priority}
                      </strong>
                    </div>
                    <div className="d-flex justify-content-between mb-1">
                      <span>Est. Duration:</span>
                      <strong className="text-dark">{task.estimatedMinutes} mins</strong>
                    </div>
                    <div className="d-flex justify-content-between">
                      <span>Deadline:</span>
                      <strong className={task.overdue ? 'text-danger' : 'text-dark'}>
                        {new Date(task.deadline).toLocaleDateString()}
                      </strong>
                    </div>
                  </div>
                </div>

                <div className="d-flex justify-content-between align-items-center pt-2 border-top">
                  <div className="btn-group">
                    {task.status !== 'COMPLETED' ? (
                      <button
                        className="btn btn-sm btn-outline-success"
                        onClick={() => handleQuickStatus(task.id, 'COMPLETED')}
                        title="Mark as Done"
                      >
                        <i className="bi-check-lg me-1"></i> Done
                      </button>
                    ) : (
                      <button
                        className="btn btn-sm btn-outline-secondary"
                        onClick={() => handleQuickStatus(task.id, 'TODO')}
                        title="Mark as Todo"
                      >
                        <i className="bi-arrow-counterclockwise me-1"></i> Reopen
                      </button>
                    )}

                    {task.status === 'TODO' && (
                      <button
                        className="btn btn-sm btn-outline-primary"
                        onClick={() => handleQuickStatus(task.id, 'IN_PROGRESS')}
                        title="Start Task"
                      >
                        <i className="bi-play-fill"></i>
                      </button>
                    )}
                  </div>

                  <div className="d-flex gap-1">
                    <button
                      className="btn btn-sm btn-light border"
                      onClick={() => handleOpenEditModal(task)}
                      title="Edit Task"
                    >
                      <i className="bi-pencil"></i>
                    </button>
                    <button
                      className="btn btn-sm btn-light border text-danger"
                      onClick={() => handleDelete(task.id)}
                      title="Delete Task"
                    >
                      <i className="bi-trash3"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="d-flex justify-content-center gap-2 mt-3">
          <button
            className="btn btn-sm btn-outline-secondary"
            disabled={page === 0}
            onClick={() => setPage(p => p - 1)}
          >
            Previous
          </button>
          <span className="align-self-center small fw-semibold text-muted">
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn-sm btn-outline-secondary"
            disabled={page >= totalPages - 1}
            onClick={() => setPage(p => p + 1)}
          >
            Next
          </button>
        </div>
      )}

      {/* Add / Edit Task Modal */}
      {showModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content shadow border-0" style={{ borderRadius: '1rem' }}>
              <div className="modal-header py-3 px-4 border-bottom">
                <h5 className="modal-title fw-bold">
                  {editingTask ? 'Edit Study Task' : 'Create New Study Task'}
                </h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="modal-body p-4">
                  {formError && (
                    <div className="alert alert-danger py-2 px-3 small mb-3">{formError}</div>
                  )}

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Task Title</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Implement Binary Search Tree"
                      value={taskForm.title}
                      onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })}
                      required
                    />
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-6">
                      <label className="form-label small fw-semibold">Course</label>
                      <select
                        className="form-select"
                        value={taskForm.courseId}
                        onChange={(e) => setTaskForm({ ...taskForm, courseId: e.target.value })}
                      >
                        <option value="">No Course (General)</option>
                        {courses.map((c) => (
                          <option key={c.id} value={c.id}>
                            {c.courseCode} - {c.courseName}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label small fw-semibold">Task Type</label>
                      <select
                        className="form-select"
                        value={taskForm.taskType}
                        onChange={(e) => setTaskForm({ ...taskForm, taskType: e.target.value })}
                      >
                        <option value="ASSIGNMENT">Assignment</option>
                        <option value="EXAM">Exam Prep</option>
                        <option value="PROJECT">Project Work</option>
                        <option value="PREPARATION">Reading / Preparation</option>
                      </select>
                    </div>
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-4">
                      <label className="form-label small fw-semibold">Difficulty</label>
                      <select
                        className="form-select"
                        value={taskForm.difficulty}
                        onChange={(e) => setTaskForm({ ...taskForm, difficulty: e.target.value })}
                      >
                        <option value="EASY">Easy (1.0x)</option>
                        <option value="MEDIUM">Medium (1.3x)</option>
                        <option value="HARD">Hard (1.6x)</option>
                      </select>
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label small fw-semibold">Priority</label>
                      <select
                        className="form-select"
                        value={taskForm.priority}
                        onChange={(e) => setTaskForm({ ...taskForm, priority: e.target.value })}
                      >
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="URGENT">Urgent</option>
                      </select>
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label small fw-semibold">Est. Minutes</label>
                      <input
                        type="number"
                        min="5"
                        max="1440"
                        className="form-control"
                        value={taskForm.estimatedMinutes}
                        onChange={(e) => setTaskForm({ ...taskForm, estimatedMinutes: e.target.value })}
                        required
                      />
                    </div>
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-6">
                      <label className="form-label small fw-semibold">Deadline</label>
                      <input
                        type="datetime-local"
                        className="form-control"
                        value={taskForm.deadline ? taskForm.deadline.slice(0, 16) : ''}
                        onChange={(e) => {
                          const val = e.target.value ? `${e.target.value}:00` : '';
                          setTaskForm({ ...taskForm, deadline: val });
                        }}
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label small fw-semibold">Status</label>
                      <select
                        className="form-select"
                        value={taskForm.status}
                        onChange={(e) => setTaskForm({ ...taskForm, status: e.target.value })}
                      >
                        <option value="TODO">To-Do</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="MISSED">Missed</option>
                      </select>
                    </div>
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Description (Optional)</label>
                    <textarea
                      className="form-control"
                      rows="3"
                      placeholder="Requirements, key topics to cover..."
                      value={taskForm.description}
                      onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })}
                    ></textarea>
                  </div>
                </div>

                <div className="modal-footer py-3 px-4 border-top">
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setShowModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Saving...' : editingTask ? 'Update Task' : 'Create Task'}
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
