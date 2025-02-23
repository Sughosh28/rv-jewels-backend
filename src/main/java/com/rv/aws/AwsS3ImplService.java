package com.rv.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AwsS3ImplService implements AwsMethodsInterface {

    @Autowired
    private AmazonS3 client;

    @Value("${app.aws.s3.bucket}")
    private String bucketName;

    public String uploadImage(MultipartFile image, String folder) throws IOException {
        String actualFileName = image.getOriginalFilename();
        assert actualFileName != null;
        String fileName = folder + "/" + System.currentTimeMillis() + "_" + actualFileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());

        client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), objectMetadata));

        return preSignedUrl(fileName);
    }

    public List<String> allFiles(String folder) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folder + "/");

        ListObjectsV2Result listObjectsV2Result = client.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

        return objectSummaries.stream().map(item -> this.preSignedUrl(item.getKey())).toList();
    }

    public String preSignedUrl(String fileName) {
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + 1000 * 60 * 60);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(expirationDate);
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public List<String> uploadImages(List<MultipartFile> images, String folder) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String actualFileName = image.getOriginalFilename();
            assert actualFileName != null;

            String fileName = folder + "/" + System.currentTimeMillis() + "_" + actualFileName.replaceAll("\\s+", "_");

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(image.getSize());

            client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), objectMetadata));

            uploadedUrls.add(preSignedUrl(fileName));
        }

        return uploadedUrls;
    }

}
