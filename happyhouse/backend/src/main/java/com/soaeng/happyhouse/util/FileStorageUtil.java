package com.soaeng.happyhouse.util;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileStorageUtil {

    @Value("${app.path.upload}")
    private String uploadPath;
    private String PROFILE_FOLDER = "profile";
    private final String THUMBNAIL_PREFIX = "thumb_";


    @PostConstruct
    public void init() {
        uploadPath = getAbsolutePath(uploadPath);
    }

    public String saveProfileImage(String url) throws RuntimeException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String folderPath = getAbsolutePath(uploadPath + File.separator + PROFILE_FOLDER);

        String filename = UUID.randomUUID() + "_" + simpleDateFormat.format(new Date());
        String filePath = folderPath + File.separator + filename;

        byte[] imageBytes = new RestTemplate().getForObject(url, byte[].class);

        try {
            Files.write(Paths.get(filePath), imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String mimeType;
        try {
            //Tika로 확장자 구분
            mimeType = new Tika().detect(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String ext = mimeType.replaceAll("image/", "");
        ext = ext.replaceAll("jpeg", "jpg");
        String newFilePath = filePath + "." + ext;
        boolean result = new File(filePath).renameTo(new File(newFilePath));

        return result ? filename + "." + ext : null;
    }

    public List<String> saveFiles(String folderName, List<MultipartFile> files) throws RuntimeException {
        if (files == null || files.size() == 0) {
            return new ArrayList<>();
        }

        List<String> uploadNames = new ArrayList<>();
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; // 파일이 비어 있으면 무시하고 다음 파일로
            }

            String originalFilename = file.getOriginalFilename();
            String savedName = UUID.randomUUID() + "_" + convertFilename(originalFilename);
            String folderPath = uploadPath + File.separator + folderName;

            // 저장 폴더 생성
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean completed = folder.mkdir();
                if (!completed) {
                    log.info("폴더 생성에 실패했습니다.");
                }
            }

            Path savePath = Paths.get(folderPath, savedName);
            try {
                Files.copy(file.getInputStream(), savePath);
                String contentType = file.getContentType();
                if (contentType.startsWith("image")) {
                    if (!folderName.equals(PROFILE_FOLDER)) {
                        Path thumbnailPath = Paths.get(folderPath, THUMBNAIL_PREFIX + savedName);
                        Thumbnails.of(savePath.toFile())
                                .sourceRegion(Positions.CENTER, 200, 150)
                                .size(200, 150)
                                .toFile(thumbnailPath.toFile());
                    }
                }
                uploadNames.add(savedName);
            } catch (IOException e) {
                deleteFiles(folderName, uploadNames);
                throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage(), e);
            }
        }

        return uploadNames;
    }

    public ResponseEntity<Resource> getFile(String folderName, String fileName) {

        String folderPath = uploadPath + File.separator + folderName;
        Resource resource = new FileSystemResource(folderPath + File.separator + fileName);

        if (!resource.exists()) {
            resource = new FileSystemResource(folderPath + File.separator + "default.png");
        }

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(String folderName, List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        String folderPath = uploadPath + File.separator + folderName;
        fileNames.forEach(fileName -> {
            try {
                // 썸네일 존재 여부 확인 후 삭제
                if (!folderName.equals(PROFILE_FOLDER)) {
                    String thumbnailFileName = THUMBNAIL_PREFIX + fileName;
                    Path thumbnailPath = Paths.get(folderPath, thumbnailFileName);
                    Files.deleteIfExists(thumbnailPath);
                }

                Path filePath = Paths.get(folderPath, fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage(), e);
            }
        });
    }

    private String getAbsolutePath(String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            boolean completed = folder.mkdir();
            if (!completed) {
                log.info("폴더 생성에 실패했습니다.");
            }
        }

        return folder.getAbsolutePath();
    }

    private String convertFilename(String filename) {
        return filename.replaceAll(" ", "_");
    }

}
