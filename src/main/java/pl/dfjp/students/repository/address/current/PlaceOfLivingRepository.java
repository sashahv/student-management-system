package pl.dfjp.students.repository.address.current;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceOfLivingRepository extends JpaRepository<PlaceOfLiving, Long> {
    @Query("SELECT student " +
            "FROM Student student " +
            "JOIN student.currentAddress.placeOfLiving placeOfLiving " +
            "WHERE placeOfLiving.roomSize = ?1 AND placeOfLiving.name = ?2")
    Optional<List<Student>> findAllStudentsByRoomSizeAndPlaceName(int roomSize, String placeName);

    Optional<PlaceOfLiving> findPlaceOfLivingByNameAndRoomSize(String name, int roomSize);
}
