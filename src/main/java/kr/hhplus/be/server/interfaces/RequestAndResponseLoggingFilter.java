package kr.hhplus.be.server.interfaces;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {

  private static final String MASKING = "*******";

  private static final List<MediaType> VISIBLE_TYPES = List.of(
      MediaType.valueOf("text/*"),
      MediaType.APPLICATION_FORM_URLENCODED,
      MediaType.APPLICATION_JSON,
      MediaType.APPLICATION_XML,
      MediaType.valueOf("application/*+json"),
      MediaType.valueOf("application/*+xml"),
      MediaType.MULTIPART_FORM_DATA
  );

  private static final List<String> SENSITIVE_HEADERS = List.of(
      HttpHeaders.AUTHORIZATION,
      HttpHeaders.PROXY_AUTHENTICATE
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (isAsyncDispatch(request)) {
      filterChain.doFilter(request, response);
    } else {
      doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
    }
  }

  protected void doFilterWrapped(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response, FilterChain filterChain)
      throws ServletException, IOException {

    StringBuilder msg = new StringBuilder();

    try {
      beforeRequest(request, msg);
      filterChain.doFilter(request, response);
    } finally {
      afterRequest(request, response, msg);
      if (log.isInfoEnabled()) {
        log.info(msg.toString());
      }
      response.copyBodyToResponse();
    }
  }

  protected void beforeRequest(ContentCachingRequestWrapper request, StringBuilder msg) {
    if (log.isInfoEnabled()) {
      msg.append("\n-- REQUEST --\n");
      logRequestHeader(request, request.getRemoteAddr() + "|>", msg);
    }
  }

  protected void afterRequest(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response, StringBuilder msg) {
    if (log.isInfoEnabled()) {
      logRequestBody(request, request.getRemoteAddr() + "|>", msg);
      msg.append("\n-- RESPONSE --\n");
      logResponse(response, request.getRemoteAddr() + "|<", msg);
    }
  }

  private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix,
      StringBuilder msg) {
    String queryString = request.getQueryString();
    if (queryString == null) {
      String notQueryString = prefix + " " + request.getMethod() + " " + request.getRequestURI();
      msg.append(notQueryString)
          .append("\n");
    } else {
      String withQueryString =
          prefix + " " + request.getMethod() + " " + request.getRequestURI() + "?" +
              queryString;
      msg.append(withQueryString).append("\n");
    }

    Collections.list(request.getHeaderNames())
        .forEach(headerName ->
            Collections.list(request.getHeaders(headerName))
                .forEach(headerValue -> {
                  if (isSensitiveHeader(headerName)) {
                    String sensitiveHeader = prefix + " " + headerName + ": " + MASKING;
                    msg.append(sensitiveHeader)
                        .append("\n");
                  } else {
                    String header = prefix + " " + headerName + ": " + headerValue;
                    msg.append(header)
                        .append("\n");
                  }
                }));
    msg.append(prefix).append("\n");
  }

  private static void logRequestBody(ContentCachingRequestWrapper request, String prefix,
      StringBuilder msg) {
    byte[] content = request.getContentAsByteArray();
    if (content.length > 0) {
      logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix, msg);
    }
  }

  private static void logResponse(ContentCachingResponseWrapper response, String prefix,
      StringBuilder msg) {
    int status = response.getStatus();
    String statusText = prefix + " " + status + " " + HttpStatus.valueOf(status).getReasonPhrase();
    msg.append(statusText)
        .append("\n");
    response.getHeaderNames()
        .forEach(headerName ->
            response.getHeaders(headerName)
                .forEach(headerValue ->
                {
                  if (isSensitiveHeader(headerName)) {
                    String sensitiveHeader = prefix + " " + headerName + ": " + MASKING;
                    msg.append(sensitiveHeader)
                        .append("\n");
                  } else {
                    String headerValueString = prefix + " " + headerName + ": " + headerValue;
                    msg.append(headerValueString)
                        .append("\n");
                  }
                }));
    msg.append(prefix).append("\n");
    byte[] content = response.getContentAsByteArray();
    if (content.length > 0) {
      logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix, msg);
    }
  }

  private static void logContent(byte[] content, String contentType, String contentEncoding,
      String prefix, StringBuilder msg) {
    MediaType mediaType = MediaType.valueOf(contentType);
    boolean visible = VISIBLE_TYPES.stream()
        .anyMatch(visibleType -> visibleType.includes(mediaType));
    if (visible) {
      try {
        String contentString = new String(content, contentEncoding);
        Stream.of(contentString.split("\r\n|\r|\n"))
            .forEach(line -> msg.append(prefix).append(" ").append(line).append("\n"));
      } catch (UnsupportedEncodingException e) {
        String bytesLength = prefix + " [" + content.length + " bytes content]";
        msg.append(bytesLength).append("\n");
      }
    } else {
      String bytesLength = prefix + " [" + content.length + " bytes content]";
      msg.append(bytesLength).append("\n");
    }
  }

  private static boolean isSensitiveHeader(String headerName) {
    return SENSITIVE_HEADERS.contains(headerName);
  }

  private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper) {
      return (ContentCachingRequestWrapper) request;
    } else {
      return new ContentCachingRequestWrapper(request);
    }
  }

  private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
    if (response instanceof ContentCachingResponseWrapper) {
      return (ContentCachingResponseWrapper) response;
    } else {
      return new ContentCachingResponseWrapper(response);
    }
  }
}