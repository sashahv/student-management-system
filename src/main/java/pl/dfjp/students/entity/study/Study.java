package pl.dfjp.students.entity.study;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "study_faculty_id", referencedColumnName = "id")
    private Faculty faculty; // WYDZIAł
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "study_field_id", referencedColumnName = "id")
    private FieldOfStudy fieldOfStudy; // KIERUNEK
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "study_kind_id", referencedColumnName = "id")
    private KindOfStudy kindOfStudy; // RODZAJ STUDIÓW
    @NotNull
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "study_type_id", referencedColumnName = "id")
    private TypeOfStudy typeOfStudy; // TYP STUDIÓW
    @NotNull(message = "Musi być większym od 0")
    @Min(value=1)
    private Integer yearOfStudy;
    @NotNull(message = "Musi być większym od 0")
    @Min(value=1)
    private Integer actualSemester;

    private Integer fromYear;
    private Integer toYear;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;
}
