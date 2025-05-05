package kr.hhplus.be.server.domain.user;

public class UserDto {

  private UserDto() {}

  public record UserInfo(
      Long id,
      String name,
      String email
  ) {

    public static UserInfo from(User user) {
      return new UserInfo(
          user.getId(),
          user.getName(),
          user.getEmail()
      );
    }
  }
}
