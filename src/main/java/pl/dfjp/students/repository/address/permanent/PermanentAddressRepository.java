package pl.dfjp.students.repository.address.permanent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;

import java.util.List;

@Repository
public interface PermanentAddressRepository extends JpaRepository<PermanentAddress, Long> {
    List<PermanentAddress> findByCountryId(Long id);
}