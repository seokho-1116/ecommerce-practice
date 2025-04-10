package kr.hhplus.be.server.domain.user;

public class UserBusinessException extends RuntimeException {

  public static class UserNotFoundException extends UserBusinessException {

  }
}
