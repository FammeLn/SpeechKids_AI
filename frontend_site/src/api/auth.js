import axios from 'axios';

const API_URL = 'http://localhost:8084/api';

// Функция для регистрации
export const registerUser = async (userData) => {
  const response = await axios.post(`${API_URL}/auth/register`, userData);
  return response.data;
};

// Функция для входа
export const loginUser = async (credentials) => {
  // credentials это { email, password }
  return await axios.post(`${API_URL}/auth/login`, credentials);
};

