package pl.dfjp.students.entity.study;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "study_field")
public class FieldOfStudy {  // KIERUNEK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fieldOfStudy")
    private List<AverageGradeBySemester> averageGradeBySemesters;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fieldOfStudy")
    private List<AverageGradeByAcademicYear> averageGradeByAcademicYears;
}