package cz.malickov.backend.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "identificator")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Identificator {

    @Id
    @JsonProperty("identificatorId")
    @Column(name = "identificator_id",nullable = false,columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identificatorId;

    @JsonProperty("identificator")
    @Column(name = "identificator")
    @NotBlank(message = "Identificator description is required")
    private String identificator;
}
