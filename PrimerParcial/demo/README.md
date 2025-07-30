# Quiz Application

A modern, full-stack quiz application built with Spring Boot and React. This application provides a complete learning platform with user authentication, quiz management, and interactive assessments.

## 🚀 Features

### Backend (Spring Boot)
- **JWT Authentication**: Secure user authentication with JWT tokens
- **RESTful APIs**: Complete CRUD operations for quizzes, questions, and user management
- **PostgreSQL Database**: Robust data persistence with PostgreSQL
- **Spring Security**: Comprehensive security implementation
- **Actuator**: Health checks and monitoring endpoints
- **CORS Support**: Cross-origin resource sharing configuration
- **Production Ready**: Docker containerization and environment configuration

### Frontend (React + TypeScript)
- **Modern UI**: Material-UI components with responsive design
- **TypeScript**: Type-safe development
- **React Router**: Client-side routing
- **React Query**: Efficient data fetching and caching
- **Form Validation**: React Hook Form with comprehensive validation
- **Toast Notifications**: User feedback with react-hot-toast
- **Responsive Design**: Mobile-first approach

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JSON Web Tokens)**
- **Maven**

### Frontend
- **React 18**
- **TypeScript**
- **Material-UI (MUI)**
- **React Router DOM**
- **React Query**
- **React Hook Form**
- **Axios**
- **React Hot Toast**

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 15 or higher
- Docker and Docker Compose (for production deployment)

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd PrimerParcial/demo
   ```

2. **Start the application**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - Database: localhost:5432

### Option 2: Local Development

#### Backend Setup

1. **Install dependencies**
   ```bash
   cd PrimerParcial/demo
   mvn clean install
   ```

2. **Configure database**
   - Create PostgreSQL database named `AutonomoWeb`
   - Update `application-dev.properties` with your database credentials

3. **Run the application**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

#### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start development server**
   ```bash
   npm start
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/AutonomoWeb` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `1234` |
| `JWT_SECRET` | JWT signing secret | `MySuperSecretKey123!` |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `36000000` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000` |

### Profiles

- **dev**: Development configuration with debug logging
- **prod**: Production configuration with optimized settings

## 📚 API Documentation

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/me` - Get current user
- `POST /api/auth/refresh` - Refresh JWT token

### Quiz Endpoints

- `GET /api/quiz` - Get all quizzes
- `GET /api/quiz/{id}` - Get quiz by ID
- `POST /api/quiz` - Create new quiz
- `PUT /api/quiz/{id}` - Update quiz
- `DELETE /api/quiz/{id}` - Delete quiz

### Question Endpoints

- `GET /api/questions/quiz/{quizId}` - Get questions for quiz
- `POST /api/questions` - Create new question
- `PUT /api/questions/{id}` - Update question
- `DELETE /api/questions/{id}` - Delete question

### User Endpoints

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## 🏗️ Project Structure

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/          # Security and configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── model/          # Entity models
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── service/        # Business logic
│   │   │   └── security/       # Security components
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/                   # Unit tests
├── frontend/
│   ├── src/
│   │   ├── components/         # Reusable components
│   │   ├── contexts/          # React contexts
│   │   ├── pages/             # Page components
│   │   ├── services/          # API services
│   │   └── types/             # TypeScript types
│   ├── public/
│   └── package.json
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🧪 Testing

### Backend Tests
```bash
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Production Deployment

1. **Build the application**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

2. **Set environment variables**
   ```bash
   export JWT_SECRET=your-secure-jwt-secret
   export DATABASE_PASSWORD=your-secure-password
   ```

3. **Access the application**
   - Frontend: https://your-domain.com
   - Backend: https://your-domain.com/api

### Environment-Specific Configurations

- **Development**: Uses `application-dev.properties`
- **Production**: Uses `application-prod.properties`

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection headers

## 📊 Monitoring

- Spring Boot Actuator endpoints
- Health checks
- Application metrics
- Database connection monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team

## 🔄 Updates

To update the application:

1. **Pull latest changes**
   ```bash
   git pull origin main
   ```

2. **Rebuild containers**
   ```bash
   docker-compose down
   docker-compose up -d --build
   ```

---

**Note**: This application is production-ready with comprehensive security, monitoring, and deployment configurations. Make sure to update the default passwords and JWT secrets before deploying to production.