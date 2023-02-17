package pl.dfjp.students.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeByAcademicYearRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ArchivedStudentService {
    private final ArchivedStudentRepository archivedStudentRepository;
    private final StudentRepository studentRepository;
    private final AttachmentService attachmentService;
    private final ListOfGradesService listOfGradesService;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;
    private final AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository;

    @Autowired
    public ArchivedStudentService(ArchivedStudentRepository archivedStudentRepository,
                                  StudentRepository studentRepository,
                                  AttachmentService attachmentService,
                                  ListOfGradesService listOfGradesService,
                                  AverageGradeBySemesterRepository averageGradeBySemesterRepository,
                                  AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository){
        this.archivedStudentRepository = archivedStudentRepository;
        this.studentRepository = studentRepository;
        this.attachmentService = attachmentService;
        this.listOfGradesService = listOfGradesService;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
        this.averageGradeByAcademicYearRepository = averageGradeByAcademicYearRepository;
    }

    public List<ArchivedStudent> listAll(String keyword) {
        if (keyword != null) {
            return archivedStudentRepository.findAll(keyword);
        }
        return archivedStudentRepository.findAll();
    }

    public Page<ArchivedStudent> listAllByYearOfGraduation(String yearOfGraduation,
                                                           Page<ArchivedStudent> page) {
        if (yearOfGraduation != null) {
            return archivedStudentRepository.findAllByYearOfGraduation(Integer.parseInt(yearOfGraduation), page.getPageable());
        }
        return page;
    }

    @Transactional
    public void archiveStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException(String.format("Student z indeksem [%d] nie znaleziony", studentId))
        );

        ArchivedStudent archivedStudent = new ArchivedStudent();
        archivedStudent.setName(student.getName());
        archivedStudent.setSurname(student.getSurname());
        archivedStudent.setFatherName(student.getFatherName());
        archivedStudent.setGender(student.getGender());
        archivedStudent.setPassportNumber(student.getPassportNumber());
        archivedStudent.setPhoneNumber(student.getPhoneNumber());
        archivedStudent.setBirthDate(student.getBirthDate());
        archivedStudent.setPlaceOfBirth(student.getPlaceOfBirth());
        archivedStudent.setCountryOfBirth(student.getCountryOfBirth());
        archivedStudent.setCitizenship(student.getCitizenship());
        archivedStudent.setNationality(student.getNationality());
        archivedStudent.setPermanentAddress(student.getPermanentAddress());
        archivedStudent.setStudy(student.getStudy());
        archivedStudent.setAdditionalInformation(student.getAdditionalInformation());
        archivedStudent.setYearOfGraduation(LocalDate.now().getYear());
        archivedStudent.setYearOfStarting(student.getScholarship().getDateOfGetting().getYear());
        archivedStudentRepository.save(archivedStudent);

        try {
            attachmentService.saveAttachmentForArchivedStudent(archivedStudent.getId(),
                    listOfGradesService.generateMultipartFileWithGrades(studentId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        averageGradeBySemesterRepository.deleteByStudyId(student.getStudy().getId());
        averageGradeByAcademicYearRepository.deleteByStudyId(student.getStudy().getId());

        studentRepository.deleteById(studentId);
    }
}
