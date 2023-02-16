package pl.dfjp.students.entity.address.current;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.student.Student;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CurrentAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String houseNumber;
    private String city;
    @Pattern(regexp = "^$|^[0-9]{2}[- ][0-9]{3}|[0-9]{5}$",
            message = "Nieprawidłowy format | Przykłady prawidłowego formatu: 12-345, 12345, 12 345")
    private String zipCode;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Country country;
    private Integer startYear;
    private Integer finishYear;
    @Valid
    @NotNull(message = "Miejsce zamieszkania nie może być puste")
    @ManyToOne
    @JoinColumn(name = "place_of_living_id", referencedColumnName = "id")
    private PlaceOfLiving placeOfLiving;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private Student student;
}
