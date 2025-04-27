package kr.hhplus.be.server.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import kr.hhplus.be.server.common.TestHelpRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponTestDataGenerator;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductTestDataGenerator;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.order.OrderRequest;
import kr.hhplus.be.server.interfaces.order.OrderRequest.AmountProductOptionRequest;
import kr.hhplus.be.server.interfaces.order.OrderResponse.OrderSuccessResponse;
import kr.hhplus.be.server.interfaces.payment.OrderPaymentRequest;
import kr.hhplus.be.server.interfaces.payment.OrderPaymentResponse;
import kr.hhplus.be.server.interfaces.point.PointResponse.ChargePointResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class E2EOrderTest {

  @Autowired
  private TestHelpRepository testHelpRepository;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  @Autowired
  private ProductTestDataGenerator productTestDataGenerator;

  @Autowired
  private CouponTestDataGenerator couponTestDataGenerator;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.baseURI = "http://localhost";

    User user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    Product product = productTestDataGenerator.product();
    testHelpRepository.save(product);

    List<ProductOption> productOptions = productTestDataGenerator.productOptions(5);
    for (ProductOption productOption : productOptions) {
      productOption.setupProduct(product);
      testHelpRepository.save(productOption);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      productInventory.setupProductOption(productOption);
      testHelpRepository.save(productInventory);
    }

    Coupon coupon = couponTestDataGenerator.validateCoupon();
    testHelpRepository.save(coupon);

    UserCoupon userCoupon = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    testHelpRepository.save(userCoupon);
  }

  @AfterEach
  void tearDown() {
    testHelpRepository.cleanup();
  }

  @DisplayName("잔액 충전부터 주문까지 E2E 테스트")
  @Test
  void order() {
    // 1. 잔액 충전
    long userId = 1L;
    long point = 10000000L;

    CommonResponseWrapper<ChargePointResponse> chargePointResponseWrapper = RestAssured.given()
        .basePath("/api/v1/point/{userId}/charge")
        .pathParam("userId", userId)
        .contentType(ContentType.JSON)
        .body(point)
        .when()
        .post()
        .then()
        .assertThat().statusCode(HttpStatus.OK.value())
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(chargePointResponseWrapper.data().amount()).isEqualTo(point);

    // 2. 주문
    long productId = 1L;
    long productOptionId = 1L;
    long amount = 100L;
    long userCouponId = 1L;
    OrderRequest orderRequest = new OrderRequest(userId, userCouponId, List.of(
        new AmountProductOptionRequest(productId, productOptionId, amount)
    ));

    CommonResponseWrapper<OrderSuccessResponse> orderResponseWrapper = RestAssured.given()
        .basePath("/api/v1/orders")
        .contentType(ContentType.JSON)
        .body(orderRequest)
        .when()
        .post()
        .then()
        .assertThat().statusCode(HttpStatus.OK.value())
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(orderResponseWrapper.data().orderId()).isNotNull();
    assertThat(orderResponseWrapper.data().status()).isEqualTo(OrderStatus.CREATED);

    // 3. 결제
    OrderPaymentRequest paymentRequest = new OrderPaymentRequest(userId);

    CommonResponseWrapper<OrderPaymentResponse> orderPaymentResponseWrapper = RestAssured.given()
        .basePath("/api/v1/payments/{orderId}")
        .pathParam("orderId", orderResponseWrapper.data().orderId())
        .contentType(ContentType.JSON)
        .body(paymentRequest)
        .when()
        .post()
        .then()
        .assertThat().statusCode(HttpStatus.OK.value())
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(orderPaymentResponseWrapper.data().amount()).isGreaterThanOrEqualTo(0L);
    assertThat(orderPaymentResponseWrapper.data().status()).isEqualTo(OrderStatus.PAID);
  }
}
