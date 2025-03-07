package com.rv.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsS3ImplService implements AwsMethodsInterface {

    private final AmazonS3 client;

    public AwsS3ImplService(AmazonS3 client) {
        this.client = client;
    }

    @Value("${app.aws.s3.bucket}")
    private String bucketName;

    @Value("${app.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public String uploadImage(MultipartFile image, String folder) throws IOException {
        String actualFileName = image.getOriginalFilename();
        assert actualFileName != null;
        String fileName = folder + "/" + System.currentTimeMillis() + "_" + actualFileName.replaceAll("\\s+", "_");

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());

        client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), objectMetadata));

        return getCloudFrontUrl(fileName); // ✅ Uses CloudFront URL
    }

    @Override
    public List<String> allFiles(String folder) {
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folder + "/");

        ListObjectsV2Result listObjectsV2Result = client.listObjectsV2(listObjectsV2Request);
        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();

        return objectSummaries.stream()
                .map(item -> getCloudFrontUrl(item.getKey()))
                .toList();
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

            uploadedUrls.add(getCloudFrontUrl(fileName));
        }

        return uploadedUrls;
    }

    private String getCloudFrontUrl(String fileName) {
        System.out.println("https://" + cloudFrontDomain + "/" + fileName);
        return "https://" + cloudFrontDomain + "/" + fileName;
    }
}
