package pl.dfjp.students.entity.address.current;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "current_address_place_of_living")
public class PlaceOfLiving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Nazwa miejsca zamieszkania nie może być pusta")
    private String name;
    @NotNull(message = "Liczba osób w pokoju nie może być pustą")
    private Integer roomSize; // 1os, 2os, 3os
    private Integer decreaseScholarship;
}