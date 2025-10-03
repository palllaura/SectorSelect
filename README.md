# SectorSelect
SectorSelect is a simple web application for managing user submissions with business sector selection.

## Features
- Load hierarchical sector data from a json file (adjacency list model)
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
- Sector data is preloaded from src/main/resources/data/sectors.json.
- You can access the H2 console at:
  http://localhost:8080/h2-console
(JDBC URL: jdbc:h2:mem:sectorsdb)

### Frontend (Vanilla JavaScript + HTML)
- Open http://localhost:8080/index.html in a browser.

  
<img width="979" height="695" alt="Screenshot 2025-10-03 at 15 17 16" src="https://github.com/user-attachments/assets/dddb19e2-3d8a-4500-b927-3feae43d206d" />
