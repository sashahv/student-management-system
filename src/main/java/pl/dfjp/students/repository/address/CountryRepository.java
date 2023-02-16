package pl.dfjp.students.repository.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
