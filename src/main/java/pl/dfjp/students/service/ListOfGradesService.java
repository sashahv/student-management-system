package pl.dfjp.students.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.AverageGradeByAcademicYear;
import pl.dfjp.students.entity.study.AverageGradeBySemester;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeByAcademicYearRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;

import java.io.*;
import java.util.List;

@Service
public class ListOfGradesService {

    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;
    private final StudentRepository studentRepository;
    private final ArchivedStudentRepository archivedStudentRepository;
    private final AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository;

    @Autowired
    public ListOfGradesService(AverageGradeBySemesterRepository averageGradeBySemesterRepository,
                               StudentRepository studentRepository,
                               ArchivedStudentRepository archivedStudentRepository,
                               AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository) {
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
        this.studentRepository = studentRepository;
        this.archivedStudentRepository = archivedStudentRepository;
        this.averageGradeByAcademicYearRepository = averageGradeByAcademicYearRepository;
    }

    public void generateAutoGeneratedPDF(Long studentId,
                                         HttpServletResponse response) throws IOException, DocumentException {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student z indeksem " + studentId + " nie istnieje")
        );
        Long studyId = student.getStudy().getId();

        // Get the average grades for the student with the given ID
        List<AverageGradeBySemester> avgGrades = averageGradeBySemesterRepository.findByStudyId(studyId);

        // Create an iText Document and PdfWriter
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

        Font fontTitle = new Font(bfTimes, 11, Font.BOLD);
        Font fontContent = new Font(bfTimes, 11, Font.NORMAL);
        Font fontMainTitle = new Font(bfTimes, 16, Font.BOLD);

