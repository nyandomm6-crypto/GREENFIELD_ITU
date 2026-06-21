package itu.greenfield.dto;

import java.math.BigDecimal;

public record ClientStatDto(
    Integer id,
    String nom,
    String prenom,
    BigDecimal totalDepense
) {
}