package pl.dfjp.students.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.dfjp.students.service.ViewService;

@Controller
@RequestMapping("/archiwum")
@Slf4j
public class ArchiveController {
    private final ViewService viewService;

    @Autowired
    public ArchiveController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping
    private String showArchivePage(Model model,
                                   String str,
                                   String poleSort,
                                   String kierSort,
                                   String rokZakonczenia,
                                   String keyword){
        viewService.showArchivePage(model, str, keyword, poleSort, kierSort, rokZakonczenia);
        return "archiwum";
    }

    @GetMapping("/stypendysta")
    private String showArchivedStudentInformationPage(Model model,
                                                      @RequestParam("id") Long id){
        viewService.showArchivedStudentInformationPage(model, id);
        return "archived-student";
    }
}
