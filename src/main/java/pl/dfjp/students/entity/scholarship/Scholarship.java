package pl.dfjp.students.entity.scholarship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.dfjp.students.entity.student.Student;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Scholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Pole jest wymagane")
    private LocalDate dateOfGetting;
    @NotNull(message = "Pole jest wymagane")
    private LocalDate dateOfTermination; // Data ustania;
    @Min(value = 0, message = "Wysokość stypendium nię może być mniejszą od 0")
    private int actualAmount;
    @Min(value = 0, message = "Wysokość stypendium nię może być mniejszą od 0")
    private int decreasingAmount;
    @Min(value = 0, message = "Wysokość stypendium nię może być mniejszą od 0")
    private int customDecreasingAmount;
    private int totalAmount;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "scholarship")
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    public void setTotalAmount() {
        this.totalAmount = actualAmount-decreasingAmount-customDecreasingAmount;
    }
}