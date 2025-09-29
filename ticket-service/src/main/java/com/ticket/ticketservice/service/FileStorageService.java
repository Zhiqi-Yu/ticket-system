package com.ticket.ticketservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${storage.upload-dir}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String save(MultipartFile file, Long ticketId) throws IOException {
        String clean = StringUtils.cleanPath(file.getOriginalFilename());
        String fname = ticketId + "_" + System.currentTimeMillis() + "_" + clean;
        Path target = this.root.resolve(fname);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    public Resource loadAsResource(String path){
        return new FileSystemResource(path);
    }
}
