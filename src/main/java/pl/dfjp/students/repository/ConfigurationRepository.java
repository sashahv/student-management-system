package pl.dfjp.students.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.config.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
}
