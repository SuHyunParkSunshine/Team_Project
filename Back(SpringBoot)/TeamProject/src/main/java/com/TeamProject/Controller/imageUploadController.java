package com.TeamProject.Controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.TeamProject.Service.FlaskService.imageSendService;
import com.TeamProject.Service.SpringBootService.imageUploadService;

@RestController
public class imageUploadController {

    @Autowired
    imageUploadService imageuploadservice;

    @Autowired
    imageSendService imagesendservice;
    
    @PostMapping("/uploadSpring")
    public ResponseEntity<String> uploadController(@RequestParam(name = "pngFile", required = false) MultipartFile pngFile,
                                                   @RequestParam(name = "plyFile", required = false) MultipartFile plyFile) {
        imageuploadservice.uploadService(pngFile, plyFile); // StringBoot 
        imagesendservice.sendImage(pngFile, plyFile); // Flask
        return ResponseEntity.ok("SpringBoot 이미지 전송 성공");
    }

    // 이미지 파일이 저장된 디렉토리 경로를 설정.
	private final String imageDirectory = "C:/Team_Project/Back(SpringBoot)/TeamProject/image";

	// 이미지 조회
	@GetMapping("/upload/image/{imageName:.+}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws MalformedURLException {

        // 요청된 이미지 파일 이름을 사용하여 이미지 파일의 경로를 가져오기.
        Path imagePath = Paths.get(imageDirectory).resolve(imageName);
        Resource imageResource = new UrlResource(imagePath.toUri());

        // 이미지 파일이 존재하지 않는 경우.
        if (!imageResource.exists()) {
            // 이미지가 없을 경우에 대한 처리를 여기에 작성.
            return ResponseEntity.notFound().build();
        }

        // 이미지 파일을 응답으로 반환합니다.
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentType(MediaType.IMAGE_PNG)
                .body(imageResource);
    }
}
