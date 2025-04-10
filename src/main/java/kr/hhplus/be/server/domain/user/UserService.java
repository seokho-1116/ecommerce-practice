package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.UserBusinessException.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private UserRepository userRepository;

  public User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
  }
}
