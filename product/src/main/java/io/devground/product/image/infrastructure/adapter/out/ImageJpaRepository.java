package io.devground.product.image.infrastructure.adapter.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.core.model.vo.ImageType;
import io.devground.product.image.infrastructure.model.persistence.ImageEntity;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {

	List<ImageEntity> findAllByImageTypeAndReferenceCodeAndImageUrlIn(
		ImageType imageType, String referenceCode, List<String> imageUrls
	);

	List<ImageEntity> findAllByImageTypeAndReferenceCode(ImageType imageType, String referenceCode);
}
