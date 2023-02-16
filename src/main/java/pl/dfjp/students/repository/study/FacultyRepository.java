package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.Faculty;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT f FROM Faculty f JOIN Study s ON s.faculty = f WHERE s.id = ?1")
    Faculty findByStudyId(Long studentId);

    Optional<Faculty> findByName(String nazwa);
}
