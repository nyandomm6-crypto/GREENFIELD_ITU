package java.it.GreenField.dto;

public record ClientStatDto(
    Integer id,
    String nom,
    String prenom,
    Double totalDepense
) {}