package com.springTemplate.springTemplate.controllers;

import org.slf4j.LoggerFactory;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

import com.springTemplate.springTemplate.models.Carte;
import com.springTemplate.springTemplate.repositories.CarteRepository;
import com.springTemplate.springTemplate.services.EmptyFileException;
import com.springTemplate.springTemplate.services.filestorage.FileStorageService;

import java.security.MessageDigest;

@RestController
public class CarteController {

    Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    private CarteRepository carteRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @CrossOrigin
    @PostMapping(value = "/carte", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public Carte post(@ModelAttribute Carte carte) {
        try {
            // on récupère l'image provenant de la classe Station (traitement automatique à
            // partir de la requête)
            MultipartFile imageFile = carte.getImageFile();
            if (imageFile.isEmpty()) {
                throw new EmptyFileException("Le fichier est vide, veuillez sélectionner un fichier valide.");
            }
            logger.info("Sauvegarde du fichier image");

            // calcul du hash du fichier pour obtenier un nom unique
            String storageHash = getStorageHash(imageFile).get();
            Path rootLocation = this.fileStorageService.getRootLocation();
            // récupération de l'extension
            String fileExtension = mimeTypeToExtension(imageFile.getContentType());
            // ajout de l'extension au nom du fichier
            storageHash = storageHash + fileExtension;
            // on retrouve le chemin de stockage de l'image
            Path saveLocation = rootLocation.resolve(storageHash);

            // suppresion du fichier au besoin
            Files.deleteIfExists(saveLocation);

            // tentative de sauvegarde
            Files.copy(imageFile.getInputStream(), saveLocation);

            // à ce niveau il n'y a pas eu d'exeption
            // on ajoute le nom utilisé pour stocké l'image
            carte.setImage(storageHash);

            // on modifie la BDD
            return carteRepository.save(carte);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        // Si on arrive la alors erreur
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossible de sauvegarder la rerssource.");
    }

    /**
     * Retourne l'extension d'un fichier en fonction d'un type MIME
     * pour plus d'informations sur les types MIME :
     * https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
     */
    private String mimeTypeToExtension(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> ".jpeg";
            case "image/png" -> ".png";
            case "image/svg" -> ".svg";
            default -> "";
        };
    }

    /**
     * Permet de retrouver un hash qui pourra être utilisé comme nom de fichier
     * uniquement pour le stockage.
     *
     * Le hash sera calculé à partir du nom du fichier, de son type MIME
     * (https://developer.mozilla.org/fr/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types)
     * et de la date d'upload.
     *
     * @return Le hash encodé en base64
     */
    private Optional<String> getStorageHash(MultipartFile file) {
        String hashString = null;

        if(!file.isEmpty()) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");

                // il faut donc transformer les différents objets utilisés pour le hachage en
                // La méthode digest de la classe "MessageDigest" prend en paramètre un byte[]
                // Nous utiliserons la classe "ByteArrayOutputStream" pour se faire
                // tableau d'octets
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                outputStream.write(file.getOriginalFilename().getBytes());
                outputStream.write(file.getContentType().getBytes());
                LocalDate date = LocalDate.now();
                outputStream.write(date.toString().getBytes());
                
                // calcul du hash, on obtient un tableau d'octets
                byte[] hashBytes = messageDigest.digest(outputStream.toByteArray());

                // on retrouve une chaine de caractères à partir d'un tableau d'octets
                hashString = String.format("%032x", new BigInteger(1,hashBytes));
            } catch (NoSuchAlgorithmException | IOException e) {
                logger.error(e.getMessage());
            }
        }
        return Optional.ofNullable(hashString);
    }

}
