package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.AverageGradeBySemester;

import java.util.List;
import java.util.Optional;

@Repository
public interface AverageGradeBySemesterRepository extends JpaRepository<AverageGradeBySemester, Long> {

    @Query("SELECT max(semester) FROM AverageGradeBySemester WHERE study.id = ?1")
    Integer findBiggestSemesterByStudyId(Long studyId);

    @Query("SELECT min(semester) FROM AverageGradeBySemester WHERE study.id = ?1")
    Integer findFirstSemesterByStudyId(Long studyId);

    List<AverageGradeBySemester> findByStudyId(Long studyId);

    Optional<AverageGradeBySemester> findBySemesterAndStudyId(int semester, Long studyId);

    void deleteByStudyId(Long id);
}
