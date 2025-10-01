# SectorSelect
SectorSelect is a simple web application for managing user submissions with business sector selection.

## Features
- Load hierarchical sector data from an SQL file (adjacency list model)
- Input field for user's name
- Multi-select box for selecting one or more sectors
- "Agree to terms" checkbox
- Data validation (all fields are mandatory)
- Save user input to database
- Refill the form with stored data
- Allow user to edit their submission during the session
- H2 in-memory database with auto-loaded schema/data
- Backend built with Java Spring Boot

## Prerequisites
- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database
- Gradle
- Lombok (optional)
- Vanilla JavaScript + HTML (frontend)


## Installation
### Backend (Spring Boot)
1. Clone the repository:
   ```bash
   git clone https://github.com/palllaura/SectorSelect.git
   cd sectorselect

2. Open the project in your IDE (e.g., IntelliJ).

3. Build and run the backend:
   ```bash
   ./gradlew bootRun
4. The backend server will start at:
   http://localhost:8080

### Database (H2)
- H2 in-memory database starts automatically.
- Sector data is preloaded from src/main/resources/data.sql.

### Frontend (Vanilla JavaScript + HTML)
- Open http://localhost:8080/index.html in a browser.
