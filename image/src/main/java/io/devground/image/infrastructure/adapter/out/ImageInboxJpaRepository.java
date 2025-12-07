package io.devground.image.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.image.infrastructure.model.ImageInbox;

public interface ImageInboxJpaRepository extends JpaRepository<ImageInbox, Long> {
}
