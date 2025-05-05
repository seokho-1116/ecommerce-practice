package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.UserBusinessException.UserNotFoundException;
import kr.hhplus.be.server.domain.user.UserDto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public User findUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
  }

  public UserInfo findUserInfoById(Long userId) {
    return userRepository.findById(userId)
        .map(UserInfo::from)
        .orElseThrow(UserNotFoundException::new);
  }
}
