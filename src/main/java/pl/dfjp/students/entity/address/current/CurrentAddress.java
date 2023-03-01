package pl.dfjp.students.entity.address.current;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String zipCode;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Country country;
    @Valid
    @NotNull(message = "Miejsce zamieszkania nie może być puste")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "place_of_living_id", referencedColumnName = "id")
    private PlaceOfLiving placeOfLiving;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;
}
