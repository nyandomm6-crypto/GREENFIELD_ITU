package itu.greenfield.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EvolutionVenteDto(
    LocalDate date,
    BigDecimal totalVentes
) {}