package cz.malickov.backend.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "identificator")
@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class Identificator {

    @Id
    @JsonProperty("identificatorId")
    @Column(name = "identificator_id",columnDefinition = "uniqueidentifier")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identificatorId;

    @JsonProperty("identificatorName")
    @Column(name = "identificator_name")
    private String identificatorName;
}
