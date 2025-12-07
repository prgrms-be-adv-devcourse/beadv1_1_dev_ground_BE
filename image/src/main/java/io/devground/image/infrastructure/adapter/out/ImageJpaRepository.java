package io.devground.image.infrastructure.adapter.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.devground.core.model.vo.ImageType;
import io.devground.image.infrastructure.model.persistence.ImageEntity;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {

	List<ImageEntity> findAllByImageTypeAndReferenceCodeAndImageUrlIn(
		ImageType imageType, String referenceCode, List<String> imageUrls
	);

	List<ImageEntity> findAllByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);

	long deleteByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);

	@Modifying
	@Query("""
		DELETE FROM ImageEntity i
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
