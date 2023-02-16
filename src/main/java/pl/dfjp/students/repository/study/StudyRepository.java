package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.Study;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    List<Study> findByFacultyId(Long id);
    List<Study> findByFieldOfStudyId(Long id);
    List<Study> findByKindOfStudyId(Long id);
    List<Study> findByTypeOfStudyId(Long id);

    Study findByStudent(Student student);
}
