# Student System Management

## Overview
This project is a student system management system that allows director to manage student information, including average grades, student archives, and information related to study(such as faculty, field of study etc), countries of living, place of living and more others. It provides a user-friendly interface that allows you to easily add, edit, and manage student information. 

The system is designed for use by a single administrator, who must be authorized to access the system's features and functions. The authorization process is handled by the Spring Security component, ensuring that only authorized users have access to sensitive information.

## Technologies Used
- Java 17
- REST API
- Spring Boot 3.0.1
- Spring Security
- Spring Data
- Maven
- Hibernate
- JUnit
- Thymeleaf
- Javascript

## Key Features
- Add, edit, and manage student information
- Calculate and track average grades for each student
- Store student information in a secure, centralized archive
- Generate reports with multiple filters to analyze student information
- Print documents related to student studies
- Uploading PDF files with grades for students after the end of the semester when administrator inputs average grade
- Automatically generated PDF file with final grades for student after adding him to archive that depends on grades during studies.

Once you have the necessary software installed, follow these steps to run the project:
1. Clone the repository to your local machine
2. Navigate to the project directory and run the following command: `./mvnw spring-boot:run`
3. Access the application by opening a web browser and navigating to `http://localhost:8080`
4. Login using the administrator account information provided to you

## Contact Information
If you have any questions or issues with the project, please feel free to reach out to the developer at s.hvozditskyi@gmail.com.
