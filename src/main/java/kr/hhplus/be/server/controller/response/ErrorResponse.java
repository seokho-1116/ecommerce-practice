package kr.hhplus.be.server.controller.response;

public record ErrorResponse(
    String errorCode,
    String message,
    String errorDescription
) {

}