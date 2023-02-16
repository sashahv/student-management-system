package pl.dfjp.students.entity.address.permanent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PermanentAddress {
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

    @JsonIgnore
    @OneToOne(mappedBy = "permanentAddress")
    private Student student;

    @JsonIgnore
    @OneToOne(mappedBy = "permanentAddress")
    private ArchivedStudent archivedStudent;
}
