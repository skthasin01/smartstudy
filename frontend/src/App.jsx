import React, { useState, useEffect } from 'react';
import { useAuth } from './context/AuthContext';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { NotificationsModal } from './components/NotificationsModal';
import { LoginView } from './views/LoginView';
import { RegisterView } from './views/RegisterView';
import { DashboardView } from './views/DashboardView';
import { TasksView } from './views/TasksView';
import { CoursesView } from './views/CoursesView';
import { StudyPlanView } from './views/StudyPlanView';
import { StudySessionView } from './views/StudySessionView';
import { WellnessView } from './views/WellnessView';
import { SettingsView } from './views/SettingsView';
import { api } from './services/api';

export function App() {
  const { user, token, loading } = useAuth();
  const [authMode, setAuthMode] = useState('login'); // 'login' or 'register'
  const [currentView, setCurrentView] = useState('dashboard');

  // Notifications state
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  // Active session tracking across views
  const [activeSession, setActiveSession] = useState(null);

  const fetchNotificationData = async () => {
    if (!token) return;
    try {
      const [count, list] = await Promise.all([
        api.getUnreadNotificationsCount(),
        api.getNotifications(0, 10),
      ]);
      setUnreadCount(count || 0);
      setNotifications(list?.content || []);
    } catch (err) {
      console.error('Error fetching notifications:', err);
    }
  };

  const fetchActiveSession = async () => {
    if (!token) return;
    try {
      const session = await api.getActiveSession();
      setActiveSession(session);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (token) {
      fetchNotificationData();
      fetchActiveSession();
    }
  }, [token, currentView]);

  const handleMarkAsRead = async (id) => {
    try {
      await api.markNotificationRead(id);
      fetchNotificationData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await api.markAllNotificationsRead();
      fetchNotificationData();
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteNotification = async (id) => {
    try {
      await api.deleteNotification(id);
      fetchNotificationData();
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading system...</span>
        </div>
      </div>
    );
  }

  // If unauthenticated, show Login or Register views
  if (!token) {
    if (authMode === 'register') {
      return <RegisterView onSwitchToLogin={() => setAuthMode('login')} />;
    }
    return <LoginView onSwitchToRegister={() => setAuthMode('register')} />;
  }

  const renderView = () => {
    switch (currentView) {
      case 'dashboard':
        return <DashboardView onNavigate={(view) => setCurrentView(view)} />;
      case 'tasks':
        return <TasksView />;
      case 'courses':
        return <CoursesView />;
      case 'study-plan':
        return <StudyPlanView />;
      case 'timer':
        return (
          <StudySessionView
            onSessionStateChange={(session) => setActiveSession(session)}
          />
        );
      case 'wellness':
        return <WellnessView onNavigate={(view) => setCurrentView(view)} />;
      case 'settings':
        return <SettingsView />;
      default:
        return <DashboardView onNavigate={(view) => setCurrentView(view)} />;
    }
  };

  const getPageTitle = () => {
    switch (currentView) {
      case 'dashboard': return 'Dashboard Overview';
      case 'tasks': return 'Study Task Management';
      case 'courses': return 'Course Management';
      case 'study-plan': return 'Rule-Based Study Schedule';
      case 'timer': return 'Live Study Session Timer';
      case 'wellness': return 'Daily Wellness Check-in';
      case 'settings': return 'Settings & Privacy Controls';
      default: return 'Smart Study System';
    }
  };

  return (
    <div className="app-container">
      <Sidebar currentView={currentView} setCurrentView={setCurrentView} />

      <div className="main-content">
        <Navbar
          pageTitle={getPageTitle()}
          unreadCount={unreadCount}
          onOpenNotifications={() => setShowNotifications(true)}
          activeSession={activeSession}
        />

        <main className="content-body">
          {renderView()}
        </main>
      </div>

      <NotificationsModal
        isOpen={showNotifications}
        onClose={() => setShowNotifications(false)}
        notifications={notifications}
        onMarkAsRead={handleMarkAsRead}
        onMarkAllRead={handleMarkAllRead}
        onDelete={handleDeleteNotification}
      />
    </div>
  );
}

export default App;
