package pl.dfjp.students.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.entity.student.Gender;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.Faculty;
import pl.dfjp.students.entity.study.FieldOfStudy;
import pl.dfjp.students.entity.study.KindOfStudy;
import pl.dfjp.students.entity.study.TypeOfStudy;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.service.ReportService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Controller
@Slf4j
public class ReportController {
    private final ReportService reportService;
    private final StudentRepository studentRepository;

    @Autowired
    public ReportController(ReportService reportService, StudentRepository studentRepository) {
        this.reportService = reportService;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/raport/lista-studentow.pdf")
    public ResponseEntity<InputStreamResource> filterStudentsAndGeneratePdf(@RequestParam(value = "miejsceZam", required = false) PlaceOfLiving placeOfLiving,
                                                                            @RequestParam(value = "plec", required = false) Gender gender,
                                                                            @RequestParam(value = "rodzajSt", required = false) KindOfStudy kindOfStudy,
                                                                            @RequestParam(value = "wydzial", required = false) Faculty faculty,
                                                                            @RequestParam(value = "kierunek", required = false) FieldOfStudy fieldOfStudy,
                                                                            @RequestParam(value = "typSt", required = false) TypeOfStudy typeOfStudy,
                                                                            @RequestParam(value = "krajUrodzenia", required = false) Country countryOfBirth,
                                                                            @RequestParam(value = "rokSt", required = false) Integer yearOfStudy,
                                                                            @RequestParam(value = "ilPustychKolumn", required = false) Integer amountOfEmptyColumns,
                                                                            @RequestParam(value = "ilPustychWierszy", required = false) Integer amountOfEmptyRows,
                                                                            @RequestParam(value = "ilKolumn", required = false) Integer amountOfColumns,
                                                                            @RequestParam(value = "ilWierszy", required = false) Integer amountOfRows,
                                                                            @RequestParam(value = "tabId", required = false) Long tableId,
                                                                            @RequestParam(value = "tytuly", required = false) String titles,
                                                                            @RequestParam(value = "tytulDokumentu", required = false) String mainTitle,
                                                                            @RequestParam(value = "typListy", required = false) String typListy) throws IOException {
        List<Student> students;

        String titlesEdited = titles.substring(1);

        if(placeOfLiving!=null
              || gender!=null
              || faculty!=null
              || kindOfStudy!=null
              || fieldOfStudy!=null
              || typeOfStudy!=null
              || countryOfBirth!=null
              || yearOfStudy!=null)
        {
            students = reportService.filterStudents(
                    placeOfLiving,
                    gender,
                    kindOfStudy,
                    faculty,
                    fieldOfStudy,
                    typeOfStudy,
                    countryOfBirth,
                    yearOfStudy
            );
        } else {
            students = studentRepository.findAll();
            students.sort(Comparator.comparing(student -> student.getSurname().toUpperCase(Locale.ROOT)));
        }

        ByteArrayInputStream bis = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=Lista stypendystów.pdf");

        bis = switch (tableId.intValue()) {
            case 1 -> typListy.equals("numerowana")
                    ? reportService.generateEmptyColumnsWithNumerationList(titlesEdited, amountOfColumns, amountOfRows, mainTitle)
                    : reportService.generateEmptyColumnsList(titlesEdited, amountOfColumns, amountOfRows, mainTitle);
            case 2 -> reportService.generateNameSurnameList(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 3 -> reportService.generateNameSurnameGenderDatePlaceCountryOfBirthList(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 4 -> reportService.generateNameSurnameGenderPermanentAddressCityCountryAddressList(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 5 -> reportService.generateNameSurnameGenderCurrentAddressDateCityCountryAddressList(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 6 -> reportService.generateNameSurnameAmountOfScholarshipList(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 7 -> reportService.generateNameSurnamePreviousSemesterAverageGrade(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 8 -> reportService.generateListOfGrades(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            case 9 -> reportService.generateNameSurnameFieldYearKindOfStudy(students, amountOfEmptyColumns, amountOfEmptyRows, titles, mainTitle);
            default -> bis;
        };

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(Objects.requireNonNull(bis)));
    }
}