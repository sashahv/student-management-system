package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.KindOfStudy;

@Repository
public interface KindOfStudyRepository extends JpaRepository<KindOfStudy, Long> {
    @Query("SELECT k FROM KindOfStudy k JOIN Study s ON s.kindOfStudy = k WHERE s.id = ?1")
    KindOfStudy findByStudyId(Long studentId);
}
