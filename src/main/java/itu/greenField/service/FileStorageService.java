package itu.greenField.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stocke les fichiers uploadés sur le disque et renvoie leur chemin web.
 * Les fichiers sont servis via /uploads/** (voir WebConfig).
 */
@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * Sauvegarde un fichier dans un sous-dossier et renvoie son chemin web
     * (ex : /uploads/produits/17_abc.png), ou null si le fichier est vide.
     */
    public String store(MultipartFile file, String sousDossier, String prefixe) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path dossier = rootLocation.resolve(sousDossier).normalize();
            Files.createDirectories(dossier);

            String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String extension = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                extension = original.substring(dot).toLowerCase();
            }
            String nomFichier = (prefixe == null ? "" : prefixe + "_") + System.nanoTime() + extension;

            Path cible = dossier.resolve(nomFichier).normalize();
            // sécurité : la cible doit rester sous le dossier racine
            if (!cible.startsWith(rootLocation)) {
                throw new IllegalStateException("Chemin de fichier invalide.");
            }
            Files.copy(file.getInputStream(), cible, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + sousDossier + "/" + nomFichier;
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'enregistrement du fichier : " + e.getMessage(), e);
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}
