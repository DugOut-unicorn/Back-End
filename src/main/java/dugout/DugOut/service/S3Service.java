package dugout.DugOut.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadProfileImage(MultipartFile file, String email) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "userProfile/profile/" + email + "/" + UUID.randomUUID().toString() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest request = new PutObjectRequest(
                bucket,
                fileName,
                file.getInputStream(),
                metadata
        );

        amazonS3Client.putObject(request);
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public String uploadPlayerImage(MultipartFile file, Integer playerIdx) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "players/" + playerIdx + "/" + UUID.randomUUID().toString() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest request = new PutObjectRequest(
                bucket,
                fileName,
                file.getInputStream(),
                metadata
        );

        amazonS3Client.putObject(request);
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteProfileImage(String imageUrl) {
        try {
            String key = imageUrl.substring(imageUrl.indexOf(bucket) + bucket.length() + 1);
            amazonS3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            log.error("Error deleting image from S3: {}", e.getMessage());
        }
    }
} 