        Paragraph paragraph = new Paragraph(new Phrase("KARTA OCEN", fontMainTitle));
        paragraph.setSpacingAfter(5);
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase(student.getName() + " " + student.getSurname(), fontMainTitle));
        paragraph.setSpacingAfter(20);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        float[] widths = {10f, 10f, 5f, 5f, 3f, 3f};
        table.setWidths(widths);

        PdfPCell cell = new PdfPCell(new Phrase("Wydział", fontTitle));
        configureCell(table, cell);

        cell = new PdfPCell(new Phrase("Kierunek", fontTitle));
        configureCell(table, cell);

        cell = new PdfPCell(new Phrase("Rodzaj studiów", fontTitle));
        configureCell(table, cell);

        cell = new PdfPCell(new Phrase("Typ studiów", fontTitle));
        configureCell(table, cell);

        cell = new PdfPCell(new Phrase("Semestr", fontTitle));
        configureCell(table, cell);

        cell = new PdfPCell(new Phrase("Ocena", fontTitle));
        configureCell(table, cell);

        for (AverageGradeBySemester grade : avgGrades) {
            cell = new PdfPCell(new Phrase(grade.getFaculty().getName(), fontContent));
            configureCell(table, cell);
            cell = new PdfPCell(new Phrase(grade.getFieldOfStudy().getName(), fontContent));
            configureCell(table, cell);
            cell = new PdfPCell(new Phrase(grade.getKindOfStudy().getName(), fontContent));
            configureCell(table, cell);
            cell = new PdfPCell(new Phrase(grade.getTypeOfStudy().getName(), fontContent));
            configureCell(table, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getSemester()), fontContent));
            configureCell(table, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getAverageGrade()), fontContent));
            configureCell(table, cell);
        }

        document.add(table);
        document.close();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"" + student.getSurname() + "_" + student.getName() + "_karta_ocen.pdf\"";
        response.setHeader(headerKey, headerValue);
        OutputStream outputStream = response.getOutputStream();
        baos.writeTo(outputStream);
        outputStream.flush();
    }

    public MultipartFile generateMultipartFileWithGrades(Long studentId) throws DocumentException, IOException {
        ArchivedStudent archivedStudent = archivedStudentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student with id " + studentId + " doesn't exist")
        );
        Long studyId = archivedStudent.getStudy().getId();
        // Get the average grades for the student with the given ID
        List<AverageGradeBySemester> avgGrades = averageGradeBySemesterRepository.findByStudyId(studyId);
        // Create an iText Document and PdfWriter
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

        Font fontTitle = new Font(bfTimes, 11, Font.BOLD);
        Font fontContent = new Font(bfTimes, 11, Font.NORMAL);
        Font fontMainTitle = new Font(bfTimes, 16, Font.BOLD);

        Paragraph paragraph = new Paragraph(new Phrase("KARTA OCEN (studia w latach "
                + archivedStudent.getYearOfStarting() + "-" + archivedStudent.getYearOfGraduation() + " )", fontMainTitle));
        paragraph.setSpacingAfter(5);
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase(archivedStudent.getName() + " " + archivedStudent.getSurname(), fontMainTitle));
        paragraph.setSpacingAfter(20);
        document.add(paragraph);

        paragraph = new Paragraph(new Phrase("Semestralne oceny", fontTitle));
        paragraph.setSpacingAfter(20);
        document.add(paragraph);

        PdfPTable semesterGradesTable = new PdfPTable(6);
        semesterGradesTable.setWidthPercentage(100);
        float[] widths = {10f, 10f, 5f, 5f, 3f, 3f};
        semesterGradesTable.setWidths(widths);

        PdfPCell cell = new PdfPCell(new Phrase("Wydział", fontTitle));
        configureCell(semesterGradesTable, cell);

        cell = new PdfPCell(new Phrase("Kierunek", fontTitle));
        configureCell(semesterGradesTable, cell);

        cell = new PdfPCell(new Phrase("Rodzaj studiów", fontTitle));
        configureCell(semesterGradesTable, cell);

        cell = new PdfPCell(new Phrase("Typ studiów", fontTitle));
        configureCell(semesterGradesTable, cell);

        cell = new PdfPCell(new Phrase("Semestr", fontTitle));
        configureCell(semesterGradesTable, cell);

        cell = new PdfPCell(new Phrase("Ocena", fontTitle));
        configureCell(semesterGradesTable, cell);

        for (AverageGradeBySemester grade : avgGrades) {
            cell = new PdfPCell(new Phrase(grade.getFaculty().getName(), fontContent));
            configureCell(semesterGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getFieldOfStudy().getName(), fontContent));
            configureCell(semesterGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getKindOfStudy().getName(), fontContent));
            configureCell(semesterGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getTypeOfStudy().getName(), fontContent));
            configureCell(semesterGradesTable, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getSemester()), fontContent));
            configureCell(semesterGradesTable, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getAverageGrade()), fontContent));
            configureCell(semesterGradesTable, cell);
        }

        document.add(semesterGradesTable);

        List<AverageGradeByAcademicYear> avgGradesByYear = averageGradeByAcademicYearRepository.findAllByStudyId(studyId);

        paragraph = new Paragraph(new Phrase("Roczne oceny", fontTitle));
        paragraph.setSpacingBefore(20);
        paragraph.setSpacingAfter(20);
        document.add(paragraph);

        PdfPTable yearGradesTable = new PdfPTable(6);
        yearGradesTable.setWidthPercentage(100);
        yearGradesTable.setWidths(widths);

        cell = new PdfPCell(new Phrase("Wydział", fontTitle));
        configureCell(yearGradesTable, cell);

        cell = new PdfPCell(new Phrase("Kierunek", fontTitle));
        configureCell(yearGradesTable, cell);

        cell = new PdfPCell(new Phrase("Rodzaj studiów", fontTitle));
        configureCell(yearGradesTable, cell);

        cell = new PdfPCell(new Phrase("Typ studiów", fontTitle));
        configureCell(yearGradesTable, cell);

        cell = new PdfPCell(new Phrase("Rok", fontTitle));
        configureCell(yearGradesTable, cell);

        cell = new PdfPCell(new Phrase("Ocena", fontTitle));
        configureCell(yearGradesTable, cell);

        for (AverageGradeByAcademicYear grade : avgGradesByYear) {
            cell = new PdfPCell(new Phrase(grade.getFaculty().getName(), fontContent));
            configureCell(yearGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getFieldOfStudy().getName(), fontContent));
            configureCell(yearGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getKindOfStudy().getName(), fontContent));
            configureCell(yearGradesTable, cell);
            cell = new PdfPCell(new Phrase(grade.getTypeOfStudy().getName(), fontContent));
            configureCell(yearGradesTable, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getAcademicYear()), fontContent));
            configureCell(yearGradesTable, cell);
            cell = new PdfPCell(new Phrase(String.valueOf(grade.getAverageGrade()), fontContent));
            configureCell(yearGradesTable, cell);
        }

        if(!avgGradesByYear.isEmpty()){
            document.add(yearGradesTable);
        }
        document.close();
        byte[] pdfBytes = baos.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(pdfBytes);

        return new MockMultipartFile("file", "karta_ocen.pdf",
                "application/pdf", inputStream);
    }

    private void configureCell(PdfPTable pdfPTable, PdfPCell cell) {
        cell.setMinimumHeight(20);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingBottom(5);
        pdfPTable.addCell(cell);
    }
}
