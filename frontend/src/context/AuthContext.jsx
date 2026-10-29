import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const savedToken = localStorage.getItem('token');
      const savedUser = localStorage.getItem('user');

      if (savedToken && savedUser) {
        try {
          setUser(JSON.parse(savedUser));
          setToken(savedToken);
        } catch (e) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (email, password) => {
    const data = await api.login({ email, password });
    const authUser = {
      id: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
    };
    setToken(data.accessToken);
    setUser(authUser);
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('user', JSON.stringify(authUser));
    return data;
  };

  const register = async (fullName, email, password, role) => {
    const data = await api.register({ fullName, email, password, role });
    const authUser = {
      id: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
    };
    setToken(data.accessToken);
    setUser(authUser);
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('user', JSON.stringify(authUser));
    return data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const updateUser = (updatedFields) => {
    setUser((prev) => {
      const updated = { ...prev, ...updatedFields };
      localStorage.setItem('user', JSON.stringify(updated));
      return updated;
    });
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
