package io.devground.image.infrastructure.adapter.out;

import org.springframework.stereotype.Repository;

import io.devground.image.application.persistence.ImagePersistencePort;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImagePersistenceAdapter implements ImagePersistencePort {
}
