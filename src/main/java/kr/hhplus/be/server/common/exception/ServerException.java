package kr.hhplus.be.server.common.exception;

public class ServerException extends RuntimeException {

  public ServerException(String message) {
    super(message);
  }

  public ServerException(Throwable cause) {
    super(cause);
  }
}
