package kr.hhplus.be.server.domain.product;

import static org.instancio.Select.field;

import java.util.List;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class ProductTestDataGenerator {

  public Product product() {
    return Instancio.of(Product.class)
        .ignore(field(Product::getId))
        .set(field(Product::getIsActive), true)
        .ignore(field(Product::getCreatedAt))
        .ignore(field(Product::getUpdatedAt))
        .create();
  }

  public List<ProductOption> productOptions(int size) {
    return Instancio.ofList(ProductOption.class)
        .size(size)
        .set(field(Product::getIsActive), true)
        .ignore(field(ProductOption::getId))
        .ignore(field(ProductOption::getCreatedAt))
        .ignore(field(ProductOption::getUpdatedAt))
        .create();
  }

  public ProductInventory productInventory() {
    return Instancio.of(ProductInventory.class)
        .set(field(Product::getIsActive), true)
        .ignore(field(ProductInventory::getId))
        .ignore(field(ProductInventory::getCreatedAt))
        .ignore(field(ProductInventory::getUpdatedAt))
        .create();
  }
}