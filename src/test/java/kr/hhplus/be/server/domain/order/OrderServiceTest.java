package kr.hhplus.be.server.domain.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderCommand.ProductAmountPair;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderService orderService;

  @DisplayName("주문 상품은 주문 생성 시 저장되어야 한다.")
  @Test
  void createOrderItemTest() {
    // given
    User user = User.builder()
        .build();

    Product product = Product.builder()
        .id(1L)
        .name("test")
        .basePrice(1000L)
        .description("test")
        .build();

    ProductOption productOption = ProductOption.builder()
        .id(1L)
        .name("test")
        .additionalPrice(100L)
        .description("test")
        .product(product)
        .build();

    long amount = 5L;
    ProductAmountPair productAmountPair = new ProductAmountPair(product, productOption, amount);

    OrderCommand orderCommand = new OrderCommand(
        user,
        List.of(productAmountPair),
        null
    );

    // when
    orderService.createOrder(orderCommand);

    // then
    verify(orderRepository, atLeastOnce()).save(any(Order.class));
  }
}