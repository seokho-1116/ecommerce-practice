package kr.hhplus.be.server.domain.product;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private ProductRepository productRepository;

  public List<Product> findAllByProductOptionIds(List<Long> productOptionIds) {
    return productRepository.findAllByProductOptionIds(productOptionIds);
  }

  public void deductInventory(ProductDeductCommand productDeductCommand) {
    if (productDeductCommand == null || productDeductCommand.isEmpty()) {
      throw new ProductBusinessException("상품 재고 차감 커맨드는 null이거나 차감할 상품 항목이 비어있을 수 없습니다.");
    }

    List<Long> productOptionIds = productDeductCommand.productOptionIds();
    List<ProductInventory> productInventories = productRepository.findProductInventoriesByProductOptionIds(productOptionIds);

    for (ProductInventory productInventory : productInventories) {
      Long productOptionId = productInventory.getProductOption().getId();
      Long amount = productDeductCommand.getAmount(productOptionId);

      if (amount != null) {
        productInventory.deduct(amount);
      }
    }

    productRepository.saveAll(productInventories);
  }

  public List<Product> findTop5SellingProducts() {
    return productRepository.findTop5SellingProducts();
  }

  public List<Product> findAllProducts() {
    return productRepository.findAll();
  }
}
