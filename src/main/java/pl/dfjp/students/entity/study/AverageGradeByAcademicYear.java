package pl.dfjp.students.entity.study;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
@Table(name = "study_avg_year")
public class AverageGradeByAcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double averageGrade;
    private int academicYear;
    private String faculty;
    private String fieldOfStudy;
    private String kindOfStudy;
    private String typeOfStudy;
    @ManyToOne(cascade = CascadeType.ALL)
    private Study study;
}
