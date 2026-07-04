package itu.greenField.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UtilsService {
    public File sauvegarderPieceJointe(MultipartFile pieceJointe) throws IOException {
        if (pieceJointe == null || pieceJointe.isEmpty()) {
            return null;
        }

        String nomFichier = getNomOriginal(pieceJointe).replaceAll("[^a-zA-Z0-9._-]", "_");
        Path dossierTemporaire = Files.createTempDirectory("greenField-email-");
        Path fichierTemporaire = dossierTemporaire.resolve(nomFichier);

        Files.copy(pieceJointe.getInputStream(), fichierTemporaire, StandardCopyOption.REPLACE_EXISTING);
        return fichierTemporaire.toFile();
    }

    public String getNomOriginal(MultipartFile pieceJointe) {
        if (pieceJointe == null || pieceJointe.isEmpty()) {
            return null;
        }

        String nomOriginal = pieceJointe.getOriginalFilename();
        return nomOriginal == null || nomOriginal.isBlank() ? "piece-jointe" : nomOriginal;
    }
}
