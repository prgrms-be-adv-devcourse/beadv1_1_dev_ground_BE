package io.devground.dbay.domain.image.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.devground.dbay.domain.image.repository.ImageRepository;
import io.devground.dbay.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;
}
