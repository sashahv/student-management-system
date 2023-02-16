package pl.dfjp.students.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.dfjp.students.entity.student.Student;

import java.time.LocalDate;
import java.time.ZoneId;

@SpringBootTest
class StudentServiceTest {
    @Autowired
    private StudentService studentService;

    Student student;

    @BeforeAll
    void setUp(){
        student = Student.builder()
                .name("Jan")
                .surname("Kowalski")
                .birthDate(LocalDate.now(ZoneId.of("Europe/Warsaw")))
                .build();
    }

    @Test
    void should_return_exceptions_when_student_information_is_null(){
        Student student = new Student();
        studentService.addStudent(student);
    }
}