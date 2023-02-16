package pl.dfjp.students.entity.study;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
@Table(name = "study_kind")
public class KindOfStudy { // RODZAJ STUDIÓW
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotBlank(message = "Nazwa nie może być pusta")
    private String name;

    @Transient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kindOfStudy")
    private List<Study> studies;
}