package pl.dfjp.students.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import pl.dfjp.students.entity.student.Attachment;
import pl.dfjp.students.service.AttachmentService;
import pl.dfjp.students.service.ViewService;
import pl.dfjp.students.utils.FileUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/karty-ocen")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final ViewService viewService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService,
                                ViewService viewService) {
        this.attachmentService = attachmentService;
        this.viewService = viewService;
    }

    @PostMapping("/przeslanie")
    public String uploadFile(@RequestParam Long studentId,
                             @RequestParam("semestr") int semester,
                             @RequestParam("plik") MultipartFile file,
                             RedirectAttributes redirectAttributes) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(studentId, semester, file);
        redirectAttributes.addFlashAttribute("message", "Karta ocen pomyślnie dodana");
//        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/download?id=")
//                .path(attachment.getId()).toUriString();
        return "redirect:/karty-ocen?studentId=" + studentId;
    }

    @GetMapping("/karta")
    public ResponseEntity<ByteArrayResource> watchFile(@RequestParam String id) {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(new ByteArrayResource(FileUtils.decompressFile(attachment.getData())));
    }

    @GetMapping("/pobieranie")
    public ResponseEntity<Resource> downloadFile(@RequestParam String id){
        Attachment attachment = attachmentService.getAttachment(id);
        String encodedFileName = UriUtils.encode(attachment.getFileName(), StandardCharsets.UTF_8.name());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(new ByteArrayResource(FileUtils.decompressFile(attachment.getData())));
    }

    @GetMapping("/usuwanie")
    public String deleteFile(@RequestParam String id,
                             RedirectAttributes redirectAttributes){
        Attachment attachment = attachmentService.deleteAttachment(id);
        redirectAttributes.addFlashAttribute("message", "Karta ocen została usunięta");
        return "redirect:/karty-ocen?studentId=" + attachment.getStudent().getId();
    }

    @GetMapping
    public String showPdfPage(Long studentId,
                              Model model){
        viewService.showPdfPage(model, studentId);
        return "karty-ocen";
    }
}
