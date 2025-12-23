 Java Swing Quiz Application

A desktop-based quiz application developed using **Java Swing** and **MySQL**, designed to demonstrate the practical application of **Object-Oriented Programming (OOP)** concepts with a clean **MVC architecture**.

This project provides an interactive quiz system with secure authentication, timed assessments, instant evaluation, and persistent performance tracking.

---

## Features

- Secure user login system  
- Multiple quiz categories (Java, General Knowledge, Mathematics)  
- Randomized question selection  
- Timer-based quiz with automatic submission  
- Instant score calculation and percentage display  
- Detailed question-by-question result review  
- Persistent storage of quiz results using MySQL  
- Structured MVC-based application design  

---

## Technologies Used

- Java (SE)
- Java Swing (GUI)
- MySQL
- JDBC
- MVC Architecture

---

## Project Structure
src/
├── model/ # Data models and business logic
├── view/ # Swing UI components
├── controller/ # Event handling and flow control
└── utils/ # Database connection and helpers

---

## Database Details

- Database: **MySQL**
- Tables used:
  - `users`
  - `quizzes`
  - `questions`
  - `user_scores`
- SQL schema is provided in the project files.
- Database connection is handled using JDBC.

> **Note:** Database credentials must be configured locally before running the application.

---

## How to Run the Project

1. Clone or download the repository
2. Open the project in **NetBeans / IntelliJ IDEA / Eclipse**
3. Import the provided MySQL database
4. Update database credentials in the connection file
5. Run the main application class

---

## Learning Outcomes

- Practical implementation of OOP principles
- Hands-on experience with Java Swing GUI development
- Database integration using JDBC
- Understanding of MVC architecture in desktop applications
- Improved code organization and maintainability

---

## Future Enhancements

- User registration system
- Admin panel for quiz management
- Performance analytics and graphical reports

---

## Author

**Lajpat Rai**  
Computer Systems Engineering Student

---

This project was developed as part of an academic coursework and is intended for learning and educational purposes.

