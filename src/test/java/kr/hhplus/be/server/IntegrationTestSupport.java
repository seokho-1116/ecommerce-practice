package kr.hhplus.be.server;

import kr.hhplus.be.server.common.TestHelpRepository;
import kr.hhplus.be.server.domain.coupon.CouponTestDataGenerator;
import kr.hhplus.be.server.domain.order.OrderTestDataGenerator;
import kr.hhplus.be.server.domain.product.ProductTestDataGenerator;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {

  @Autowired
  protected OrderTestDataGenerator orderTestDataGenerator;

  @Autowired
  protected UserTestDataGenerator userTestDataGenerator;

  @Autowired
  protected ProductTestDataGenerator productTestDataGenerator;

  @Autowired
  protected CouponTestDataGenerator couponTestDataGenerator;

  @Autowired
  protected TestHelpRepository testHelpRepository;

  @AfterEach
  void tearDown() {
    testHelpRepository.cleanup();
    testHelpRepository.cleanCache();
  }
}
