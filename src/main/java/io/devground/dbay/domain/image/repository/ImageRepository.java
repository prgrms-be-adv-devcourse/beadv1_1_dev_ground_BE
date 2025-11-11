package io.devground.dbay.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.devground.dbay.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
