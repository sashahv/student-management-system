package pl.dfjp.students.entity.study;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
@Table(name = "study_avg_semester")
public class AverageGradeBySemester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double averageGrade;
    private int semester;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private Faculty faculty; // WYDZIAł
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "field_of_study_id", referencedColumnName = "id")
    private FieldOfStudy fieldOfStudy; // KIERUNEK
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "kind_of_study_id", referencedColumnName = "id")
    private KindOfStudy kindOfStudy; // RODZAJ STUDIÓW
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "type_of_study_id", referencedColumnName = "id")
    private TypeOfStudy typeOfStudy; // TYP STUDIÓW

    @ManyToOne(cascade = CascadeType.ALL)
    private Study study;
}
