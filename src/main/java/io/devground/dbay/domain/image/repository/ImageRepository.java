package io.devground.dbay.domain.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.core.model.vo.ImageType;
import io.devground.dbay.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findAllByImageTypeAndReferenceCodeAndImageUrlIn(
		ImageType imageType, String referenceCode, List<String> imageUrls
	);

	List<Image> findAllByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);

	long deleteByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);

	@Modifying
	@Query("""
		DELETE FROM Image i
		WHERE i.referenceCode = :referenceCode
		AND i.imageType = :imageType
		AND i.imageUrl IN :imageUrls
		""")
	long deleteImagesByReferencesAndImageUrls(
		@Param("imageType") ImageType imageType,
		@Param("referenceCode") String referenceCode,
		@Param("imageUrls") List<String> imageUrls
	);
}
