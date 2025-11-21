package cz.malickov.backend.dto;

import cz.malickov.backend.enums.Role;

import java.math.BigDecimal;

public record UserOutboundDTO(
        Long userId,
        String firstName,
        String lastName,
        String email,
        boolean active,
        Role role,
        BigDecimal credit) {}
