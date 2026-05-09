package cz.malickov.backend.dto;

import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.enums.Department;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;


public record ChildOutboundDTO (
    @NotNull
    UUID childUuid,
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    Department department,
    Date birthDay,
    @NotNull
    Boolean active,
    @NotNull
    String notes,
    @NotNull
    UUID userUuid,
    Identificator identificator,
    Boolean mon,
    Boolean tue,
    Boolean wed,
    Boolean thu,
    Boolean fri
){}
