[![codecov](https://codecov.io/gh/konx8/ShortLinkApp/branch/main/graph/badge.svg)](https://codecov.io/gh/NAZWA_UZYTKOWNIKA/NAZWA_REPO)


# 📎 Link Shortening Service

## 🧾 1. Project Description
This project is a simple link shortening service. Users can submit a URL and receive a shortened version. Accessing the shortened link will redirect to the original URL.

## 📚 About the Project

This service allows authenticated users to:
- Register and log in
- Submit original URLs to generate short links (optionally with custom short codes)
- Access analytics (how many times a link was used)


## API Endpoints

- `POST /sl/auth/register` – crate user  
  **Consumes:** `application/json`  
  **Body:** creating new user in db
    - `username` - user name 
    - `password` - password


- `POST /sl/auth/login` – login to get token     
  **Consumes:** `application/json`  
  **Body:** user login and password to login 
    - `username` - user name
    - `password` - password   
**Returning:** token


- `POST /sl/auth/login` – login to get token  
  **Consumes:** `application/json`  
  **Body:** partial movie data to update
    - `url` - origin url to the page
    - `customCode` (optional) - custom short code 

- `GET /sl/{shortCode}` – getting redirect to page by code  


- `GET /sl/analytics` – getting information about code, url and clicked counter 


## 🚀 Instructions: Setup, Run & Test

### 📦 Prerequisites

- Java 17+
- Maven 3.8+
- Git

### 🛠️ Setup

1. **Copy repository**:
   ```bash
   git clone https://github.com/konx8/ShortLinkApp.git
2. **Run the App**
    ```bash
   mvn spring-boot:run
   ```
3. **Run Tests**
    ```bash
   mvn test
   ```   


## Technologies
- Java 21
- Spring Boot 3.5.4
- Maven
- JUnit 5 + Mockito
- H2 (in-memory test database)
- JaCoCo (test coverage measurement)
- GitHub Actions (CI/CD)
- Codecov (test coverage reporting)

## Architecture & Design
- **Modular service** with clear separation of concerns.
- **JWT-based authentication** for secure API access.
- **JaCoCo + Codecov** – test coverage is measured locally using JaCoCo and reported to Codecov for online tracking.
- **GitHub Actions CI/CD** – automatic build, test execution, and code coverage reporting on every commit and pull request.

