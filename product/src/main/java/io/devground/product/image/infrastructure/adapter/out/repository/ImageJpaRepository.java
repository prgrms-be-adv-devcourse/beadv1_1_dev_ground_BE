package io.devground.product.image.infrastructure.adapter.out.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.devground.core.model.vo.ImageType;
import io.devground.product.image.infrastructure.model.persistence.ImageEntity;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {

	List<ImageEntity> findAllByImageTypeAndReferenceCodeAndImageUrlIn(
		ImageType imageType, String referenceCode, List<String> imageUrls
	);

	List<ImageEntity> findAllByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		DELETE FROM ImageEntity i
		WHERE i.imageType = :imageType
		AND i.referenceCode = :referenceCode
		AND i.imageUrl IN :imageUrls
		""")
	void deleteAllImagesByImageSpec(
		ImageType imageType, String referenceCode, List<String> imageUrls
	);
}
