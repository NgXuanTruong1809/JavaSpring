package vn.hoidanit.laptopshop.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;

@Service
public class UploadService {

    private final ServletContext servletContext;

    public UploadService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String handleSaveUploadFile(MultipartFile file, String targetFile) {
        // dont upload file
        if (file.isEmpty()) {
            return "";
        }
        //
        String fileNamePath = "";
        try {
            byte[] bytes;
            bytes = file.getBytes();
            // relative path : absolute path
            String rootPath = this.servletContext.getRealPath("/resources/images");
            // File.separator = "/"
            File dir = new File(rootPath + File.separator + targetFile);
            if (!dir.exists())
                dir.mkdirs();

            fileNamePath = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            // Create the file on server
            File serverFile = new File(dir.getAbsolutePath() + File.separator + fileNamePath);

            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileNamePath;
    }
}
