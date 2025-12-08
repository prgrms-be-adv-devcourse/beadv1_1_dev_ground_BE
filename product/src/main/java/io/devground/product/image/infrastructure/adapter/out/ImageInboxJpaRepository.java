package io.devground.product.image.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.product.image.infrastructure.model.persistence.ImageInbox;

public interface ImageInboxJpaRepository extends JpaRepository<ImageInbox, Long> {
}
