package pl.dfjp.students.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Gender;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.AverageGradeByAcademicYear;
import pl.dfjp.students.entity.study.AverageGradeBySemester;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.address.CountryRepository;
import pl.dfjp.students.repository.address.current.PlaceOfLivingRepository;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.*;
import pl.dfjp.students.service.ArchivedStudentService;
import pl.dfjp.students.service.StudentService;
import pl.dfjp.students.service.ViewService;
import pl.dfjp.students.service.study.AverageGradeService;

import java.util.Comparator;
import java.util.List;


@Controller
@Slf4j
public class StudentController {
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final AverageGradeService averageGradeService;
    private final ArchivedStudentService archivedStudentService;
    private final FacultyRepository facultyRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final KindOfStudyRepository kindOfStudyRepository;
    private final TypeOfStudyRepository typeOfStudyRepository;
    private final CountryRepository countryRepository;
    private final ViewService viewService;
    private final PlaceOfLivingRepository placeOfLivingRepository;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;
    private final AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository;
    private final ArchivedStudentRepository archivedStudentRepository;

    @Autowired
    public StudentController(StudentService studentService,
                             StudentRepository studentRepository,
                             AverageGradeService averageGradeService,
                             ArchivedStudentService archivedStudentService,
                             FacultyRepository facultyRepository,
                             FieldOfStudyRepository fieldOfStudyRepository,
                             KindOfStudyRepository kindOfStudyRepository,
                             TypeOfStudyRepository typeOfStudyRepository,
                             CountryRepository countryRepository,
                             ViewService viewService,
                             PlaceOfLivingRepository placeOfLivingRepository,
                             AverageGradeBySemesterRepository averageGradeBySemesterRepository,
                             AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository,
                             ArchivedStudentRepository archivedStudentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.averageGradeService = averageGradeService;
        this.archivedStudentService = archivedStudentService;
        this.facultyRepository = facultyRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository;
        this.kindOfStudyRepository = kindOfStudyRepository;
        this.typeOfStudyRepository = typeOfStudyRepository;
        this.countryRepository = countryRepository;
        this.viewService = viewService;
        this.placeOfLivingRepository = placeOfLivingRepository;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
        this.averageGradeByAcademicYearRepository = averageGradeByAcademicYearRepository;
        this.archivedStudentRepository = archivedStudentRepository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> fetchStudentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(studentService.fetchStudentById(id));
    }

    @GetMapping("/stypendysci/stypendysta")
    private String showStudentInformationPage(Model model, @RequestParam("id") Long id) {
        viewService.showStudentInformationPage(model, id);
        return "student";
    }

    @RequestMapping(value = "/stypendysci", method = {RequestMethod.GET, RequestMethod.POST})
    private String showStudentsPage(Model model,
                                    String str,
                                    String poleSort,
                                    String kierSort,
                                    String keyword) {
        viewService.showStudentsPage(model, str, keyword, poleSort, kierSort);
        return "stypendysci";
    }

    @GetMapping("/stypendysci/zaswiadczenie")
    private String showDocumentPage(Model model, Long studentId) {
        viewService.showDocumentPage(model, studentId);
        return "zaswiadczenia";
    }


    @GetMapping("/dodawanie-stypendysty")
    private String showAddStudentPage(Model model) {
        viewService.showAddStudentPage(model);
        return "dodawanie-stypendysty";
    }

    @PostMapping("/dodawanie-stypendysty")
    private String addStudent(@Valid @ModelAttribute Student student,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("student", student);
            model.addAttribute("genders", List.of(Gender.values()));
            model.addAttribute("placesOfLiving", placeOfLivingRepository.findAll());
            model.addAttribute("countries", countryRepository.findAll());
            model.addAttribute("faculties", facultyRepository.findAll());
            model.addAttribute("fields", fieldOfStudyRepository.findAll());
            model.addAttribute("kinds", kindOfStudyRepository.findAll());
            model.addAttribute("types", typeOfStudyRepository.findAll());
            return "dodawanie-stypendysty";
        }

        redirectAttributes.addFlashAttribute("message", "Student pomyślnie dodany");
        studentService.addStudent(student);
        return "redirect:/dodawanie-stypendysty";
    }

    @GetMapping("/stypendysci/edytowanie")
    private String showUpdateStudentPage(Model model,
                                         @RequestParam("studentId") Long id) {
        viewService.showEditStudentPage(model, id);
        return "edytowanie-stypendysty";
    }

    @PostMapping("/stypendysci/edytowanie")
    private String updateStudent(@Valid @ModelAttribute Student student,
                                 @RequestParam(value = "ocena", required = false) List<Double> averageGradeBySemesterList,
                                 @RequestParam Long id,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            Long studyId = student != null ? student.getStudy().getId() : null;
            int customDecreasingAmount = student != null ? student.getScholarship().getCustomDecreasingAmount() : 0;
            List<AverageGradeBySemester> avgGrades = averageGradeBySemesterRepository.findByStudyId(studyId);
            List<AverageGradeByAcademicYear> avgGradesByYear = averageGradeByAcademicYearRepository.findAllByStudyId(studyId);
            avgGradesByYear.sort(Comparator.comparing(AverageGradeByAcademicYear::getAverageGrade).reversed());
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
            return "edytowanie-stypendysty";
        }

        redirectAttributes.addFlashAttribute("message", "Zmiany zostały zapisane");
        studentService.updateStudent(student, averageGradeBySemesterList, id);
        return "redirect:/stypendysci/stypendysta?id=" + id;
    }

    @GetMapping("/stypendysci/archiwizacja")
    private String archiveStudent(@RequestParam("studentId") Long id,
                                  RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + id + " doesn't exist")
        );
        redirectAttributes.addFlashAttribute("message",
                student.getName().charAt(0) + ". " + student.getSurname() + " dodany do archiwum");
        archivedStudentService.archiveStudent(id);
        return "redirect:/stypendysci";
    }

    @PostMapping("/stypendysta/zwieksz-semestr")
    private String generateAverageGradeForActualSemester(@RequestParam Long studentId,
                                                         @RequestParam("ocena") double grade,
                                                         RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );
        if (student.getStudy().getActualSemester() == 10) {
            averageGradeService.generateAverageGradeForActualSemester(studentId, grade);
            redirectAttributes.addFlashAttribute("message", "Semestr pomyślnie zwiększony\nStudent dodany do archiwum");
            return "redirect:/archiwum/stypendysta?id=" + archivedStudentRepository.findLastIndex();
        } else {
            averageGradeService.generateAverageGradeForActualSemester(studentId, grade);
            redirectAttributes.addFlashAttribute("message", "Semestr pomyślnie zwiększony");
            return "redirect:/stypendysci/stypendysta?id=" + studentId;
        }
    }
}
