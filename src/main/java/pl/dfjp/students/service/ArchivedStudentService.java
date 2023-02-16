package pl.dfjp.students.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.address.current.CurrentAddressRepository;
import pl.dfjp.students.repository.scholarship.ScholarshipRepository;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.AttachmentRepository;
import pl.dfjp.students.repository.student.StudentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ArchivedStudentService {
    private final ArchivedStudentRepository archivedStudentRepository;
    private final StudentRepository studentRepository;
    private final AttachmentRepository attachmentRepository;
    private final CurrentAddressRepository currentAddressRepository;
    private final ScholarshipRepository scholarshipRepository;

    @Autowired
    public ArchivedStudentService(ArchivedStudentRepository archivedStudentRepository,
                                  StudentRepository studentRepository,
                                  AttachmentRepository attachmentRepository,
                                  CurrentAddressRepository currentAddressRepository,
                                  ScholarshipRepository scholarshipRepository){
        this.archivedStudentRepository = archivedStudentRepository;
        this.attachmentRepository = attachmentRepository;
        this.studentRepository = studentRepository;
        this.currentAddressRepository = currentAddressRepository;
        this.scholarshipRepository = scholarshipRepository;
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

        studentRepository.deleteStudentWithoutInformation(studentId);

        attachmentRepository.deleteAllByStudentId(studentId);
        currentAddressRepository.deleteCurrentAddressByStudentId(studentId);
        scholarshipRepository.deleteByStudentId(studentId);

    }
}
