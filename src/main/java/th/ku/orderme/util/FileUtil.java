package th.ku.orderme.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Slf4j
public class FileUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Success save image file: "+ fileName);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public static void deleteFile(String dir, String fileName) throws IOException {
        try {
            if(fileName == null) return;
            Path path = Paths.get(dir);
            Path filePath = path.resolve(fileName);

            // Delete file or directory
            Files.delete(filePath);
            log.info("File or directory deleted successfully: "+ fileName);
        }
        catch (NoSuchFileException ex) {
            log.error("No such file or directory: "+fileName);
        }
        catch (DirectoryNotEmptyException ex) {
            log.error("Directory" +dir+ "is not empty");
        }
        catch (IOException ex) {
            throw ex;
        }
    }
}
