package kr.hhplus.be.server;

import kr.hhplus.be.server.common.TestHelpRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {

  @Autowired
  protected TestHelpRepository testHelpRepository;

  @AfterEach
  void tearDown() {
    testHelpRepository.cleanup();
    testHelpRepository.cleanCache();
  }
}
