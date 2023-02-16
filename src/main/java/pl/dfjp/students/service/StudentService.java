package pl.dfjp.students.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.student.*;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.student.AttachmentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;
import pl.dfjp.students.service.study.AverageGradeService;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class StudentService {
    private final GeneratorService generatorService;
    private final StudentRepository studentRepository;
    private final AverageGradeService averageGradeService;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          GeneratorService generatorService,
                          AverageGradeService averageGradeService,
                          AverageGradeBySemesterRepository averageGradeBySemesterRepository) {
        this.studentRepository = studentRepository;
        this.generatorService = generatorService;
        this.averageGradeService = averageGradeService;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
    }

    public Student fetchStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(String.format("Student z indeksem [%d] nie został znaleziony", id))
        );
    }

    public List<Student> listAll(String keyword) {
        if (keyword != null) {
            return studentRepository.findAll(keyword);
        }
        return studentRepository.findAll();
    }

    public void updateStudent(Student givenStudent, List<Double> averageGradeBySemesterList,
                              Long id) {
        Student studentById = studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(String.format("Student z indeksem stypendii [%d] nie znaleziony", id))
        );

        Integer biggestSemester = averageGradeBySemesterRepository.findBiggestSemesterByStudyId(studentById.getStudy().getId());
        Integer firstSemester = averageGradeBySemesterRepository.findFirstSemesterByStudyId(studentById.getStudy().getId());

        HashMap<Integer, Double> gradesMap = new HashMap<>();
        if(averageGradeBySemesterList!=null){
            for (int i = firstSemester; i <= biggestSemester; i++) {
                gradesMap.put(i, averageGradeBySemesterList.get(biggestSemester-i));
            }  // 1 -> 9 === 2 -> 8 === 3 -> 7 === 4 -> 6 === 5 -> 5 === 6 -> 4 === 7 -> 3 === 8 -> 2 === 9 -> 1 === 10 -> 0
        }   // 3 ->     4
        saveStudent(givenStudent, studentById, gradesMap);
    }

    public void addStudent(Student student) {
        Student newStudent = new Student();
        saveStudent(student, newStudent, new HashMap<>());
    }

    private void saveStudent(Student givenStudent,
                             Student requiredStudent,
                             HashMap<Integer, Double> mapping) {
        requiredStudent.setName(givenStudent.getName());
        requiredStudent.setSurname(givenStudent.getSurname());
        requiredStudent.setFatherName(givenStudent.getFatherName());
        requiredStudent.setGender(givenStudent.getGender());
        requiredStudent.setPassportNumber(givenStudent.getPassportNumber());
        requiredStudent.setPhoneNumber(generatorService.generatePhoneNumber(givenStudent.getPhoneNumber()));
        requiredStudent.setBirthDate(givenStudent.getBirthDate());
        requiredStudent.setPlaceOfBirth(givenStudent.getPlaceOfBirth());
        requiredStudent.setCountryOfBirth(givenStudent.getCountryOfBirth());
        requiredStudent.setCitizenship(givenStudent.getCitizenship());
        requiredStudent.setNationality(givenStudent.getNationality());
        requiredStudent.setAdditionalInformation(givenStudent.getAdditionalInformation());
        studentRepository.save(requiredStudent);
        requiredStudent.setPermanentAddress(
                generatorService.generatePermanentAddress(requiredStudent, givenStudent.getPermanentAddress())
        );
        requiredStudent.setCurrentAddress(
                generatorService.generateCurrentAddress(requiredStudent, givenStudent.getCurrentAddress(), givenStudent)
        );
        requiredStudent.setStudy(
                generatorService.generateStudy(requiredStudent, givenStudent.getStudy())
        );
        if(!mapping.isEmpty()){
            averageGradeService.setAllGradesForStudent(mapping, requiredStudent.getId());
        }
        requiredStudent.setScholarship(
                generatorService.generateScholarship(requiredStudent, givenStudent.getScholarship())
        );
        studentRepository.save(requiredStudent);
    }
}
