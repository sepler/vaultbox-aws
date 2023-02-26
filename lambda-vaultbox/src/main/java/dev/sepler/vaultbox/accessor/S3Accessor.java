package dev.sepler.vaultbox.accessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Log4j2
@RequiredArgsConstructor
public class S3Accessor {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final String stagingBucketName;

    private final String vaultBucketName;

    public String getStagingUploadUrl(final String key) {
        log.info("Creating presignPutObjectRequest with key: {}", key);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(stagingBucketName)
                .key(key)
                .build();
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();
        return s3Presigner.presignPutObject(putObjectPresignRequest).url().toString();
    }

    public String getVaultDownloadUrl(final String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(vaultBucketName)
                .key(key)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }

    public HeadObjectResponse headStagingObject(final String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(stagingBucketName)
                .key(key)
                .build();
        return s3Client.headObject(headObjectRequest);
    }

    public void moveToVault(final String key) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(stagingBucketName)
                .sourceKey(key)
                .destinationBucket(vaultBucketName)
                .destinationKey(key)
                .build();
        s3Client.copyObject(copyObjectRequest);
        deletingFromStaging(key);
    }

    public void deletingFromStaging(final String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(stagingBucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

}
