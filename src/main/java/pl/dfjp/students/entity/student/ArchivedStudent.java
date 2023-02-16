package pl.dfjp.students.entity.student;

import jakarta.persistence.*;
import lombok.*;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;
import pl.dfjp.students.entity.study.Study;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ArchivedStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String fatherName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String passportNumber;
    private String phoneNumber;
    private LocalDate birthDate;
    private String placeOfBirth;
    @ManyToOne
    @JoinColumn(name = "country_of_birth_id", referencedColumnName = "id")
    private Country countryOfBirth;
    private String citizenship;
    private String nationality;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permanent_address_id", referencedColumnName = "id")
    private PermanentAddress permanentAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    private Study study;

    private Integer yearOfStarting;
    private Integer yearOfGraduation;
    private String additionalInformation;
}
