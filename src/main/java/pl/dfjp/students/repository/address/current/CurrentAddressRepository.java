package pl.dfjp.students.repository.address.current;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.student.Student;

import java.util.List;

@Repository
public interface CurrentAddressRepository extends JpaRepository<CurrentAddress, Long> {
    List<CurrentAddress> findByCountryId(Long id);

    List<CurrentAddress> findByPlaceOfLivingId(Long id);

    @Modifying
    @Query(value = "DELETE FROM current_address c WHERE c.id IN " +
            "(SELECT current_address_id FROM student s WHERE s.id=?1)", nativeQuery = true)
    void deleteCurrentAddressByStudentId(Long studentId);

}
