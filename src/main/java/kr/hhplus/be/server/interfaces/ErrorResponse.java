package kr.hhplus.be.server.interfaces;

public record ErrorResponse(
    String errorCode,
    String message,
    String errorDescription
) {

}