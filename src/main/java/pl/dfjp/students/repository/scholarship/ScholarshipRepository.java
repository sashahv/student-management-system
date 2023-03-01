package pl.dfjp.students.repository.scholarship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.scholarship.Scholarship;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    void deleteByStudentId(Long studentId);
}
