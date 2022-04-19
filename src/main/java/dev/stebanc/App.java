package dev.stebanc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws InvalidKeyException, ErrorResponseException,
            InsufficientDataException, InternalException, InvalidResponseException, NoSuchAlgorithmException,
            ServerException, XmlParserException, IllegalArgumentException, IOException {

        String accessKey = "admin";
        String secretKey = "password";

        MinioClient minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000")
                .credentials(accessKey, secretKey).build();

        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket("cats").build());
        if (isExist) {
            System.out.println("Bucket already exists.");
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("cats").build());
        }

        minioClient.listBuckets().forEach(b -> System.out.println(b.name()));

        URL url = new URL(
                "https://preview.redd.it/7i4g79z1ih071.jpg?width=640&crop=smart&auto=webp&s=139c4dc2c873d538316519031dc7c8ea8bd86c36");

        Path tempFile = Files.createTempFile("cat", ".jpg");
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        UploadObjectArgs.Builder builder = UploadObjectArgs.builder().bucket("cats")
                .object("cat.jpg").filename(tempFile.toString());
        minioClient.uploadObject(builder.build());
    }
}
