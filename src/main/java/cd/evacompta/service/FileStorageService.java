package cd.evacompta.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * Gere l'enregistrement physique des images televersees dans le dossier
 * defini par app.upload.dir. Retourne le nom de fichier a stocker en base.
 */
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload.dir}") String dir) {
        this.uploadDir = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de creer le dossier d'upload : " + this.uploadDir, e);
        }
    }

    /**
     * Enregistre le fichier et renvoie son nom unique.
     * Renvoie null si aucun fichier n'a ete fourni.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase();
        // nom unique pour eviter les collisions
        String filename = "img-" + UUID.randomUUID().toString().substring(0, 8) + ext;
        try {
            Path target = this.uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Echec de l'enregistrement du fichier " + filename, e);
        }
        return filename;
    }

    public Path getUploadDir() { return uploadDir; }
}
