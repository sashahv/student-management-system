package pl.dfjp.students.repository.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.study.TypeOfStudy;

@Repository
public interface TypeOfStudyRepository extends JpaRepository<TypeOfStudy, Long> {
    @Query("SELECT t FROM TypeOfStudy t JOIN Study s ON s.typeOfStudy = t WHERE s.id = ?1")
    TypeOfStudy findByStudyId(Long studentId);
}
