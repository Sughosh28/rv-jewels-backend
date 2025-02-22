package com.rv.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface AwsService {

    String uploadImage(MultipartFile image);

    List<String> allFiles();

    String preSignedUrl();
}
