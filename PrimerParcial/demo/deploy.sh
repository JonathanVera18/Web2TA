#!/bin/bash

# Production Deployment Script for Quiz Application
# This script sets up and deploys the complete application stack

set -e  # Exit on any error

echo "🚀 Starting Quiz Application Deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Set environment variables
export JWT_SECRET=${JWT_SECRET:-"$(openssl rand -base64 32)"}
export DATABASE_PASSWORD=${DATABASE_PASSWORD:-"$(openssl rand -base64 16)"}
export NODE_ENV=production

print_status "Environment variables set:"
print_status "JWT_SECRET: ${JWT_SECRET:0:20}..."
print_status "DATABASE_PASSWORD: ${DATABASE_PASSWORD:0:20}..."

# Create .env file for frontend
cat > frontend/.env << EOF
REACT_APP_API_URL=http://localhost:8080/api
GENERATE_SOURCEMAP=false
EOF

print_status "Created frontend environment file"

# Build and start the application
print_status "Building and starting application containers..."

# Stop any existing containers
docker-compose down --remove-orphans

# Build and start the application
docker-compose up -d --build

# Wait for services to be ready
print_status "Waiting for services to be ready..."

# Wait for database
print_status "Waiting for database..."
until docker-compose exec -T postgres pg_isready -U postgres; do
    sleep 2
done

# Wait for backend
print_status "Waiting for backend..."
until curl -f http://localhost:8080/api/actuator/health; do
    sleep 5
done

# Wait for frontend
print_status "Waiting for frontend..."
until curl -f http://localhost:3000; do
    sleep 5
done

print_status "✅ All services are running!"

# Display service information
echo ""
print_status "Application is now running:"
echo "  🌐 Frontend: http://localhost:3000"
echo "  🔧 Backend API: http://localhost:8080/api"
echo "  🗄️  Database: localhost:5432"
echo "  📊 Health Check: http://localhost:8080/api/actuator/health"
echo ""

# Display container status
print_status "Container status:"
docker-compose ps

echo ""
print_warning "Important notes:"
echo "  - Default database credentials: postgres/1234"
echo "  - Change default passwords in production"
echo "  - Update JWT_SECRET in production"
echo "  - Configure proper SSL certificates for production"
echo ""

print_status "Deployment completed successfully! 🎉"

# Optional: Show logs
read -p "Would you like to see the application logs? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker-compose logs -f
fi