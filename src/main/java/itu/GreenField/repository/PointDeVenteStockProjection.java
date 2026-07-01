package itu.GreenField.repository;

/**
 * Résultat de la fonction "find point de vente avec le plus de stock".
 * Spring Data mappe automatiquement les alias SQL (codepointdevente,
 * stocktotal) vers ces getters (insensible à la casse / underscores).
 */
public interface PointDeVenteStockProjection {
    String getCodePointDeVente();
    Integer getStockTotal();
}
