package pl.dfjp.students.service.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.dfjp.students.entity.study.*;
import pl.dfjp.students.repository.study.*;

import java.util.List;

@Service
@Slf4j
public class StudyService {
    private final FacultyRepository facultyRepository;
    private final KindOfStudyRepository kindOfStudyRepository;
    private final TypeOfStudyRepository typeOfStudyRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final StudyRepository studyRepository;

    @Autowired
    public StudyService(StudyRepository studyRepository,
                        FacultyRepository facultyRepository,
                        KindOfStudyRepository kindOfStudyRepository,
                        TypeOfStudyRepository typeOfStudyRepository,
                        FieldOfStudyRepository fieldOfStudyRepository) {
        this.facultyRepository = facultyRepository;
        this.kindOfStudyRepository = kindOfStudyRepository;
        this.typeOfStudyRepository = typeOfStudyRepository;
        this.fieldOfStudyRepository = fieldOfStudyRepository;
        this.studyRepository = studyRepository;
    }

    public void addNewFaculty(String name) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        List<Study> studies = studyRepository.findByFacultyId(id);
        if (faculty != null) {
            if (studies.isEmpty()) {
                facultyRepository.deleteById(id);
            } else {
                throw new RuntimeException("Wydział studiów stypendysty musi być zamienione przez usuwaniem");
            }
        }
    }

    public void addNewFieldOfStudy(String name) {
        FieldOfStudy fieldOfStudy = new FieldOfStudy();
        fieldOfStudy.setName(name);
        fieldOfStudyRepository.save(fieldOfStudy);
    }

    public void deleteFieldOfStudy(Long id) {
        FieldOfStudy fieldOfStudy = fieldOfStudyRepository.findById(id).orElse(null);
        List<Study> studies = studyRepository.findByFieldOfStudyId(id);
        if (fieldOfStudy != null) {
            if (studies.isEmpty()) {
                fieldOfStudyRepository.deleteById(id);
            } else {
                throw new RuntimeException("Kierunek studiów stypendysty musi być zamienione przez usuwaniem");
            }
        }
    }

    public void addNewKindOfStudy(String name) {
        KindOfStudy kindOfStudy = new KindOfStudy();
        kindOfStudy.setName(name);
        kindOfStudyRepository.save(kindOfStudy);
    }

    public void deleteKindOfStudy(Long id) {
        KindOfStudy kindOfStudy = kindOfStudyRepository.findById(id).orElse(null);
        List<Study> studies = studyRepository.findByKindOfStudyId(id);
        if (kindOfStudy != null) {
            if (studies.isEmpty()) {
                kindOfStudyRepository.deleteById(id);
            } else {
                throw new RuntimeException("Rodzaj studiów stypendysty musi być zamienione przez usuwaniem");
            }
        }
}

    public void addNewTypeOfStudy(String name) {
        TypeOfStudy typeOfStudy = new TypeOfStudy();
        typeOfStudy.setName(name);
        typeOfStudyRepository.save(typeOfStudy);
    }

    public void deleteTypeOfStudy(Long id) {
        TypeOfStudy typeOfStudy = typeOfStudyRepository.findById(id).orElse(null);
        List<Study> studies = studyRepository.findByTypeOfStudyId(id);
        if (typeOfStudy != null) {
            if (studies.isEmpty()) {
                typeOfStudyRepository.deleteById(id);
            } else {
                throw new RuntimeException("Typ studiów stypendysty musi być zamieniony przez usuwaniem");
            }
        }
    }

    public void addStudyObjects(Model model) {
        model.addAttribute("faculties", facultyRepository.findAll());
        model.addAttribute("fields", fieldOfStudyRepository.findAll());
        model.addAttribute("kinds", kindOfStudyRepository.findAll());
        model.addAttribute("types", typeOfStudyRepository.findAll());
    }
}
