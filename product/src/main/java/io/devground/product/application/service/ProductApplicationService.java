package io.devground.product.application.service;

import org.springframework.stereotype.Service;

import io.devground.product.domain.port.in.CategoryUseCase;
import io.devground.product.domain.port.in.ProductSaleUseCase;
import io.devground.product.domain.port.in.ProductUseCase;
import lombok.RequiredArgsConstructor;

// TODO: Dbay 제거 후 ProductApplication으로 롤백
@Service
@RequiredArgsConstructor
public class ProductApplicationService implements CategoryUseCase, ProductUseCase, ProductSaleUseCase {


}
