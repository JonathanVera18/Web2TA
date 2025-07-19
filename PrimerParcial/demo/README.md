# Quiz Platform - Production Ready Spring Boot Application

A comprehensive, production-ready Spring Boot application for managing quizzes with user authentication, JWT security, caching, and monitoring.

## 🚀 Features

- **Security**: JWT authentication, BCrypt password encryption, role-based access control
- **Performance**: Caching with Caffeine, pagination, optimized database queries
- **Monitoring**: Actuator endpoints, Prometheus metrics, comprehensive logging
- **Testing**: Unit tests, integration tests with TestContainers, security tests
- **Documentation**: OpenAPI/Swagger documentation
- **Rate Limiting**: Protection against abuse and DDoS attacks
- **Production Ready**: Docker support, CI/CD pipeline, environment-specific configurations

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.5
- **Java**: 17
- **Database**: PostgreSQL 15
- **Security**: Spring Security + JWT
- **Caching**: Caffeine
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: OpenAPI 3
- **Monitoring**: Spring Boot Actuator + Prometheus
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL (for local development)

## 🚀 Quick Start

### Development Environment

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Set up environment variables**
   ```bash
   export DATABASE_URL=jdbc:postgresql://localhost:5432/AutonomoWeb
   export DATABASE_USERNAME=postgres
   export DATABASE_PASSWORD=1234
   export JWT_SECRET=your-super-secret-jwt-key
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Or run locally**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Application: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator: http://localhost:8080/actuator

### Production Deployment

1. **Build the application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Build Docker image**
   ```bash
   docker build -t quiz-platform .
   ```

3. **Run with production configuration**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     -e DATABASE_URL=jdbc:postgresql://your-db-host:5432/quizdb \
     -e DATABASE_USERNAME=your-username \
     -e DATABASE_PASSWORD=your-password \
     -e JWT_SECRET=your-production-jwt-secret \
     quiz-platform
   ```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `DATABASE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/AutonomoWeb` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `1234` |
| `JWT_SECRET` | JWT signing secret | Random UUID |
| `JWT_EXPIRATION` | JWT expiration time (ms) | `86400000` (24h) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000,http://localhost:8080` |
| `RATE_LIMIT_ENABLED` | Enable rate limiting | `true` |
| `RATE_LIMIT_REQUESTS_PER_MINUTE` | Rate limit requests per minute | `60` |

### Profiles

- **dev**: Development configuration with debug logging
- **prod**: Production configuration with optimized settings

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run with coverage
```bash
mvn clean test jacoco:report
```

### Run integration tests
```bash
mvn test -Dtest=*IntegrationTest
```

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### Prometheus Metrics
```bash
curl http://localhost:8080/actuator/prometheus
```

## 🔒 Security

### Authentication
- JWT-based authentication
- BCrypt password encryption
- Role-based access control (ADMIN, USER)

### Rate Limiting
- Configurable rate limiting per minute
- Protection against abuse and DDoS attacks

### CORS
- Configurable CORS settings
- Secure by default

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification
Download the OpenAPI specification at:
```
http://localhost:8080/v3/api-docs
```

## 🐳 Docker

### Build Image
```bash
docker build -t quiz-platform .
```

### Run Container
```bash
docker run -p 8080:8080 quiz-platform
```

### Docker Compose
```bash
docker-compose up -d
```

## 🔄 CI/CD

The project includes a GitHub Actions workflow that:

1. **Tests**: Runs unit and integration tests
2. **Security Scan**: Performs vulnerability scanning with Trivy
3. **Build**: Creates Docker image
4. **Deploy**: Deploys to staging and production environments

### Manual Deployment

1. **Build and push Docker image**
   ```bash
   docker build -t your-registry/quiz-platform:latest .
   docker push your-registry/quiz-platform:latest
   ```

2. **Deploy to Kubernetes**
   ```bash
   kubectl apply -f k8s/
   ```

## 📈 Performance

### Caching
- Quiz data cached with Caffeine
- Configurable cache size and expiration
- Cache invalidation on data changes

### Database Optimization
- Connection pooling
- Optimized queries with JPA
- Database indexes on frequently queried fields

### Monitoring
- Application metrics with Micrometer
- Database connection monitoring
- Request/response time tracking

## 🛡️ Production Checklist

- [ ] Environment variables configured
- [ ] Database credentials secured
- [ ] JWT secret changed from default
- [ ] CORS origins restricted
- [ ] Rate limiting enabled
- [ ] Logging configured
- [ ] Health checks implemented
- [ ] Monitoring enabled
- [ ] SSL/TLS configured
- [ ] Database backups configured
- [ ] CI/CD pipeline tested
- [ ] Security scan passed

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API documentation at `/swagger-ui.html`