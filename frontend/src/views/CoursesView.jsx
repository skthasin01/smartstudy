import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const CoursesView = () => {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  const [form, setForm] = useState({
    courseCode: '',
    courseName: '',
    colorCode: '#4F46E5',
    description: '',
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const data = await api.getCourses();
      setCourses(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  const handleOpenCreate = () => {
    setEditingCourse(null);
    setForm({
      courseCode: '',
      courseName: '',
      colorCode: '#4F46E5',
      description: '',
    });
    setError('');
    setShowModal(true);
  };

  const handleOpenEdit = (course) => {
    setEditingCourse(course);
    setForm({
      courseCode: course.courseCode,
      courseName: course.courseName,
      colorCode: course.colorCode || '#4F46E5',
      description: course.description || '',
    });
    setError('');
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      if (editingCourse) {
        await api.updateCourse(editingCourse.id, form);
      } else {
        await api.createCourse(form);
      }
      setShowModal(false);
      fetchCourses();
    } catch (err) {
      setError(err.message || 'Failed to save course');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Delete this course? Associated tasks will lose their course tag.')) {
      try {
        await api.deleteCourse(id);
        fetchCourses();
      } catch (err) {
        alert(err.message);
      }
    }
  };

  const colorPresets = ['#4F46E5', '#06B6D4', '#10B981', '#F59E0B', '#EF4444', '#EC4899', '#8B5CF6'];

  return (
    <div className="d-flex flex-column gap-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
        <div>
          <h4 className="fw-bold text-dark mb-1">Courses & Subjects</h4>
          <p className="text-muted small mb-0">Manage courses and color-tag study tasks for clear schedule organization</p>
        </div>
        <button className="btn btn-primary d-flex align-items-center gap-2" onClick={handleOpenCreate}>
          <i className="bi-plus-lg"></i> Add Course
        </button>
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
        </div>
      ) : courses.length === 0 ? (
        <div className="text-center py-5 bg-white rounded-4 border p-5">
          <i className="bi-journal-code fs-1 text-muted d-block mb-3"></i>
          <h5 className="fw-bold text-dark">No courses registered yet</h5>
          <p className="text-muted small">Add your enrolled university courses to categorize tasks and study schedules.</p>
          <button className="btn btn-outline-primary" onClick={handleOpenCreate}>
            Add First Course
          </button>
        </div>
      ) : (
        <div className="row g-3">
          {courses.map((course) => (
            <div key={course.id} className="col-12 col-md-6 col-xl-4">
              <div
                className="bg-white p-4 rounded-4 border shadow-sm h-100 position-relative overflow-hidden"
                style={{ borderTop: `5px solid ${course.colorCode || '#4F46E5'}` }}
              >
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <span
                    className="badge text-white px-2 py-1 fs-6"
                    style={{ backgroundColor: course.colorCode || '#4F46E5' }}
                  >
                    {course.courseCode}
                  </span>
                  <div className="d-flex gap-1">
                    <button
                      className="btn btn-sm btn-light border"
                      onClick={() => handleOpenEdit(course)}
                      title="Edit"
                    >
                      <i className="bi-pencil"></i>
                    </button>
                    <button
                      className="btn btn-sm btn-light border text-danger"
                      onClick={() => handleDelete(course.id)}
                      title="Delete"
                    >
                      <i className="bi-trash3"></i>
                    </button>
                  </div>
                </div>

                <h5 className="fw-bold text-dark mb-1">{course.courseName}</h5>
                {course.description && (
                  <p className="text-muted small mb-3">{course.description}</p>
                )}

                <div className="pt-2 border-top text-muted small d-flex justify-content-between">
                  <span>Created:</span>
                  <span className="text-dark fw-medium">
                    {course.createdAt ? new Date(course.createdAt).toLocaleDateString() : 'Active'}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content shadow border-0" style={{ borderRadius: '1rem' }}>
              <div className="modal-header py-3 px-4 border-bottom">
                <h5 className="modal-title fw-bold">
                  {editingCourse ? 'Edit Course' : 'Add Course'}
                </h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="modal-body p-4">
                  {error && <div className="alert alert-danger py-2 px-3 small mb-3">{error}</div>}

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Course Code</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. CS201 or MATH101"
                      value={form.courseCode}
                      onChange={(e) => setForm({ ...form, courseCode: e.target.value })}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Course Name</label>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="e.g. Data Structures & Algorithms"
                      value={form.courseName}
                      onChange={(e) => setForm({ ...form, courseName: e.target.value })}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Color Tag</label>
                    <div className="d-flex align-items-center gap-2 mb-2">
                      <input
                        type="color"
                        className="form-control form-control-color"
                        value={form.colorCode}
                        onChange={(e) => setForm({ ...form, colorCode: e.target.value })}
                        title="Choose course color"
                      />
                      <span className="small text-muted">{form.colorCode}</span>
                    </div>
                    <div className="d-flex gap-2">
                      {colorPresets.map((preset) => (
                        <div
                          key={preset}
                          className="rounded-circle cursor-pointer border"
                          style={{
                            width: '24px',
                            height: '24px',
                            backgroundColor: preset,
                            cursor: 'pointer',
                            outline: form.colorCode === preset ? '2px solid #0f172a' : 'none',
                          }}
                          onClick={() => setForm({ ...form, colorCode: preset })}
                        ></div>
                      ))}
                    </div>
                  </div>

                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Description (Optional)</label>
                    <textarea
                      className="form-control"
                      rows="2"
                      placeholder="Instructor, credits, room..."
                      value={form.description}
                      onChange={(e) => setForm({ ...form, description: e.target.value })}
                    ></textarea>
                  </div>
                </div>

                <div className="modal-footer py-3 px-4 border-top">
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setShowModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Saving...' : editingCourse ? 'Update Course' : 'Create Course'}
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
