package com.nike.artemis;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class S3RuleSourceProviderImpl implements RuleSourceProvider, Serializable {

    public String regionName;
    public String bucketName;
    public String keyName;

    public S3RuleSourceProviderImpl(Properties properties){
        this.regionName = properties.getProperty("RulesRegionName");
        this.bucketName = properties.getProperty("RulesBucketName");
        this.keyName = properties.getProperty("RulesKeyName");
    }

    @Override
    public Date getLastModified() {
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(regionName)
                .build();

        try{
            S3Object amazonS3Object = amazonS3.getObject(new GetObjectRequest(bucketName, keyName));
            return amazonS3Object.getObjectMetadata().getLastModified();
        }catch (Exception e){
            return Date.from(Instant.EPOCH);
        }
    }

    @Override
    public InputStream getObjectContent() {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(regionName)
                .build();
        try {
            S3Object rulesObject = s3Client.getObject(new GetObjectRequest(bucketName, keyName));
            return rulesObject.getObjectContent();
        } catch (Exception e) {
            return null;
        }
    }
}
