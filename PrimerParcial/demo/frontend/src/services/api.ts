import axios, { AxiosInstance, AxiosResponse } from 'axios';
import toast from 'react-hot-toast';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('token');
          window.location.href = '/login';
        }
        
        const message = error.response?.data?.message || 'An error occurred';
        toast.error(message);
        
        return Promise.reject(error);
      }
    );
  }

  setToken(token: string) {
    this.api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  removeToken() {
    delete this.api.defaults.headers.common['Authorization'];
  }

  // Auth API
  async login(username: string, password: string) {
    const response: AxiosResponse = await this.api.post('/auth/login', {
      username,
      password,
    });
    return response.data;
  }

  async register(username: string, email: string, password: string) {
    const response: AxiosResponse = await this.api.post('/auth/register', {
      username,
      email,
      password,
    });
    return response.data;
  }

  async getCurrentUser() {
    const response: AxiosResponse = await this.api.get('/auth/me');
    return response.data;
  }

  // Quiz API
  async getQuizzes() {
    const response: AxiosResponse = await this.api.get('/quiz');
    return response.data;
  }

  async getQuiz(id: string) {
    const response: AxiosResponse = await this.api.get(`/quiz/${id}`);
    return response.data;
  }

  async createQuiz(quizData: any) {
    const response: AxiosResponse = await this.api.post('/quiz', quizData);
    return response.data;
  }

  async updateQuiz(id: string, quizData: any) {
    const response: AxiosResponse = await this.api.put(`/quiz/${id}`, quizData);
    return response.data;
  }

  async deleteQuiz(id: string) {
    const response: AxiosResponse = await this.api.delete(`/quiz/${id}`);
    return response.data;
  }

  // Questions API
  async getQuestions(quizId: string) {
    const response: AxiosResponse = await this.api.get(`/questions/quiz/${quizId}`);
    return response.data;
  }

  async createQuestion(questionData: any) {
    const response: AxiosResponse = await this.api.post('/questions', questionData);
    return response.data;
  }

  async updateQuestion(id: string, questionData: any) {
    const response: AxiosResponse = await this.api.put(`/questions/${id}`, questionData);
    return response.data;
  }

  async deleteQuestion(id: string) {
    const response: AxiosResponse = await this.api.delete(`/questions/${id}`);
    return response.data;
  }

  // Quiz Attempts API
  async submitQuizAttempt(attemptData: any) {
    const response: AxiosResponse = await this.api.post('/quiz-attempts', attemptData);
    return response.data;
  }

  async getUserAttempts(userId: string) {
    const response: AxiosResponse = await this.api.get(`/quiz-attempts/user/${userId}`);
    return response.data;
  }

  async getQuizAttempts(quizId: string) {
    const response: AxiosResponse = await this.api.get(`/quiz-attempts/quiz/${quizId}`);
    return response.data;
  }

  // Stories API
  async getStories() {
    const response: AxiosResponse = await this.api.get('/stories');
    return response.data;
  }

  async getStory(id: string) {
    const response: AxiosResponse = await this.api.get(`/stories/${id}`);
    return response.data;
  }

  async createStory(storyData: any) {
    const response: AxiosResponse = await this.api.post('/stories', storyData);
    return response.data;
  }

  async updateStory(id: string, storyData: any) {
    const response: AxiosResponse = await this.api.put(`/stories/${id}`, storyData);
    return response.data;
  }

  async deleteStory(id: string) {
    const response: AxiosResponse = await this.api.delete(`/stories/${id}`);
    return response.data;
  }

  // Users API
  async getUsers() {
    const response: AxiosResponse = await this.api.get('/users');
    return response.data;
  }

  async getUser(id: string) {
    const response: AxiosResponse = await this.api.get(`/users/${id}`);
    return response.data;
  }

  async updateUser(id: string, userData: any) {
    const response: AxiosResponse = await this.api.put(`/users/${id}`, userData);
    return response.data;
  }

  async deleteUser(id: string) {
    const response: AxiosResponse = await this.api.delete(`/users/${id}`);
    return response.data;
  }
}

export const apiService = new ApiService();

// Specific API exports
export const authApi = {
  login: apiService.login.bind(apiService),
  register: apiService.register.bind(apiService),
  getCurrentUser: apiService.getCurrentUser.bind(apiService),
  setToken: apiService.setToken.bind(apiService),
  removeToken: apiService.removeToken.bind(apiService),
};

export const quizApi = {
  getQuizzes: apiService.getQuizzes.bind(apiService),
  getQuiz: apiService.getQuiz.bind(apiService),
  createQuiz: apiService.createQuiz.bind(apiService),
  updateQuiz: apiService.updateQuiz.bind(apiService),
  deleteQuiz: apiService.deleteQuiz.bind(apiService),
};

export const questionApi = {
  getQuestions: apiService.getQuestions.bind(apiService),
  createQuestion: apiService.createQuestion.bind(apiService),
  updateQuestion: apiService.updateQuestion.bind(apiService),
  deleteQuestion: apiService.deleteQuestion.bind(apiService),
};

export const attemptApi = {
  submitQuizAttempt: apiService.submitQuizAttempt.bind(apiService),
  getUserAttempts: apiService.getUserAttempts.bind(apiService),
  getQuizAttempts: apiService.getQuizAttempts.bind(apiService),
};

export const storyApi = {
  getStories: apiService.getStories.bind(apiService),
  getStory: apiService.getStory.bind(apiService),
  createStory: apiService.createStory.bind(apiService),
  updateStory: apiService.updateStory.bind(apiService),
  deleteStory: apiService.deleteStory.bind(apiService),
};

export const userApi = {
  getUsers: apiService.getUsers.bind(apiService),
  getUser: apiService.getUser.bind(apiService),
  updateUser: apiService.updateUser.bind(apiService),
  deleteUser: apiService.deleteUser.bind(apiService),
};