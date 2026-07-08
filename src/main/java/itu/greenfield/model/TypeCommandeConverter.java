package itu.greenfield.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TypeCommandeConverter implements AttributeConverter<TypeCommande, String> {

    @Override
    public String convertToDatabaseColumn(TypeCommande attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public TypeCommande convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Normalise 'En boutique' ou 'En ligne' en remplaçant l'espace par un underscore
        String normalized = dbData.trim().replace(" ", "_");
        try {
            return TypeCommande.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return TypeCommande.fromString(normalized);
        }
    }
}
