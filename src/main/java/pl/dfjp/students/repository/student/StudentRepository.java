package pl.dfjp.students.repository.student;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.student.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Modifying
    @Query(value = "DELETE FROM student s WHERE s.id=?1 ", nativeQuery = true)
    void deleteStudentWithoutInformation(Long studentId);

    @Query("SELECT s " +
            "FROM Student s " +
            "WHERE s.name LIKE %?1%" +
            "OR s.surname LIKE %?1%" +
            "OR CONCAT(s.name, ' ',s.surname) LIKE %?1%" +
            "OR CONCAT(s.surname, ' ', s.name) LIKE %?1%")
    List<Student> findAll(String keyword);

    List<Student> findByCountryOfBirthId(Long id);

    Student findByStudyId(Long id);

    List<Student> findAll(Specification<Student> specification);

//    @Query("SELECT s FROM Student  s JOIN CurrentAddress a ON a=s.currentAddress WHERE a.placeOfLiving.id=?1")
//    List<Student> findByPlaceOfLivingId(Long placeOfLivingId);
//
//    List<Student> findByGender(String gender);

//    Object findByKindOfStudyId(Long kindOfStudyId);
}
