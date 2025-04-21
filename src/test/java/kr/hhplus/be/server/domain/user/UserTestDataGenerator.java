package kr.hhplus.be.server.domain.user;

import static org.instancio.Select.field;

import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class UserTestDataGenerator {

  public User user() {
    return Instancio.of(User.class)
        .ignore(field(User::getId))
        .set(field(User::getIsActive), true)
        .ignore(field(User::getCreatedAt))
        .ignore(field(User::getUpdatedAt))
        .create();
  }
}
