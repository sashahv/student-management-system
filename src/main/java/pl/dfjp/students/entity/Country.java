package pl.dfjp.students.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.dfjp.students.entity.address.current.CurrentAddress;
import pl.dfjp.students.entity.address.permanent.PermanentAddress;
import pl.dfjp.students.entity.student.ArchivedStudent;
import pl.dfjp.students.entity.student.Student;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Nazwa państwa nie może być pusty")
    private String name;

    @Transient
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
    List<PermanentAddress> permanentAddresses;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country")
    List<CurrentAddress> currentAddresses;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryOfBirth")
    List<ArchivedStudent> archivedStudents;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "countryOfBirth")
    List<Student> students;
}
