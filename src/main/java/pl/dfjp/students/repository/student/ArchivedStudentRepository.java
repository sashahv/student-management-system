package pl.dfjp.students.repository.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivedStudentRepository extends JpaRepository<ArchivedStudent, Long> {
    @Query("SELECT s " +
            "FROM ArchivedStudent s " +
            "WHERE s.name LIKE %?1%" +
            "OR s.surname LIKE %?1%" +
            "OR CONCAT(s.name, ' ',s.surname) LIKE %?1%" +
            "OR CONCAT(s.surname, ' ', s.name) LIKE %?1%")
    List<ArchivedStudent> findAll(String keyword);

    @Query("SELECT s " +
            "FROM ArchivedStudent s " +
            "WHERE s.yearOfGraduation=?1")
    Page<ArchivedStudent> findAllByYearOfGraduation(int yearOfGraduation, Pageable pageable);

    @Query("SELECT DISTINCT s.yearOfGraduation " +
            "FROM ArchivedStudent s")
    List<Integer> listAllYears();

    Optional<ArchivedStudent> findBySID(String sid);

    @Query("SELECT s.id FROM ArchivedStudent s WHERE s.SID=?1")
    String findIndexBySID(String sid);
}
