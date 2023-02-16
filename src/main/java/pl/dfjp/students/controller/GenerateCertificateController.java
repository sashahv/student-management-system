package pl.dfjp.students.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
public class GenerateCertificateController {

    @PostMapping(value = "stypendysci/zaswiadczenia/zaswiadczenie.pdf")
    public ResponseEntity<InputStreamResource> generatePDF(@RequestParam("zaswiadczenie") String textareaContent) {

        ByteArrayInputStream bis = generatePDFContent(textareaContent);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=Zaświadczenie.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private ByteArrayInputStream generatePDFContent(String textareaContent) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
                // Add the text from the textarea to the PDF
            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
            Font times = new Font(bfTimes, 13, Font.NORMAL, Color.BLACK);
            com.lowagie.text.Paragraph paragraph = new com.lowagie.text.Paragraph(textareaContent, times);
            paragraph.setAlignment(com.lowagie.text.Element.ALIGN_JUSTIFIED);
            paragraph.setIndentationLeft(40);
            paragraph.setIndentationRight(40);
            document.add(paragraph);

            // Close the document
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}