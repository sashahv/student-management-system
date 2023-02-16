package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.FieldOfStudy;
import pl.dfjp.students.entity.study.KindOfStudy;

import java.util.List;

@Repository
public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
    @Query("SELECT f FROM FieldOfStudy f JOIN Study s ON s.fieldOfStudy = f WHERE s.id = ?1")
    FieldOfStudy findByStudyId(Long studentId);

//    @Query("SELECT f FROM FieldOfStudy f JOIN Faculty s ON s = f.faculty WHERE s.id = ?1")
//    FieldOfStudy findByFacultyId(Long id);
}
