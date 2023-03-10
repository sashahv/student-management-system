package pl.dfjp.students.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;
import pl.dfjp.students.entity.scholarship.Scholarship;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.Study;
import pl.dfjp.students.repository.address.CountryRepository;
import pl.dfjp.students.repository.address.current.CurrentAddressRepository;
import pl.dfjp.students.repository.address.permanent.PermanentAddressRepository;
import pl.dfjp.students.repository.scholarship.ScholarshipRepository;
import pl.dfjp.students.repository.study.StudyRepository;

@Service
@Slf4j
public class GeneratorService {
    private final PermanentAddressRepository permanentAddressRepository;
    private final CurrentAddressRepository currentAddressRepository;
    private final StudyRepository studyRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final ScholarshipService scholarshipService;
    private final CountryRepository countryRepository;

    @Autowired
    public GeneratorService(PermanentAddressRepository permanentAddressRepository,
                            CurrentAddressRepository currentAddressRepository,
                            StudyRepository studyRepository,
                            ScholarshipRepository scholarshipRepository,
                            ScholarshipService scholarshipService,
                            CountryRepository countryRepository) {
        this.currentAddressRepository = currentAddressRepository;
        this.studyRepository = studyRepository;
        this.permanentAddressRepository = permanentAddressRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.scholarshipService = scholarshipService;
        this.countryRepository = countryRepository;
    }

//    public String generatePhoneNumber(String phoneNumber) {
//        String clearPhoneNumber = phoneNumber.replaceAll(" ", "");
//        int length = clearPhoneNumber.length();
//        return "+48 " + clearPhoneNumber.substring(length - 9, length - 6) + " "
//                + clearPhoneNumber.substring(length - 6, length - 3) + " "
//                + clearPhoneNumber.substring(length - 3, length);
//    }

    public String generateZipCode(String zipCode) {
        String clearZipCode = zipCode.replaceAll(" ", "");
        if(zipCode.length()==5){
            return clearZipCode.substring(0, 2) + "-" + clearZipCode.substring(2, 5);
        } else {
            return clearZipCode;
        }
    }

    public CurrentAddress generateCurrentAddress(Student student, CurrentAddress currentAddress, Student givenStudent) {
        CurrentAddress generatedCurrentAddress;

        if (student.getCurrentAddress()==null) {
            generatedCurrentAddress = new CurrentAddress();
        } else {
            generatedCurrentAddress = student.getCurrentAddress();
        }

        generatedCurrentAddress.setStreet(currentAddress.getStreet().isBlank() ? "Boczna Lubomelskiej" : currentAddress.getStreet());
        generatedCurrentAddress.setHouseNumber(currentAddress.getHouseNumber().isBlank() ? "2" : currentAddress.getHouseNumber());
        generatedCurrentAddress.setCity(currentAddress.getCity().isBlank() ? "Lublin" : currentAddress.getCity());
        generatedCurrentAddress.setZipCode(generateZipCode(currentAddress.getZipCode().isBlank() ? "20-070" : currentAddress.getZipCode()));
        generatedCurrentAddress.setCountry(currentAddress.getCountry()==null ? countryRepository.findByName("Polska") : currentAddress.getCountry());
        generatedCurrentAddress.setPlaceOfLiving(currentAddress.getPlaceOfLiving());
        generatedCurrentAddress.setStudent(student);
        currentAddressRepository.save(generatedCurrentAddress);
        return generatedCurrentAddress;
    }

    public PermanentAddress generatePermanentAddress(Student student, PermanentAddress permanentAddress) {
        PermanentAddress generatedPermanentAddress;

        if (student.getPermanentAddress()==null) {
            generatedPermanentAddress = new PermanentAddress();
        } else {
            generatedPermanentAddress = student.getPermanentAddress();
        }
        generatedPermanentAddress.setStreet(permanentAddress.getStreet());
        generatedPermanentAddress.setHouseNumber(permanentAddress.getHouseNumber());
        generatedPermanentAddress.setCity(permanentAddress.getCity());
        generatedPermanentAddress.setZipCode(generateZipCode(permanentAddress.getZipCode()));
        generatedPermanentAddress.setCountry(permanentAddress.getCountry());
        generatedPermanentAddress.setStudent(student);
        permanentAddressRepository.save(generatedPermanentAddress);
        return generatedPermanentAddress;
    }

    public Study generateStudy(Student student, Study study) {
        Study generatedStudy;

        if (student.getStudy()==null) {
            generatedStudy = new Study();
        } else {
            generatedStudy = student.getStudy();
        }
        generatedStudy.setFaculty(study.getFaculty());
        generatedStudy.setFieldOfStudy(study.getFieldOfStudy());
        generatedStudy.setKindOfStudy(study.getKindOfStudy());
        generatedStudy.setFromYear(study.getFromYear());
        generatedStudy.setToYear(study.getToYear());
        generatedStudy.setTypeOfStudy(study.getTypeOfStudy());
        generatedStudy.setYearOfStudy(study.getYearOfStudy());
        generatedStudy.setActualSemester(study.getActualSemester());
        generatedStudy.setStudent(student);
        studyRepository.save(generatedStudy);
        return generatedStudy;
    }

    public Scholarship generateScholarship(Student student, Scholarship scholarship) {
        Scholarship generatedScholarship;

        if(student.getScholarship()==null){
            generatedScholarship = new Scholarship();
        } else {
            generatedScholarship=student.getScholarship();
        }

        int decreasingAmount = student.getCurrentAddress().getPlaceOfLiving().getDecreaseScholarship();

        generatedScholarship.setActualAmount(student.getScholarship()==null
                ? scholarshipService.getActualScholarship()
                : scholarship.getActualAmount());
        generatedScholarship.setDecreasingAmount(decreasingAmount);
        generatedScholarship.setCustomDecreasingAmount(scholarship.getCustomDecreasingAmount());
        generatedScholarship.setTotalAmount();
        generatedScholarship.setStudent(student);
        scholarshipRepository.save(generatedScholarship);
        return generatedScholarship;
    }
}