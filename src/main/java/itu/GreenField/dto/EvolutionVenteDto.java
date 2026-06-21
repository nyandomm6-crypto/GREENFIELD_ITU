package itu.greenfield.dto;

import java.time.LocalDate;

public record EvolutionVenteDto(
    LocalDate date,
    Double totalVentes
) {}