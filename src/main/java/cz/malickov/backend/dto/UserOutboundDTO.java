package cz.malickov.backend.dto;

import cz.malickov.backend.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserOutboundDTO(
        UUID userUuid,
        String firstName,
        String lastName,
        String email,
        String telephone,
        boolean active,
        boolean passwordSet,
        Role role,
        BigDecimal credit,
        String identifier,
        LocalDateTime createdAt) {}
