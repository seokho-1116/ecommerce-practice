package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.order.OrderCommand.ProductAmountPair;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private ProductTestDataGenerator productTestDataGenerator;

  @Autowired
  private ProductService productService;

  private final List<ProductOption> productOptions = new ArrayList<>();
  private ProductOption limitedQuantityProductOption;

  @BeforeEach
  void setup() {
    for (int i = 0; i < 5; i++) {
      saveProductAndRelated();
    }

    Product product = saveProductAndRelated();

    saveProductOptionAndInventory(product);
  }

  private Product saveProductAndRelated() {
    Product product = productTestDataGenerator.product();
    testHelpRepository.save(product);

    List<ProductOption> productOption = productTestDataGenerator.productOptions(10);

    for (ProductOption option : productOption) {
      option.setupProduct(product);
      testHelpRepository.save(option);
      productOptions.add(option);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      TestReflectionUtil.setField(productInventory, "quantity", 7L);
      productInventory.setupProductOption(option);
      testHelpRepository.save(productInventory);

      OrderItem orderItem = OrderItem.create(new ProductAmountPair(product, option, 1L));
      TestReflectionUtil.setField(orderItem, "createdAt",
          LocalDateTime.now().minusDays(1).minusMinutes(30));
      testHelpRepository.save(orderItem);
    }
    return product;
  }

  private void saveProductOptionAndInventory(Product product) {
    limitedQuantityProductOption = productTestDataGenerator.productOption();
    limitedQuantityProductOption.setupProduct(product);
    testHelpRepository.save(limitedQuantityProductOption);

    ProductInventory productInventory = productTestDataGenerator.productInventory();
    TestReflectionUtil.setField(productInventory, "quantity", 1L);
    productInventory.setupProductOption(limitedQuantityProductOption);
    testHelpRepository.save(productInventory);
  }

  @DisplayName("상위 상품 조회 시 이전 3일동안 가장 많이 팔린 상품이 순위로 정렬되어 조회된다")
  @Test
  void getTopSellingProducts() {
    // given
    LocalDateTime from = LocalDateTime.now().minusDays(1).minusHours(1);
    LocalDateTime to = LocalDateTime.now().minusDays(1);
    productService.saveTop5SellingProductsBefore(from, to);

    // when
    Top5SellingProducts top5SellingProducts = productService.findTop5SellingProducts();

    // then
    assertThat(top5SellingProducts.topSellingProducts())
        .isNotEmpty()
        .isSortedAccordingTo(Comparator.comparing(ProductWithRank::rank));
  }

  @DisplayName("동시에 재고를 차감하면 차감 요청 수만큼 전부 차감된다")
  @Test
  void reduceStockTest() throws InterruptedException {
    // given
    int concurrentRequest = 2;
    long quantity = 2L;
    Map<Long, Long> productOptionIdToAmount = productOptions.stream()
        .collect(Collectors.toMap(ProductOption::getId, productOption -> quantity));
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          ProductDeductCommand productDeductCommand = new ProductDeductCommand(
              productOptionIdToAmount);
          productService.deductInventory(productDeductCommand);

          successCount.incrementAndGet();
        } catch (Exception e) {
          failureCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }

    latch.await();

    // then
    assertThat(successCount.get()).isEqualTo(concurrentRequest);
    assertThat(failureCount.get()).isZero();
  }

  @DisplayName("순차적으로 재고를 차감해서 0이 되는 경우 동시에 재고를 차감해도 재고가 0이 된다")
  @Test
  void reduceStockToZeroTest() throws InterruptedException {
    // given
    Map<Long, Long> productOptionIdToAmount = Map.of(limitedQuantityProductOption.getId(),
        limitedQuantityProductOption.getProductInventory().getQuantity());
    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          ProductDeductCommand productDeductCommand = new ProductDeductCommand(
              productOptionIdToAmount);
          productService.deductInventory(productDeductCommand);
        } catch (Exception ignore) {
          // ignore
        } finally {
          latch.countDown();
        }
      }).start();
    }

    latch.await();

    // then
    ProductInventory productInventory = extractProductInventory(limitedQuantityProductOption);
    assertThat(productInventory.getQuantity()).isZero();
  }

  private ProductInventory extractProductInventory(ProductOption productOption) {
    List<Product> products = productService.findAllByProductOptionIds(
        List.of(productOption.getId()));

    return products.stream()
        .flatMap(product -> product.getProductOptions().stream())
        .filter(option -> option.getId().equals(productOption.getId()))
        .findFirst()
        .map(ProductOption::getProductInventory)
        .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다."));
  }

  @DisplayName("한정된 재고를 대상으로 동시에 재고를 차감하면 재고가 있으면 성공하고 재고가 없으면 실패한다")
  @Test
  void limitedQuantityReduceStockWithSuccessAndFailure() throws InterruptedException {
    // given
    Map<Long, Long> productOptionIdToAmount = Map.of(limitedQuantityProductOption.getId(),
        limitedQuantityProductOption.getProductInventory().getQuantity());
    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          ProductDeductCommand productDeductCommand = new ProductDeductCommand(
              productOptionIdToAmount);
          productService.deductInventory(productDeductCommand);

          successCount.incrementAndGet();
        } catch (Exception e) {
          failureCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }

    latch.await();

    // then
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failureCount.get()).isEqualTo(1);
  }

  @DisplayName("이전 1시간 동안 판매된 상품을 기준으로 상위 5개 상품을 저장한다")
  @Test
  void saveTop5SellingProductsBefore() {
    // given
    LocalDateTime from = LocalDateTime.now().minusDays(1).minusHours(1);
    LocalDateTime to = LocalDateTime.now().minusDays(1);

    // when
    productService.saveTop5SellingProductsBefore(from, to);

    // then
    Top5SellingProducts top5SellingProducts = productService.findTop5SellingProducts();
    assertThat(top5SellingProducts.topSellingProducts()).isNotEmpty();
  }
}
