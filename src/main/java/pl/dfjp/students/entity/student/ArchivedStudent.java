package pl.dfjp.students.entity.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.dfjp.students.entity.study.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "archive")
public class ArchivedStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String SID;

    private String name;
    private String surname;
    private String fatherName;
    private String gender;
    private String phoneNumber;
    private String birthDate;
    private String placeOfBirth;
    private String countryOfBirth;
    private String citizenship;
    private String nationality;

    private String permAdrCountry;
    private String permAdrCity;
    private String permAdrStreet;
    private String permAdrZipCode;

    @Column(name = "study_finished_year")
    private Integer finishedYearOfStudy;
    @Column(name = "study_finished_semester")
    private Integer finishedSemester;
    @Column(name = "study_faculty")
    private String faculty; // WYDZIAł
    @Column(name = "study_field")
    private String fieldOfStudy; // KIERUNEK
    @Column(name = "study_kind")
    private String kindOfStudy; // RODZAJ STUDIÓW
    @Column(name = "study_type")
    private String typeOfStudy; // TYP STUDIÓW

    private Integer yearOfStarting;
    private Integer yearOfGraduation;
    private String additionalInformation;

    @Transient
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    private Study study;
}
