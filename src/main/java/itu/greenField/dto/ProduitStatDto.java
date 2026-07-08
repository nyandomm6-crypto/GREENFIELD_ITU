package itu.greenfield.dto;

public record ProduitStatDto(
    Integer id,
    String nom,
    Long quantiteVendue
) {
}