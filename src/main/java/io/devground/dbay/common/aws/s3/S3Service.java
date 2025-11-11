package io.devground.dbay.common.aws.s3;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.devground.core.model.exception.ServiceException;
import io.devground.core.model.vo.ErrorCode;
import io.devground.dbay.domain.image.vo.ImageType;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	public List<URL> generatePresignedUrls(ImageType imageType, String referenceCode, List<String> fileExtensions) {

		if (CollectionUtils.isEmpty(fileExtensions)) {
			return List.of();
		}

		try {
			return fileExtensions.stream()
				.map(extension -> this.generatePresignedUrl(imageType, referenceCode, extension))
				.toList();
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public URL generatePresignedUrl(ImageType imageType, String referenceCode, String fileExtension) {

		try {
			// PresignedURL 생성
			return s3Presigner.presignPutObject(builder -> builder
					.putObjectRequest(this.buildPutObjectRequest(imageType, referenceCode, fileExtension))
					.signatureDuration(Duration.ofMinutes(5)))
				.url();
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// S3 객체 모두 삭제 (다건 삭제), type + code 참조
	public void deleteAllObjectsByIdentifier(ImageType imageType, String referenceCode) {

		String folderPath = S3Util.getFolderPath(imageType, referenceCode);

		try {
			List<S3Object> objects = this.getAllS3ObjectsInFolderPath(folderPath);

			if (objects.isEmpty()) {
				return;
			}

			// 삭제할 키 목록 생성
			List<ObjectIdentifier> objectIdentifiers = objects.stream()
				.map(this::buildObjectIdentifier)
				.toList();

			s3Client.deleteObjects(this.buildDeleteObjectsRequest(objectIdentifiers));
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_OBJECT_DELETE_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// S3 객체 삭제 (단건 삭제), type + code 참조
	public void deleteObjectByIdentifier(ImageType imageType, String referenceCode) {

		String folderPath = S3Util.getFolderPath(imageType, referenceCode);

		try {
			List<S3Object> objects = this.getAllS3ObjectsInFolderPath(folderPath);

			if (objects.isEmpty()) {
				return;
			}

			String key = objects.getFirst().key();

			s3Client.deleteObject(this.buildDeleteObjectRequest(key));
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_OBJECT_DELETE_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// S3 객체 모두 삭제 (다건 삭제), url 참조
	public void deleteObjectsByUrl(List<String> urls) {

		if (CollectionUtils.isEmpty(urls)) {
			return;
		}

		try {
			// 삭제할 키가 포함되는 ObjectIdentifier 생성
			List<ObjectIdentifier> objectIdentifiers = urls.stream()
				.map(S3Util::extractS3KeyFromUrl)
				.map(this::buildObjectIdentifier)
				.toList();

			// S3에서 해당 키의 파일들을 모두 삭제
			s3Client.deleteObjects(this.buildDeleteObjectsRequest(objectIdentifiers));
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_OBJECT_DELETE_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	// S3 객체 삭제 (단건 삭제), url 참조
	public void deleteObjectByUrl(String url) {

		if (StringUtils.isBlank(url)) {
			return;
		}

		try {
			String key = S3Util.extractS3KeyFromUrl(url);

			if (key == null) {
				return;
			}

			s3Client.deleteObject(this.buildDeleteObjectRequest(key));
		} catch (SdkException e) {
			throw new ServiceException(ErrorCode.S3_OBJECT_DELETE_FAILED);
		} catch (Exception e) {
			throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

	}

	// S3 폴더의 모든 객체 조회
	private List<S3Object> getAllS3ObjectsInFolderPath(String folderPath) {

		List<S3Object> objects = new ArrayList<>();

		try {
			// 다음 페이지 여부
			String continuationToken = null;
			do {
				ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
					.bucket(bucketName)
					.prefix(folderPath)
					.continuationToken(continuationToken)
					.build();

				ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
				objects.addAll(listResponse.contents());

				continuationToken = listResponse.nextContinuationToken();
			} while (continuationToken == null);
		} catch (SdkException e) {
			ErrorCode.S3_OBJECT_GET_FAILED.throwServiceException();
		}

		return objects;
	}

	// S3 ObjectIdentifier 생성
	private ObjectIdentifier buildObjectIdentifier(String key) {

		return ObjectIdentifier.builder()
			.key(key)
			.build();
	}

	private ObjectIdentifier buildObjectIdentifier(S3Object object) {

		return ObjectIdentifier.builder()
			.key(object.key())
			.build();
	}

	// S3 PutObjectRequest 생성
	private PutObjectRequest buildPutObjectRequest(ImageType imageType, String referenceCode, String fileExtension) {

		return PutObjectRequest.builder()
			.bucket(bucketName)
			.key(S3Util.buildS3Key(imageType, referenceCode, fileExtension))
			.build();
	}

	// S3 DeleteObjectsRequest 생성 (다건 삭제)
	private DeleteObjectsRequest buildDeleteObjectsRequest(List<ObjectIdentifier> objectIdentifiers) {

		return DeleteObjectsRequest.builder()
			.bucket(bucketName)
			.delete(delete -> delete.objects(objectIdentifiers))
			.build();
	}

	// S3 DeleteObjectRequest 생성 (단건 삭제)
	private DeleteObjectRequest buildDeleteObjectRequest(String key) {

		return DeleteObjectRequest.builder()
			.bucket(bucketName)
			.key(key)
			.build();
	}
}
