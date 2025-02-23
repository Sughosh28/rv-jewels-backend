package com.rv.aws;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface AwsMethodsInterface {

    String uploadImage(MultipartFile image, String folder) throws IOException;

    List<String> allFiles(String folder);

    String preSignedUrl(String filename);
}
