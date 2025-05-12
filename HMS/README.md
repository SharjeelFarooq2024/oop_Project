# Hospital Management System (HMS)

## Overview
The Hospital Management System (HMS) is a comprehensive JavaFX application designed to streamline hospital operations by providing an integrated platform for patients, doctors, and administrators. The system facilitates appointment management, patient record-keeping, vital sign monitoring, emergency alerts, and secure communication between healthcare providers and patients.

## Features

### For Patients
- **User Authentication**: Secure login and registration
- **Appointment Booking**: Schedule appointments with preferred doctors
- **Vital Sign Upload**: Record and monitor health metrics
- **Medical History**: Access personal medical records and history
- **Feedback System**: View doctor recommendations and prescribed medications
- **Emergency Alerts**: Send emergency notifications to medical staff
- **Chat System**: Communicate directly with assigned doctors
- **Video Consultation**: Virtual appointments through integrated video calling

### For Doctors
- **Patient Management**: View and manage assigned patients
- **Appointment Handling**: Accept, reschedule, or complete appointments
- **Medical Feedback**: Provide medical advice and prescribe medications
- **Emergency Response**: Receive and respond to patient emergencies
- **Patient Monitoring**: Track patient vital signs and health metrics
- **Communication Tools**: Chat and video call with patients

### For Administrators
- **User Management**: Add, view, update, and delete system users
- **Report Generation**: Generate reports for system activities
- **System Monitoring**: View logs and system performance
- **Dashboard**: Overview of hospital operations and statistics

## Architecture

The application follows a Model-View-Controller (MVC) architecture with clear separation of concerns:

### Model
- Contains data objects like User, Patient, Doctor, Admin, Appointment, etc.
- Implements business logic for data manipulation

### View
- JavaFX FXML files define the user interface
- CSS styling creates a modern and intuitive design

### Controller
- Handles user interactions and updates both model and view
- Manages navigation between different screens

### Services
- Provides business logic for various system operations
- Includes user management, appointment scheduling, reporting, etc.

### DAO (Data Access Objects)
- Manages data persistence using JSON files
- Provides an interface for CRUD operations on data

## Technologies Used

- **Java**: Core programming language (JDK 11+)
- **JavaFX**: GUI framework for desktop application
- **Jackson**: JSON serialization/deserialization
- **Maven**: Dependency management and build automation

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans, etc.)

### Installation

1. Clone the repository or download the source code
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory
   ```
   cd HMS
   ```

3. Build the project using Maven
   ```
   mvn clean install
   ```

4. Run the application
   ```
   mvn javafx:run
   ```

### Default Users

The system comes with some default users for testing:
- **Admin**: email: admin@hospital.com, password: admin123
- **Doctor**: email: doctor@hospital.com, password: doctor123
- **Patient**: email: patient@hospital.com, password: patient123

## Project Structure

```
HMS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── myapp/
│   │   │           ├── app/           # Application entry point
│   │   │           ├── backend/       # Backend functionality
│   │   │           │   ├── dao/       # Data Access Objects
│   │   │           │   ├── model/     # Data models
│   │   │           │   ├── services/  # Business logic
│   │   │           │   └── util/      # Utility classes
│   │   │           └── frontend/      # Frontend functionality
│   │   │               └── controllers/ # UI controllers
│   │   └── resources/
│   │       ├── css/                   # Stylesheets
│   │       ├── fxml/                  # JavaFX layout files
│   │       └── config/                # Configuration files
│   └── test/                          # Test files
└── data/                              # JSON data storage
```

## Data Storage

The application uses JSON files stored in the `data/` directory for persistence:
- `AdminUsers.json`: Stores admin user details
- `Patients.json`: Stores patient information
- `Doctors.json`: Stores doctor profiles
- `Appointments.json`: Stores appointment data
- `Vitals.json`: Stores patient vital sign records

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- JavaFX community for the excellent UI framework
- All contributors who participated in this project