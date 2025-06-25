A healthcare platform to enhance access to HIV treatment and medical services.

## Prerequisites
- Springboot >= 16.x
- Java jdk 24
- SQL Server 2019
- Maven

## Backend Setup
```bash
cd hiv-treatment-backend
mvn clean install
mvn spring-boot:run
```

## Database Configuration
- Create a SQL Server database named `hiv_treatment`.
- Update `application.properties` with your database credentials.

## API Documentation
Access at: `http://localhost:8080/swagger-ui.html`
