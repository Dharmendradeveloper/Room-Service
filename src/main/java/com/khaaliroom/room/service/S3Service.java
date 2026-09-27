package com.khaaliroom.room.service;

import com.khaaliroom.room.dto.RoomImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final long MAX_IMAGE_SIZE_BYTES = 500L * 1024L;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public RoomImageUploadResponse generateUploadUrl(
            UUID roomId,
            String contentType) {

        if (!List.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        ).contains(contentType.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Unsupported image content type: " + contentType
            );
        }

        UUID imageId = UUID.randomUUID();

        String extension = getFileExtension(contentType);

        String objectKey =
                "rooms/" + roomId + "/" + imageId + extension;

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(contentType)
                        .build();

        try (S3Presigner presigner =
                     S3Presigner.builder()
                             .region(Region.of(region))
                             .build()) {

            PresignedPutObjectRequest presignedRequest =
                    presigner.presignPutObject(
                            builder -> builder
                                    .signatureDuration(
                                            Duration.ofMinutes(10)
                                    )
                                    .putObjectRequest(
                                            putObjectRequest
                                    )
                    );

            return new RoomImageUploadResponse(
                    imageId,
                    objectKey,
                    presignedRequest.url().toString(),
                    10
            );
        }
    }

    public HeadObjectResponse getObjectMetadata(
            String objectKey) {

        HeadObjectRequest request =
                HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        return s3Client.headObject(request);
    }

    public String getObjectContentType(String objectKey) {

        HeadObjectResponse metadata =
                getObjectMetadata(objectKey);

        return metadata.contentType();
    }

    public void validateImageSize(
            String objectKey) {

        HeadObjectResponse metadata =
                getObjectMetadata(objectKey);

        long contentLength = metadata.contentLength();

        if (contentLength <= 0) {
            throw new IllegalArgumentException(
                    "Uploaded image is empty"
            );
        }

        if (contentLength > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 500 KB"
            );
        }
    }

    public void deleteObject(String objectKey) {

        s3Client.deleteObject(builder ->
                builder
                        .bucket(bucketName)
                        .key(objectKey)
                        .build()
        );
    }

    private String getFileExtension(String contentType) {

        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException(
                    "Unsupported image content type: " + contentType
            );
        };
    }

    public String generateDownloadUrl(
            String objectKey) {

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        try (S3Presigner presigner =
                     S3Presigner.builder()
                             .region(Region.of(region))
                             .build()) {

            PresignedGetObjectRequest presignedRequest =
                    presigner.presignGetObject(
                            builder -> builder
                                    .signatureDuration(
                                            Duration.ofMinutes(15)
                                    )
                                    .getObjectRequest(
                                            getObjectRequest
                                    )
                    );

            return presignedRequest.url().toString();
        }
    }
}
