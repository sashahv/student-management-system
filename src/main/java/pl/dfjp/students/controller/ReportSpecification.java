package pl.dfjp.students.controller;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.Country;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.current.PlaceOfLiving;
import pl.dfjp.students.entity.student.Gender;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.*;
import pl.dfjp.students.repository.student.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ReportSpecification {

    public final StudentRepository studentRepository;

    @Autowired
    public ReportSpecification(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public static Specification<Student> getSpec(PlaceOfLiving placeOfLiving,
                                          Gender gender,
                                          KindOfStudy kindOfStudy,
                                          Faculty faculty,
                                          FieldOfStudy fieldOfStudy,
                                          TypeOfStudy typeOfStudy,
                                          Integer yearOfStudy,
                                          Country countryOfBirth) {
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Student, CurrentAddress> joinCurrentAddress = root.join("currentAddress", JoinType.RIGHT);
            Join<Student, Study> joinStudy = root.join("study", JoinType.LEFT);


            if (Objects.nonNull(placeOfLiving)) {
                predicates.add(
                        criteriaBuilder.equal(joinCurrentAddress.get("placeOfLiving"), placeOfLiving)
                );
            }
            if (Objects.nonNull(gender)) {
                predicates.add(
                        criteriaBuilder.equal(root.get("gender"), gender)
                );
            }
            if (Objects.nonNull(kindOfStudy)) {
                predicates.add(
                        criteriaBuilder.equal(joinStudy.get("kindOfStudy"), kindOfStudy)
                );
            }
            if (Objects.nonNull(typeOfStudy)) {
                predicates.add(
                        criteriaBuilder.equal(joinStudy.get("typeOfStudy"), typeOfStudy)
                );
            }
            if (Objects.nonNull(countryOfBirth)) {
                predicates.add(
                        criteriaBuilder.equal(root.get("countryOfBirth"), countryOfBirth)
                );
            }
            if (Objects.nonNull(faculty)) {
                predicates.add(
                        criteriaBuilder.equal(joinStudy.get("faculty"), faculty)
                );
            }
            if (Objects.nonNull(fieldOfStudy)) {
                predicates.add(
                        criteriaBuilder.equal(joinStudy.get("fieldOfStudy"), fieldOfStudy)
                );
            }
            if (Objects.nonNull(yearOfStudy) && yearOfStudy>0) {
                predicates.add(
                        criteriaBuilder.equal(joinStudy.get("yearOfStudy"), yearOfStudy)
                );
            }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }));
    }
}
