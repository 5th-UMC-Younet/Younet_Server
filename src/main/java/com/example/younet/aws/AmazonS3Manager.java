package com.example.younet.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.younet.configuration.AmazonConfig;
import com.example.younet.domain.Uuid;
import com.example.younet.post.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata=new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        try{
            String extension=file.getOriginalFilename();
            keyName=keyName+'.'+ extension.substring(extension.lastIndexOf(".")+1);
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName,file.getInputStream(),metadata));
        }catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile: {}",(Object) e.getStackTrace());
        }
        return amazonS3.getUrl(amazonConfig.getBucket(),keyName).toString();
    }

    public String generateKeyName(Uuid uuid){
        return amazonConfig.getImagePath()+'/'+uuid.getUuid();
    }
}