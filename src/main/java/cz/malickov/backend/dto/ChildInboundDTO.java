package cz.malickov.backend.dto;

import cz.malickov.backend.entity.Identificator;
import cz.malickov.backend.enums.Department;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;
import java.util.UUID;


public record ChildInboundDTO (

    UUID childUuid,// can be null for creation
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    Department department,
    @Past
    Date birthDay,
    @NotNull
    Boolean active,
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
