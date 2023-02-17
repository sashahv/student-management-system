package pl.dfjp.students.entity.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;
import pl.dfjp.students.entity.scholarship.Scholarship;
import pl.dfjp.students.entity.study.Study;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Imię nie może być pusty")
    private String name;
    @NotBlank(message = "Nazwisko nie może być pusty")
    private String surname;
    private String fatherName;
    @NotNull(message = "Plec nie może być pusta")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotBlank(message = "Numer paszportu nie może być pusty")
    private String passportNumber;
    @Pattern(regexp = "^(\\+48)? [0-9]{9}$|^(\\+48)? [0-9]{3} [0-9]{3} [0-9]{3}$|^(\\+48)?[0-9]{9}$",
    message = "Nieprawidłowy format | Przykłady prawidłowego formatu: +48 123 456 789, +48123456789, 123 456 789, 123456789")
    private String phoneNumber;
    @NotNull(message = "Pole jest wymagane")
    @Past(message = "Nie może być w przyszłości")
    private LocalDate birthDate;
    private String placeOfBirth;
    @NotNull(message = "Kraj urodzenia nie może być pusty")
    @ManyToOne
    @JoinColumn(name = "country_of_birth_id", referencedColumnName = "id")
    private Country countryOfBirth;
    private String citizenship;
    private String nationality;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permanent_address_id", referencedColumnName = "id")
    private PermanentAddress permanentAddress;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_address_id", referencedColumnName = "id")
    private CurrentAddress currentAddress;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "study_id", referencedColumnName = "id")
    private Study study;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scholarship_id", referencedColumnName = "id")
    private Scholarship scholarship;

    private String additionalInformation;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "student")
    List<Attachment> attachments;
}
