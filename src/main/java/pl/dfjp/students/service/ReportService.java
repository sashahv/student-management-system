package pl.dfjp.students.service;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.dfjp.students.controller.ReportSpecification;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.entity.student.Gender;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.*;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class ReportService {
    private final StudentRepository studentRepository;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;

    @Autowired
    public ReportService(StudentRepository studentRepository,
                         AverageGradeBySemesterRepository averageGradeBySemesterRepository) {
        this.studentRepository = studentRepository;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
    }

    public ByteArrayInputStream generateEmptyColumnsWithNumerationList(String titles,
                                                                       Integer columns,
                                                                       Integer rows,
                                                                       String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            String replace = mainTitle.replace(",", "");

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPTable pdfPTable = configurePdfPTable(1, columns);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                    for (int i = 0; i < columns; i++) {
                        pdfPTable.addCell(" ");
                    }

                for (int i = 1; i <= rows; i++) {
                    cell = new PdfPCell(new Phrase(String.valueOf(i)));
                    configureCell(pdfPTable, cell);
                    for (int j = 0; j < columns; j++) {
                        pdfPTable.addCell(" ");
                    }
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < columns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 1; i <= rows; i++) {
                    cell = new PdfPCell(new Phrase(String.valueOf(i)));
                    configureCell(pdfPTable, cell);
                    for (int j = 0; j < columns; j++) {
                        pdfPTable.addCell(" ");
                    }
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateEmptyColumnsList(String titles,
                                                         Integer columns,
                                                         Integer rows,
                                                         String mainTitle) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configureEmptyTable(columns);

            String replace = mainTitle.replace(",", "");

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            if (titles.isEmpty()) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(" "));
                        configureCell(pdfPTable, cell);
                    }
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    log.info(title.trim());
                    PdfPCell cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < columns - titleList.length; i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        pdfPTable.addCell(" ");
                    }
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnameList(List<Student> students,
                                                        Integer amountOfEmptyColumns,
                                                        Integer amountOfEmptyRows,
                                                        String titles,
                                                        String mainTitle) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            String replace = mainTitle.replace(",", "");

            PdfPTable pdfPTable = configurePdfPTable(3, amountOfEmptyColumns);
            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);
            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            if (amountOfEmptyColumns > 6) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
                fontContent.setSize(10);
            }

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    pdfPTable.addCell("");
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);

                for (int j = 0; j < amountOfEmptyColumns + 2; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    public ByteArrayInputStream generateNameSurnameGenderDatePlaceCountryOfBirthList(List<Student> students,
                                                                                     Integer amountOfEmptyColumns,
                                                                                     Integer amountOfEmptyRows,
                                                                                     String titles,
                                                                                     String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(7, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }
            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Płeć", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Data urodzenia", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Kraj urodzenia", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Miejsce urodzenia", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {
                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getGender()!=null ? student.getGender().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                LocalDate localDate = student.getBirthDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("pl", "PL"));
                String formattedDate = localDate.format(formatter);
                cell = new PdfPCell(new Phrase(formattedDate, fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getCountryOfBirth()!=null ? student.getCountryOfBirth().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getPlaceOfBirth(), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    pdfPTable.addCell("");
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);

                for (int j = 0; j < amountOfEmptyColumns + 6; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnameGenderPermanentAddressCityCountryAddressList(List<Student> students,
                                                                                                Integer amountOfEmptyColumns,
                                                                                                Integer amountOfEmptyRows,
                                                                                                String titles,
                                                                                                String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(7, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }
            String replace = mainTitle.replace(",", "");
            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Płeć", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Miasto", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Kraj", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Adres", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getGender()!=null ? student.getGender().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getPermanentAddress()!=null ? student.getPermanentAddress().getCity() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getPermanentAddress().getCountry()!=null ? student.getPermanentAddress().getCountry().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getPermanentAddress().getStreet()
                        + " " + student.getPermanentAddress().getHouseNumber() + ", " + student.getPermanentAddress().getZipCode(), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    pdfPTable.addCell("");
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);


                for (int j = 0; j < amountOfEmptyColumns + 6; j++) {
                    pdfPTable.addCell(" ");
                }
            }


            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnameGenderCurrentAddressDateCityCountryAddressList(List<Student> students,
                                                                                                  Integer amountOfEmptyColumns,
                                                                                                  Integer amountOfEmptyRows,
                                                                                                  String titles,
                                                                                                  String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(8, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }

            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Płeć", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Data rozpoczęcia", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Miasto", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Kraj", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Adres", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getGender()!=null ? student.getGender().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                LocalDate localDate = student.getScholarship().getDateOfGetting();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("pl", "PL"));
                String formattedDate = localDate.format(formatter);
                cell = new PdfPCell(new Phrase(formattedDate, fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getCurrentAddress()!=null ? student.getCurrentAddress().getCity() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getCurrentAddress().getCountry()!=null ? student.getCurrentAddress().getCountry().getName() : " ", fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getCurrentAddress().getStreet()
                        + " " + student.getCurrentAddress().getHouseNumber() + ", " + student.getCurrentAddress().getZipCode(), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    pdfPTable.addCell("");
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);

                for (int j = 0; j < amountOfEmptyColumns + 7; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnameAmountOfScholarshipList(List<Student> students,
                                                                           Integer amountOfEmptyColumns,
                                                                           Integer amountOfEmptyRows,
                                                                           String titles,
                                                                           String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(4, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }
            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Wysokość stypendium", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(String.valueOf(student.getScholarship().getTotalAmount()), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    pdfPTable.addCell("");
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);


                for (int j = 0; j < amountOfEmptyColumns + 3; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnamePreviousSemesterAverageGrade(List<Student> students,
                                                                                Integer amountOfEmptyColumns,
                                                                                Integer amountOfEmptyRows,
                                                                                String titles,
                                                                                String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(5, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }
            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Semestr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Średnia ocena", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                int prevSemester = student.getStudy().getActualSemester() - 1;
                cell = new PdfPCell(new Phrase(String.valueOf(prevSemester>0 ? prevSemester : " "), fontContent));
                configureCell(pdfPTable, cell);

                Optional<AverageGradeBySemester> avgGradeBySemester = averageGradeBySemesterRepository.findBySemesterAndStudyId(
                        prevSemester, student.getStudy().getId());
                    cell = new PdfPCell(new Phrase(String.valueOf(avgGradeBySemester.isPresent() ? avgGradeBySemester.get().getAverageGrade() : " "), fontContent));
                    configureCell(pdfPTable, cell);
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);

                for (int j = 0; j < amountOfEmptyColumns + 4; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateListOfGrades(List<Student> students,
                                                     Integer amountOfEmptyColumns,
                                                     Integer amountOfEmptyRows,
                                                     String titles,
                                                     String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = new PdfPTable(13);
            float[] widths = {0.8f, 3f, 3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setWidths(widths);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            Font fontGradesTitle = new Font(bfTimes, 12, Font.BOLD, Color.BLACK);
            Font fontGradesContent = new Font(bfTimes, 11, Font.NORMAL, Color.BLACK);

            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Dane stypendysty", fontTitle));
            cell.setColspan(3);
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Semestralne oceny", fontTitle));
            cell.setColspan(10);
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("1", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("2", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("3", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("4", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("5", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("6", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("7", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("8", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("9", fontGradesTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("10", fontGradesTitle));
            configureCell(pdfPTable, cell);


            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                for (int i = 1; i <= 10; i++) {
                    Optional<AverageGradeBySemester> avgGradeBySemester = averageGradeBySemesterRepository.findBySemesterAndStudyId(i, student.getStudy().getId());
                    if (avgGradeBySemester.isPresent()) {
                        cell = new PdfPCell(new Phrase(String.valueOf(avgGradeBySemester.get().getAverageGrade()), fontGradesContent));
                        configureCell(pdfPTable, cell);
                    } else {
                        pdfPTable.addCell("");
                    }
                }
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);


                for (int j = 0; j < amountOfEmptyColumns + 3; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayInputStream generateNameSurnameFieldYearKindOfStudy(List<Student> students,
                                                                        Integer amountOfEmptyColumns,
                                                                        Integer amountOfEmptyRows,
                                                                        String titles,
                                                                        String mainTitle) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable pdfPTable = configurePdfPTable(6, amountOfEmptyColumns);

            BaseFont bfTimes = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1257, false);

            Font fontTitle = new Font(bfTimes, 13, Font.BOLD, Color.BLACK);
            Font fontContent = new Font(bfTimes, 12, Font.NORMAL, Color.BLACK);
            if (amountOfEmptyColumns > 2) {
                fontTitle.setSize(12);
                fontContent.setSize(10);
            }
            String replace = mainTitle.replace(",", "");

            Font mainTitleFont = new Font(bfTimes, 14, Font.BOLD, Color.BLACK);
            Paragraph headerTitle = new Paragraph(replace, mainTitleFont);
            headerTitle.setAlignment(Paragraph.ALIGN_CENTER);
            headerTitle.setSpacingAfter(20);
            document.add(headerTitle);

            PdfPCell cell = new PdfPCell(new Phrase("Nr", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Nazwisko", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Imię", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Kierunek", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Rok studiów", fontTitle));
            configureCell(pdfPTable, cell);

            cell = new PdfPCell(new Phrase("Rodzaj studiów", fontTitle));
            configureCell(pdfPTable, cell);

            if (titles.isEmpty()) {
                for (int i = 0; i < amountOfEmptyColumns; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            } else {
                String[] titleList = titles.split(",");

                for (String title : titleList) {
                    cell = new PdfPCell(new Phrase(title.trim(), fontTitle));
                    configureCell(pdfPTable, cell);
                }

                for (int i = 0; i < amountOfEmptyColumns - titleList.length; i++) {
                    cell = new PdfPCell(new Phrase(" "));
                    configureCell(pdfPTable, cell);
                }
            }

            for (Student student : students) {

                cell = new PdfPCell(new Phrase(String.valueOf(students.indexOf(student) + 1), fontTitle));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getSurname(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getStudy().getFaculty().getName(), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(String.valueOf(student.getStudy().getYearOfStudy()), fontContent));
                configureCell(pdfPTable, cell);

                cell = new PdfPCell(new Phrase(student.getStudy().getKindOfStudy().getName(), fontContent));
                configureCell(pdfPTable, cell);
            }

            for (int i = students.size()+1; i <= amountOfEmptyRows + students.size(); i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i), fontTitle));
                configureCell(pdfPTable, cell);

                for (int j = 0; j < amountOfEmptyRows; j++) {
                    pdfPTable.addCell(" ");
                }
            }

            document.add(pdfPTable);
            document.close();

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    public List<Student> filterStudents(PlaceOfLiving placeOfLiving,
                                        Gender gender,
                                        KindOfStudy kindOfStudy,
                                        Faculty faculty,
                                        FieldOfStudy fieldOfStudy,
                                        TypeOfStudy typeOfStudy,
                                        Country countryOfBirth,
                                        Integer yearOfStudy) {
        return getStudents(
                placeOfLiving,
                gender,
                kindOfStudy,
                faculty,
                fieldOfStudy,
                typeOfStudy,
                countryOfBirth,
                yearOfStudy);
    }


    private List<Student> getStudents(PlaceOfLiving placeOfLiving,
                                      Gender gender,
                                      KindOfStudy kindOfStudy,
                                      Faculty faculty,
                                      FieldOfStudy fieldOfStudy,
                                      TypeOfStudy typeOfStudy,
                                      Country countryOfBirth,
                                      Integer yearOfStudy) {
        Specification<Student> specification = ReportSpecification.getSpec(
                placeOfLiving,
                gender,
                kindOfStudy,
                faculty,
                fieldOfStudy,
                typeOfStudy,
                yearOfStudy,
                countryOfBirth
        );
        List<Student> students = studentRepository.findAll(specification);
        students.sort(Comparator.comparing(student -> student.getSurname().toUpperCase(Locale.ROOT)));
        return students;
    }

    private void configureCell(PdfPTable pdfPTable, PdfPCell cell) {
        cell.setMinimumHeight(25);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingBottom(5);
        pdfPTable.addCell(cell);
    }

    private PdfPTable configureEmptyTable(Integer amountOfAdditionalEmptyColumns) {
        PdfPTable pdfPTable = new PdfPTable(amountOfAdditionalEmptyColumns);
        pdfPTable.setWidthPercentage(100);
        return pdfPTable;
    }

    private PdfPTable configurePdfPTable(int amountOfColumns, Integer amountOfAdditionalEmptyColumns) {
        PdfPTable pdfPTable = new PdfPTable(amountOfColumns + amountOfAdditionalEmptyColumns);
        pdfPTable.setWidthPercentage(100);
        float totalWidth = 1; // Total width of the table
        int numberOfColumns = amountOfColumns + amountOfAdditionalEmptyColumns; // Number of columns in the table
        float firstCellWidth = totalWidth * 0.06f; // 10% of the total width for the first cell
        float remainingCellWidth = (totalWidth - firstCellWidth) / (numberOfColumns - 1);
        float[] columnWidths = new float[numberOfColumns];

        columnWidths[0] = firstCellWidth;
        for (int i = 1; i < numberOfColumns; i++) {
            columnWidths[i] = remainingCellWidth;
        }

        pdfPTable.setWidths(columnWidths);
        return pdfPTable;
    }
}
