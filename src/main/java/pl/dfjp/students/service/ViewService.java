package pl.dfjp.students.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.entity.scholarship.Scholarship;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Attachment;
import pl.dfjp.students.entity.student.Gender;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.*;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.address.CountryRepository;
import pl.dfjp.students.repository.address.current.PlaceOfLivingRepository;
import pl.dfjp.students.repository.scholarship.ScholarshipRepository;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.AttachmentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeByAcademicYearRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;
import pl.dfjp.students.service.study.StudyService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ViewService {
    private final StudyService studyService;
    private final CountryRepository countryRepository;
    private final StudentRepository studentRepository;
    private final PlaceOfLivingRepository placeOfLivingRepository;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;
    private final AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final StudentService studentService;
    private final ArchivedStudentService archivedStudentService;
    private final ArchivedStudentRepository archivedStudentRepository;
    private final AttachmentRepository attachmentRepository;

    public ViewService(StudyService studyService,
                       CountryRepository countryRepository,
                       StudentRepository studentRepository,
                       PlaceOfLivingRepository placeOfLivingRepository,
                       AverageGradeBySemesterRepository averageGradeBySemesterRepository,
                       AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository,
                       ScholarshipRepository scholarshipRepository,
                       StudentService studentService,
                       ArchivedStudentService archivedStudentService,
                       ArchivedStudentRepository archivedStudentRepository,
                       AttachmentRepository attachmentRepository) {
        this.studyService = studyService;
        this.countryRepository = countryRepository;
        this.studentRepository = studentRepository;
        this.placeOfLivingRepository = placeOfLivingRepository;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
        this.averageGradeByAcademicYearRepository = averageGradeByAcademicYearRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.studentService = studentService;
        this.archivedStudentService = archivedStudentService;
        this.archivedStudentRepository = archivedStudentRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public void showAddStudentPage(Model model) {
        Student student = new Student();
        List<Scholarship> allScholarships = scholarshipRepository.findAll();
        if(!allScholarships.isEmpty()) {
            model.addAttribute("actualScholarship", allScholarships.get(0).getActualAmount());
        } else {
            model.addAttribute("actualScholarship", 0);
        }

        model.addAttribute("student", student);
        model.addAttribute("genders", List.of(Gender.values()));
        model.addAttribute("placesOfLiving", placeOfLivingRepository.findAll());
        model.addAttribute("countries", countryRepository.findAll());
        studyService.addStudyObjects(model);
    }

    public void showChangeSemesterPage(Model model) {
        model.addAttribute("students", studentRepository.findAll());

        List<Student> students = studentRepository.findAll();
        Map<Student, Float> studentGradeMap = new HashMap<>();
        for (Student student : students) {
            averageGradeBySemesterRepository
                    .findBySemesterAndStudyId(student.getStudy().getActualSemester(),
                            student.getStudy().getId())
                    .ifPresent(avgGrade -> studentGradeMap.put(student, (float) avgGrade.getAverageGrade()));
        }
        model.addAttribute("studentGradeMap", studentGradeMap);
    }

    public void showReportPage(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("placesOfLiving", placeOfLivingRepository.findAll());
        model.addAttribute("genders", List.of(Gender.values()));
        model.addAttribute("countries", countryRepository.findAll());
        studyService.addStudyObjects(model);
    }

    public void showAdministrationPanelPage(Model model) {
        List<PlaceOfLiving> placesOfLiving = placeOfLivingRepository.findAll();
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("placesOfLiving", placesOfLiving);
        model.addAttribute("countries", countryRepository.findAll());
        List<Scholarship> allScholarships = scholarshipRepository.findAll();
        if(!allScholarships.isEmpty()) {
            model.addAttribute("actualScholarship", allScholarships.get(0).getActualAmount());
        } else {
            model.addAttribute("actualScholarship", 0);
        }
        model.addAttribute("faculty", new Faculty());
        model.addAttribute("fieldOfStudy", new FieldOfStudy());
        model.addAttribute("kindOfStudy", new KindOfStudy());
        model.addAttribute("typeOfStudy", new KindOfStudy());
        model.addAttribute("country", new Country());
        model.addAttribute("placeOfLiving", new PlaceOfLiving());
        studyService.addStudyObjects(model);
    }

    public void showEditStudentPage(Model model,
                                    Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );
        Long studyId = student != null ? student.getStudy().getId() : null;
        int customDecreasingAmount = student != null ? student.getScholarship().getCustomDecreasingAmount() : 0;
        List<AverageGradeBySemester> avgGrades = averageGradeBySemesterRepository.findByStudyId(studyId);
        avgGrades.sort(Comparator.comparing(AverageGradeBySemester::getSemester).reversed());
        List<AverageGradeByAcademicYear> avgGradesByYear = averageGradeByAcademicYearRepository.findAllByStudyId(studyId);
        avgGradesByYear.sort(Comparator.comparing(AverageGradeByAcademicYear::getAcademicYear).reversed());
        long countAvgGradesByYear = avgGradesByYear.size();
        long countAvgGrades = avgGrades.size();
        model.addAttribute("student", student);
        model.addAttribute("customDecreasingAmount", customDecreasingAmount);
        model.addAttribute("genders", List.of(Gender.values()));
        model.addAttribute("placesOfLiving", placeOfLivingRepository.findAll());
        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("countAvgGradesByYear", countAvgGradesByYear);
        model.addAttribute("countAvgGrades", countAvgGrades);
        model.addAttribute("averageGrades", avgGrades);
        model.addAttribute("averageGradesByYear", avgGradesByYear);
        studyService.addStudyObjects(model);
    }

    public void showStudentInformationPage(Model model,
                                           Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );
        Long studyId = student.getStudy().getId();
        List<AverageGradeBySemester> avgGrades = averageGradeBySemesterRepository.findByStudyId(studyId);
        avgGrades.sort(Comparator.comparing(AverageGradeBySemester::getSemester).reversed());
        List<AverageGradeByAcademicYear> avgGradesByYear = averageGradeByAcademicYearRepository.findAllByStudyId(studyId);
        avgGradesByYear.sort(Comparator.comparing(AverageGradeByAcademicYear::getAcademicYear).reversed());
        long countAvgGradesByYear = avgGradesByYear.size();
        long countAvgGrades = avgGrades.size();
        model.addAttribute("student", student);
        model.addAttribute("countAvgGradesByYear", countAvgGradesByYear);
        model.addAttribute("countAvgGrades", countAvgGrades);
        model.addAttribute("averageGrades", avgGrades);
        model.addAttribute("averageGradesByYear", avgGradesByYear);
        studyService.addStudyObjects(model);
    }

    public void showPdfPage(Model model,
                            Long studentId){
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );
        List<Attachment> attachments = attachmentRepository.findAllByStudentId(studentId);
        attachments.sort(Comparator.comparing(Attachment::getFileName));
        long countAttachments = attachments.size();
        model.addAttribute("attachments", attachments);
        model.addAttribute("student", student);
        model.addAttribute("countAttachments", countAttachments);
    }

    public void showStudentsPage(Model model,
                                 String pageNumber,
                                 String keyword,
                                 String sortField,
                                 String sortDirection) {
        int pageSize = 200;
        if (pageNumber == null) {
            pageNumber = "1";
        }
        int formattedPageNumber = Integer.parseInt(pageNumber);
        if (sortDirection == null) {
            sortDirection = "asc";
        }
        if (sortField == null) {
            sortField = "surname";
        }
        List<Student> studentsFiltered = studentService.listAll(keyword);
        Page<Student> page = paginateStudentsPage(formattedPageNumber, pageSize, sortField, sortDirection);
        List<Student> studentsPaged = page.getContent();
        model.addAttribute("currentPage", formattedPageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("students", studentsPaged);
        model.addAttribute("studentsFiltered", studentsFiltered);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
    }

    public Page<Student> paginateStudentsPage(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return studentRepository.findAll(pageable);
    }

    public void showArchivedStudentInformationPage(Model model, Long studentId){
        ArchivedStudent student = archivedStudentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist in archive")
        );
        Attachment attachment = attachmentRepository.findByArchivedStudentId(studentId);
        LocalDate now = LocalDate.now(ZoneId.of("Europe/Warsaw"));

        model.addAttribute("student", student);
        model.addAttribute("now", now);
        model.addAttribute("attachment", attachment);
        studyService.addStudyObjects(model);
    }

    public void showArchivePage(Model model,
                                String pageNumber,
                                String keyword,
                                String sortField,
                                String sortDirection,
                                String yearOfGraduation) {

        int pageSize = 100;
        if (pageNumber == null) {
            pageNumber = "1";
        }
        int formattedPageNumber = Integer.parseInt(pageNumber);
        if (sortDirection == null) {
            sortDirection = "desc";
        }
        if (sortField == null) {
            sortField = "id";
        }
        List<ArchivedStudent> archivedStudentsFiltered = archivedStudentService.listAll(keyword);
        Page<ArchivedStudent> page = showPaginatedArchivePage(formattedPageNumber, pageSize, sortField, sortDirection);
        Page<ArchivedStudent> archivedStudentsPage = archivedStudentService.listAllByYearOfGraduation(yearOfGraduation, page);
        List<ArchivedStudent> studentsByYearOfGraduation = archivedStudentsPage.getContent();
        int totalAmountOfStudentsByYear = archivedStudentsPage.getContent().size();
        List<Integer> sortedAvailableYears = archivedStudentRepository
                .listAllYears().stream()
                .sorted(Comparator.reverseOrder())
                .filter(integer -> integer>0)
                .collect(Collectors.toList());
        model.addAttribute("currentPage", formattedPageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("totalAmountOfStudentsByYear", totalAmountOfStudentsByYear);
        model.addAttribute("yearOfGraduation", yearOfGraduation);
        model.addAttribute("availableYears", sortedAvailableYears);
        model.addAttribute("studentsFiltered", archivedStudentsFiltered);
        model.addAttribute("studentsByYearOfGraduation", studentsByYearOfGraduation);
        model.addAttribute("countArchiveStudents", (long) studentsByYearOfGraduation.size());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
    }

    public Page<ArchivedStudent> showPaginatedArchivePage(int pageNumber, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return archivedStudentRepository.findAll(pageable);
    }

    public void showDocumentPage(Model model, Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );

        LocalDate now = LocalDate.now(ZoneId.of("Europe/Warsaw"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("pl", "PL"));
        String formattedDate = now.format(formatter);
        model.addAttribute("student", student);
        model.addAttribute("currentDate", formattedDate);
    }
}
