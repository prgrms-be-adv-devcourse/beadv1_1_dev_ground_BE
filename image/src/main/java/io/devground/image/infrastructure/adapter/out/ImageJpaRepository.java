package io.devground.image.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.image.infrastructure.model.persistence.ImageEntity;

public interface ImageJpaRepository extends JpaRepository<ImageEntity, Long> {
}
