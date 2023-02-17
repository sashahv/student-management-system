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
@Table(name = "study_type")
public class TypeOfStudy {  // TYP STUDIÓW
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typeOfStudy")
    private List<AverageGradeBySemester> averageGradeBySemesters;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typeOfStudy")
    private List<AverageGradeByAcademicYear> averageGradeByAcademicYears;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typeOfStudy")
    private List<Study> studies;
}