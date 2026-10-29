const BASE_URL = 'http://localhost:8080/api';

const getHeaders = () => {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
};

const handleResponse = async (response) => {
  if (response.status === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
    throw new Error('Session expired. Please log in again.');
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    const errorMsg = data?.message || data?.error || 'An unexpected error occurred';
    throw new Error(errorMsg);
  }

  return data?.data !== undefined ? data.data : data;
};

export const api = {
  // Auth
  login: async (credentials) => {
    const res = await fetch(`${BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });
    return handleResponse(res);
  },

  register: async (userData) => {
    const res = await fetch(`${BASE_URL}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData),
    });
    return handleResponse(res);
  },

  getProfile: async () => {
    const res = await fetch(`${BASE_URL}/users/me`, { headers: getHeaders() });
    return handleResponse(res);
  },

  updateProfile: async (data) => {
    const res = await fetch(`${BASE_URL}/users/profile`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  changePassword: async (data) => {
    const res = await fetch(`${BASE_URL}/users/password`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  // Courses
  getCourses: async () => {
    const res = await fetch(`${BASE_URL}/courses`, { headers: getHeaders() });
    return handleResponse(res);
  },

  createCourse: async (data) => {
    const res = await fetch(`${BASE_URL}/courses`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  updateCourse: async (id, data) => {
    const res = await fetch(`${BASE_URL}/courses/${id}`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  deleteCourse: async (id) => {
    const res = await fetch(`${BASE_URL}/courses/${id}`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Tasks
  getTasks: async (params = {}) => {
    const query = new URLSearchParams();
    if (params.search) query.append('search', params.search);
    if (params.status) query.append('status', params.status);
    if (params.difficulty) query.append('difficulty', params.difficulty);
    if (params.priority) query.append('priority', params.priority);
    if (params.courseId) query.append('courseId', params.courseId);
    if (params.page !== undefined) query.append('page', params.page);
    if (params.size !== undefined) query.append('size', params.size);
    if (params.sortBy) query.append('sortBy', params.sortBy);
    if (params.sortDirection) query.append('sortDirection', params.sortDirection);

    const res = await fetch(`${BASE_URL}/tasks?${query.toString()}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  createTask: async (data) => {
    const res = await fetch(`${BASE_URL}/tasks`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  updateTask: async (id, data) => {
    const res = await fetch(`${BASE_URL}/tasks/${id}`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  updateTaskStatus: async (id, status) => {
    const res = await fetch(`${BASE_URL}/tasks/${id}/status?status=${status}`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  deleteTask: async (id) => {
    const res = await fetch(`${BASE_URL}/tasks/${id}`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Study Plans
  generatePlan: async (data) => {
    const res = await fetch(`${BASE_URL}/study-plans/generate`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  getCurrentPlan: async () => {
    const res = await fetch(`${BASE_URL}/study-plans/current`, { headers: getHeaders() });
    return handleResponse(res);
  },

  updatePlanItemStatus: async (itemId, data) => {
    const res = await fetch(`${BASE_URL}/study-plans/items/${itemId}/status`, {
      method: 'PATCH',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  // Study Sessions
  startSession: async (data) => {
    const res = await fetch(`${BASE_URL}/study-sessions/start`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data || {}),
    });
    return handleResponse(res);
  },

  endSession: async (data) => {
    const res = await fetch(`${BASE_URL}/study-sessions/end`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data || {}),
    });
    return handleResponse(res);
  },

  getActiveSession: async () => {
    const res = await fetch(`${BASE_URL}/study-sessions/active`, { headers: getHeaders() });
    return handleResponse(res);
  },

  getSessionHistory: async (page = 0, size = 10) => {
    const res = await fetch(`${BASE_URL}/study-sessions/history?page=${page}&size=${size}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  getSessionSummary: async () => {
    const res = await fetch(`${BASE_URL}/study-sessions/summary`, { headers: getHeaders() });
    return handleResponse(res);
  },

  // Daily Check-ins
  getTodayCheckin: async () => {
    const res = await fetch(`${BASE_URL}/check-ins/today`, { headers: getHeaders() });
    return handleResponse(res);
  },

  submitCheckin: async (data) => {
    const res = await fetch(`${BASE_URL}/check-ins`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  getCheckinHistory: async (page = 0, size = 14) => {
    const res = await fetch(`${BASE_URL}/check-ins/history?page=${page}&size=${size}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  deleteCheckinData: async () => {
    const res = await fetch(`${BASE_URL}/check-ins/my-data`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Workload
  getCurrentWorkload: async () => {
    const res = await fetch(`${BASE_URL}/workload/current`, { headers: getHeaders() });
    return handleResponse(res);
  },

  calculateWorkload: async () => {
    const res = await fetch(`${BASE_URL}/workload/calculate`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  getWorkloadTrends: async (days = 14) => {
    const res = await fetch(`${BASE_URL}/workload/trends?days=${days}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  // Burnout
  getCurrentBurnout: async () => {
    const res = await fetch(`${BASE_URL}/burnout/current`, { headers: getHeaders() });
    return handleResponse(res);
  },

  assessBurnout: async () => {
    const res = await fetch(`${BASE_URL}/burnout/assess`, {
      method: 'POST',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  getBurnoutTrends: async (days = 14) => {
    const res = await fetch(`${BASE_URL}/burnout/trends?days=${days}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  // Recommendations
  getRecommendations: async () => {
    const res = await fetch(`${BASE_URL}/recommendations`, { headers: getHeaders() });
    return handleResponse(res);
  },

  markRecommendationRead: async (id) => {
    const res = await fetch(`${BASE_URL}/recommendations/${id}/read`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  dismissRecommendation: async (id) => {
    const res = await fetch(`${BASE_URL}/recommendations/${id}/dismiss`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Notifications
  getNotifications: async (page = 0, size = 15) => {
    const res = await fetch(`${BASE_URL}/notifications?page=${page}&size=${size}`, { headers: getHeaders() });
    return handleResponse(res);
  },

  getUnreadNotificationsCount: async () => {
    const res = await fetch(`${BASE_URL}/notifications/unread-count`, { headers: getHeaders() });
    return handleResponse(res);
  },

  markNotificationRead: async (id) => {
    const res = await fetch(`${BASE_URL}/notifications/${id}/read`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  markAllNotificationsRead: async () => {
    const res = await fetch(`${BASE_URL}/notifications/read-all`, {
      method: 'PATCH',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  deleteNotification: async (id) => {
    const res = await fetch(`${BASE_URL}/notifications/${id}`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Settings
  getSettings: async () => {
    const res = await fetch(`${BASE_URL}/settings`, { headers: getHeaders() });
    return handleResponse(res);
  },

  updateSettings: async (data) => {
    const res = await fetch(`${BASE_URL}/settings`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(res);
  },

  clearWellnessData: async () => {
    const res = await fetch(`${BASE_URL}/settings/wellness-data`, {
      method: 'DELETE',
      headers: getHeaders(),
    });
    return handleResponse(res);
  },

  // Analytics Dashboard
  getDashboardAnalytics: async () => {
    const res = await fetch(`${BASE_URL}/analytics/dashboard`, { headers: getHeaders() });
    return handleResponse(res);
  },
};
