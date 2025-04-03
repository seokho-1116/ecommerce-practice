package kr.hhplus.be.server.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.List;
import kr.hhplus.be.server.controller.response.ChargePointResponse;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.OrderPaymentRequest;
import kr.hhplus.be.server.controller.response.OrderPaymentResponse;
import kr.hhplus.be.server.controller.response.OrderRequest;
import kr.hhplus.be.server.controller.response.OrderRequest.AmountProductOptionRequest;
import kr.hhplus.be.server.controller.response.OrderResponse;
import kr.hhplus.be.server.service.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class E2EOrderTest {

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
    RestAssured.baseURI = "http://localhost";
  }

  @DisplayName("잔액 충전부터 주문까지 E2E 테스트")
  @Test
  void order() {
    // 1. 잔액 충전
    long userId = 1L;
    long point = 10000L;

    CommonResponseWrapper<ChargePointResponse> chargePointResponseWrapper = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(point)
        .when()
        .pathParam("userId", userId)
        .post("/api/v1/point/{userId}/charge")
        .then()
        .statusCode(200)
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(chargePointResponseWrapper.data().amount()).isEqualTo(point);

    // 2. 주문
    long productOptionId = 1L;
    long couponId = 1L;
    long amount = 100L;
    OrderRequest orderRequest = new OrderRequest(userId, List.of(
        new AmountProductOptionRequest(productOptionId, amount, couponId)
    ));

    CommonResponseWrapper<OrderResponse> orderResponseWrapper = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(orderRequest)
        .when()
        .post("/api/v1/orders")
        .then()
        .statusCode(200)
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(orderResponseWrapper.data().status()).isEqualTo(OrderStatus.CREATED);

    // 3. 결제
    OrderPaymentRequest paymentRequest = new OrderPaymentRequest(userId);

    CommonResponseWrapper<OrderPaymentResponse> orderPaymentResponseWrapper = RestAssured.given()
        .contentType(ContentType.JSON)
        .body(paymentRequest)
        .when()
        .pathParam("orderId", orderResponseWrapper.data().orderId())
        .post("/api/v1/orders/{orderId}/payments")
        .then()
        .statusCode(200)
        .extract()
        .response().as(new TypeRef<>() {
        });

    assertThat(orderPaymentResponseWrapper.data().status()).isEqualTo(OrderStatus.PAID);
  }
}
