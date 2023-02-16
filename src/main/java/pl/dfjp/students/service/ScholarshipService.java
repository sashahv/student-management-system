package pl.dfjp.students.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.entity.scholarship.Scholarship;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.exception.PlaceOfLivingNotFoundException;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.address.current.PlaceOfLivingRepository;
import pl.dfjp.students.repository.scholarship.ScholarshipRepository;
import pl.dfjp.students.repository.student.StudentRepository;

import java.util.List;

@Service
public class ScholarshipService {
    private final ScholarshipRepository scholarshipRepository;
    private final StudentRepository studentRepository;
    private final PlaceOfLivingRepository placeOfLivingRepository;

    @Autowired
    public ScholarshipService(ScholarshipRepository scholarshipRepository,
                              StudentRepository studentRepository,
                              PlaceOfLivingRepository placeOfLivingRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.studentRepository = studentRepository;
        this.placeOfLivingRepository = placeOfLivingRepository;
    }

    public int getActualScholarship(){
        List<Scholarship> scholarships = scholarshipRepository.findAll();
        int actualAmount=0;
        for (Scholarship scholarship : scholarships) {
            int amount = scholarship.getActualAmount();
            if(amount > actualAmount) actualAmount = amount;
        }
        return actualAmount;
    }

    public void changeActualAmountOfScholarship(int newAmountOfScholarship) {
        Iterable<Scholarship> scholarships = scholarshipRepository.findAll();
        for (Scholarship scholarship : scholarships) {
            scholarship.setActualAmount(newAmountOfScholarship);
            scholarship.setTotalAmount();
            scholarshipRepository.save(scholarship);
        }
    }

//    USE THIS METHOD IN CASE IF AMOUNT OF SCHOLARSHIP WILL BE DIFFERENT IN EACH STUDENT

//    private void checkIfOldAmountEqualsToNewAmount(int oldAmountOfScholarship, int newAmountOfScholarship, Scholarship scholarship) {
//        if (scholarship.getActualAmount() != oldAmountOfScholarship) {
//            if (newAmountOfScholarship > oldAmountOfScholarship) {
//                int differenceBetweenAmounts = newAmountOfScholarship - oldAmountOfScholarship;
//                scholarship.setActualAmount(scholarship.getActualAmount() + differenceBetweenAmounts);
//            } else {
//                int differenceBetweenAmounts = oldAmountOfScholarship - newAmountOfScholarship;
//                scholarship.setActualAmount(scholarship.getActualAmount() - differenceBetweenAmounts);
//            }
//        }
//    }

    public void changeDecreasingAmountDependingOnRoomSize(String name,
                                                          int roomSize,
                                                          int newAmount) {
        PlaceOfLiving placeOfLiving = placeOfLivingRepository
                .findPlaceOfLivingByNameAndRoomSize(name, roomSize).orElseThrow(
                        () -> new PlaceOfLivingNotFoundException(String.format(
                                "Nie znaleziono pokoju w %s z %s miejscami w pokoju",
                                name,
                                roomSize
                        ))
                );


        placeOfLiving.setDecreaseScholarship(newAmount);
        placeOfLivingRepository.save(placeOfLiving);
        changeDecreasingAmountForStudents(name, roomSize, placeOfLiving);
    }

    private void changeDecreasingAmountForStudents(String name,
                                                   int roomSize,
                                                   PlaceOfLiving placeOfLiving) {
        List<Student> students = placeOfLivingRepository.findAllStudentsByRoomSizeAndPlaceName(roomSize, name).orElseThrow(
                () -> new StudentNotFoundException(String.format("Nie znaleziono studentów mieszkających w pokoju %d-x", roomSize))
        );

        students.forEach(student -> {
            student.getScholarship().setDecreasingAmount(placeOfLiving.getDecreaseScholarship());
            student.getScholarship().setTotalAmount();
        });

        studentRepository.saveAll(students);
    }
}
