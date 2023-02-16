package pl.dfjp.students.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.dfjp.students.service.AttachmentService;
import pl.dfjp.students.service.ViewService;
import pl.dfjp.students.service.address.CountryService;
import pl.dfjp.students.service.address.CurrentAddressService;
import pl.dfjp.students.service.study.AverageGradeService;
import pl.dfjp.students.service.study.StudyService;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("/panel-administracyjny")
public class AdministrationController {
    private final StudyService studyService;
    private final CountryService countryService;
    private final CurrentAddressService currentAddressService;
    private final ViewService viewService;
    private final AverageGradeService averageGradeService;
    private final AttachmentService attachmentService;

    @Autowired
    public AdministrationController(StudyService studyService,
                                    CountryService countryService,
                                    CurrentAddressService currentAddressService,
                                    ViewService viewService,
                                    AverageGradeService averageGradeService,
                                    AttachmentService attachmentService) {
        this.studyService = studyService;
        this.countryService = countryService;
        this.currentAddressService = currentAddressService;
        this.viewService = viewService;
        this.averageGradeService = averageGradeService;
        this.attachmentService = attachmentService;
    }

    @GetMapping("")
    private String showAdministrationPanelPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "administracja";
    }

    @GetMapping("/zwiekszenie-semestru")
    private String showChangeSemesterPage(Model model) {
        viewService.showChangeSemesterPage(model);
        return "zmiana-semestru";
    }

    @PostMapping("/zwiekszenie-semestru")
    public String setAverageGrades(@RequestParam("studentId") List<Long> studentIds,
                                   @RequestParam("ocena") List<Double> grades,
                                   @RequestParam("pliki") List<MultipartFile> files,
                                   RedirectAttributes redirectAttributes) throws Exception {
        HashMap<Long, Double> gradesMap = new HashMap<>();
        HashMap<Long, MultipartFile> filesMap = new HashMap<>();
        for (int i = 0; i < studentIds.size(); i++) {
            gradesMap.put(studentIds.get(i), grades.get(i));

            if (Objects.requireNonNull(files.get(i).getContentType()).equals("application/pdf")) {
                filesMap.put(studentIds.get(i), files.get(i));
            }
        }
        attachmentService.saveAttachmentsForAllStudents(filesMap);
        averageGradeService.setAverageGradesForAllStudents(gradesMap);
        redirectAttributes.addFlashAttribute("message", "Zmiany zostały zapisane");
        return "redirect:/panel-administracyjny";
    }

    @GetMapping("/wydzialy")
    private String showFacultiesPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-wydzialami";
    }

    @GetMapping("/wydzialy/dodawanie")
    private String addNewFaculty(@RequestParam("nazwa") String name,
                                 RedirectAttributes redirectAttributes) {
        studyService.addNewFaculty(name);
        redirectAttributes.addFlashAttribute("message", "Wydział pomyślnie dodany");
        return "redirect:/panel-administracyjny/wydzialy";
    }

    @DeleteMapping("/wydzialy/usuwanie")
    private String deleteFaculty(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        studyService.deleteFaculty(id);
        redirectAttributes.addFlashAttribute("message", "Wydział został usunięty");
        return "redirect:/panel-administracyjny/wydzialy";
    }

    @GetMapping("/kierunki")
    private String showFieldsOfStudyPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-kierunkami";
    }

    @PostMapping("/kierunki/dodawanie")
    private String addNewFieldOfStudy(@RequestParam("nazwa") String name,
                                      RedirectAttributes redirectAttributes) {
        studyService.addNewFieldOfStudy(name);
        redirectAttributes.addFlashAttribute("message", "Kierunek pomyślnie dodany");
        return "redirect:/panel-administracyjny/kierunki";
    }

    @GetMapping("/kierunki/usuwanie")
    private String deleteFieldOfStudy(@RequestParam("id") Long id,
                                      RedirectAttributes redirectAttributes) {
        studyService.deleteFieldOfStudy(id);
        redirectAttributes.addFlashAttribute("message", "Kierunek został usunięty");
        return "redirect:/panel-administracyjny/kierunki";
    }

    @GetMapping("/rodzaje-studiow")
    private String showKindsOfStudyPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-rodzajami-studiow";
    }


    @PostMapping("/rodzaje-studiow/dodawanie")
    private String addNewKindOfStudy(@RequestParam("nazwa") String name,
                                     RedirectAttributes redirectAttributes) {
        studyService.addNewKindOfStudy(name);
        redirectAttributes.addFlashAttribute("message", "Rodzaj studiów pomyślnie dodany");
        return "redirect:/panel-administracyjny/rodzaje-studiow";
    }

    @GetMapping("/rodzaje-studiow/usuwanie")
    private String deleteKindOfStudy(@RequestParam("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        studyService.deleteKindOfStudy(id);
        redirectAttributes.addFlashAttribute("message", "Rodzaj studiow został usunięty");
        return "redirect:/panel-administracyjny/rodzaje-studiow";
    }

    @GetMapping("/typy-studiow")
    private String showTypesOfStudyPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-typami-studiow";
    }


    @PostMapping("/typy-studiow/dodawanie")
    private String addNewTypeOfStudy(@RequestParam("nazwa") String name,
                                     RedirectAttributes redirectAttributes) {
        studyService.addNewTypeOfStudy(name);
        redirectAttributes.addFlashAttribute("message", "Typ studiów pomyślnie dodany");
        return "redirect:/panel-administracyjny/typy-studiow";
    }

    @GetMapping("/typy-studiow/usuwanie")
    private String deleteTypeOfStudy(@RequestParam("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        studyService.deleteTypeOfStudy(id);
        redirectAttributes.addFlashAttribute("message", "Typ studiow został usunięty");
        return "redirect:/panel-administracyjny/typy-studiow";
    }

    @GetMapping("/panstwa")
    private String showCountriesPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-panstwami";
    }

    @PostMapping("/panstwa/dodawanie")
    private String addNewCountry(@RequestParam("nazwa") String name,
                                 RedirectAttributes redirectAttributes) {
        countryService.addNewCountry(name);
        redirectAttributes.addFlashAttribute("message", "Państwo pomyślnie dodane");
        return "redirect:/panel-administracyjny/panstwa";
    }

    @GetMapping("/panstwa/usuwanie")
    private String deleteCountry(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        countryService.deleteCountry(id);
        redirectAttributes.addFlashAttribute("message", "Państwo zostało usunięte");
        return "redirect:/panel-administracyjny/panstwa";
    }

    @GetMapping("/miejsca-zamieszkania")
    private String showPlacesOfLivingPage(Model model) {
        viewService.showAdministrationPanelPage(model);
        return "zarzadzanie-miejscami-zamieszkania";
    }

    @PostMapping("/miejsca-zamieszkania/dodawanie")
    private String addNewPlaceOfLiving(@RequestParam("nazwa") String name,
                                       @RequestParam("ilOsob") int roomSize,
                                       @RequestParam("wysOdl") int decreaseScholarshipAmount,
                                       RedirectAttributes redirectAttributes) {
        currentAddressService.addNewPlaceOfLiving(name, roomSize, decreaseScholarshipAmount);
        redirectAttributes.addFlashAttribute("message", "Miejsce zamieszkania pomyślnie dodane");
        return "redirect:/panel-administracyjny/miejsca-zamieszkania";
    }

    @GetMapping("/miejsca-zamieszkania/usuwanie")
    private String deletePlaceOfLiving(@RequestParam("id") Long id,
                                       RedirectAttributes redirectAttributes) {
        currentAddressService.deletePlaceOfLiving(id);
        redirectAttributes.addFlashAttribute("message", "Miejsce zamieszkania zostało usunięte");
        return "redirect:/panel-administracyjny/miejsca-zamieszkania";
    }
}
