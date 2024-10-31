package eshop.backend.service.impl;

import eshop.backend.exception.DiscountNotFoundException;
import eshop.backend.model.Category;
import eshop.backend.model.Discount;
import eshop.backend.model.Product;
import eshop.backend.model.Variant;
import eshop.backend.repository.CategoryRepository;
import eshop.backend.repository.DiscountRepository;
import eshop.backend.request.DiscountRequest;
import eshop.backend.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Discount create(DiscountRequest request) {
        var discount = new Discount(request);

        manageCategoriesIfExist(discount, request);

        return discountRepository.save(discount);
    }

    @Override
    public Discount read(Long discountId) throws DiscountNotFoundException {
        return findByIdOrElseThrow(discountId, discountRepository, DiscountNotFoundException::new);
    }

    @Override
    public Discount update(DiscountRequest request) throws DiscountNotFoundException {
        var discount = read(request.id());

        updateDiscountProperties(discount, request);
        manageCategoriesIfExist(discount, request);

        return discountRepository.save(discount);
    }

    @Override
    public void delete(Long discountId) throws DiscountNotFoundException {
        var discount = read(discountId);

        discountRepository.delete(discount);
    }

    @Override
    public BigDecimal calculateDiscountedPrice(Variant variant) {
        Optional<Discount> discountOptional = Optional.ofNullable(variant.getProduct())
                .map(Product::getCategory)
                .map(Category::getDiscount);

        return discountOptional.filter(Discount::isActive)
                .map(discount -> variant.getBasePrice().multiply(BigDecimal.valueOf(1 - discount.getAmount())))
                .orElse(variant.getBasePrice());
    }

    private void manageCategoriesIfExist(Discount discount, DiscountRequest request) {
        if (request.categoryIds() != null && !request.categoryIds().isEmpty()) {
            var categories = categoryRepository.findAllById(request.categoryIds());

            discount.setCategories(new HashSet<>(categories));
        }
    }

    private void updateDiscountProperties(Discount discount, DiscountRequest request) {
        discount.setName(request.name());
        discount.setType(request.type());
        discount.setStartDate(request.startDate());
        discount.setEndDate(request.endDate());
    }
}
