package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.AverageGradeByAcademicYear;
import pl.dfjp.students.entity.study.AverageGradeBySemester;

import java.util.List;
import java.util.Optional;

@Repository
public interface AverageGradeByAcademicYearRepository extends JpaRepository<AverageGradeByAcademicYear, Long> {


    @Query("SELECT a FROM AverageGradeByAcademicYear a JOIN Study s ON a.study = s WHERE s.id = ?1")
    List<AverageGradeByAcademicYear> findAllByStudyId(Long studyId);

    Optional<AverageGradeByAcademicYear> findByAcademicYearAndStudyId(int year, Long studyId);

    AverageGradeByAcademicYear findTopByStudyId(Long studyId);

    double findByAcademicYear(int year);
}
