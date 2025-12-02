package cz.malickov.backend.dto;

import cz.malickov.backend.enums.Role;

import java.math.BigDecimal;
import java.util.UUID;

public record UserOutboundDTO(
        UUID userUuid,
        String firstName,
        String lastName,
        String email,
        boolean active,
        Role role,
        BigDecimal credit) {}
