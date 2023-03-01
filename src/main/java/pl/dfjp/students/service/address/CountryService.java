package pl.dfjp.students.service.address;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.repository.address.CountryRepository;
import pl.dfjp.students.repository.address.current.CurrentAddressRepository;
import pl.dfjp.students.repository.address.permanent.PermanentAddressRepository;
import pl.dfjp.students.repository.student.ArchivedStudentRepository;
import pl.dfjp.students.repository.student.StudentRepository;

import java.util.List;

@Service
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;
    private final PermanentAddressRepository permanentAddressRepository;
    private final CurrentAddressRepository currentAddressRepository;
    private final StudentRepository studentRepository;
    private final ArchivedStudentRepository archivedStudentRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository,
                          PermanentAddressRepository permanentAddressRepository,
                          CurrentAddressRepository currentAddressRepository,
                          StudentRepository studentRepository,
                          ArchivedStudentRepository archivedStudentRepository) {
        this.countryRepository = countryRepository;
        this.permanentAddressRepository = permanentAddressRepository;
        this.currentAddressRepository = currentAddressRepository;
        this.studentRepository = studentRepository;
        this.archivedStudentRepository = archivedStudentRepository;
    }

    public void addNewCountry(String name){
        Country country = new Country();
        country.setName(name);
        countryRepository.save(country);
    }

    public void deleteCountry(Long id){
        Country country = countryRepository.findById(id).orElse(null);
        List<Student> students = studentRepository.findByCountryOfBirthId(id);
        List<PermanentAddress> permanentAddresses = permanentAddressRepository.findByCountryId(id);
        List<CurrentAddress> currentAddresses = currentAddressRepository.findByCountryId(id);
        if(country!=null){
            currentAddresses.forEach(currentAddress -> {
                currentAddress.setCountry(null);
                currentAddressRepository.save(currentAddress);
            });
            permanentAddresses.forEach(permanentAddress -> {
                permanentAddress.setCountry(null);
                permanentAddressRepository.save(permanentAddress);
            });
            students.forEach(student -> {
                student.setCountryOfBirth(null);
                studentRepository.save(student);
            });
                countryRepository.deleteById(id);
        }
    }
}
