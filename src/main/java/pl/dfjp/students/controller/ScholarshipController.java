package pl.dfjp.students.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.dfjp.students.service.ScholarshipService;

@Controller
@Slf4j
@RequestMapping("/stypendium")
public class ScholarshipController {
    private final ScholarshipService scholarshipService;

    @Autowired
    public ScholarshipController(ScholarshipService scholarshipService) {
        this.scholarshipService = scholarshipService;
    }

    @PostMapping("/zmiana-wysokosci")
    private String changeActualAmountOfScholarship(@Valid @RequestParam("nowaWysokosc") int newAmount,
                                                   RedirectAttributes redirectAttributes){
        scholarshipService.changeActualAmountOfScholarship(newAmount);
        redirectAttributes.addFlashAttribute("message", "Zmiany zostały zapisane");
        return "redirect:/panel-administracyjny";
    }

    @PostMapping("/zmiana-odliczenia")
    private String changeDecreasingAmountDependingOnRoomSize(@RequestParam("nazwa") String name,
                                                             @RequestParam("ilOsob") int roomSize,
                                                             @RequestParam("nowaWysokosc") int newAmount,
                                                             RedirectAttributes redirectAttributes){
        scholarshipService.changeDecreasingAmountDependingOnRoomSize(name, roomSize, newAmount);
        redirectAttributes.addFlashAttribute("message", "Zmiany zostały zapisane");
        return "redirect:/panel-administracyjny";
    }
}
