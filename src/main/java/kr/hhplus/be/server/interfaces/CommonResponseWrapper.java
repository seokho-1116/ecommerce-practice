package kr.hhplus.be.server.interfaces;

public record CommonResponseWrapper<T>(
    String resultCode,
    String message,
    T data
) {

  public static <T> CommonResponseWrapper<T> ok(T data) {
    return new CommonResponseWrapper<>("00", "success", data);
  }
}
