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
@Table(name = "study_faculty")
public class Faculty { // WYDZIAł
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faculty")
    private List<AverageGradeBySemester> averageGradeBySemesters;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faculty")
    private List<AverageGradeByAcademicYear> averageGradeByAcademicYears;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faculty")
    private List<Study> studies;
}