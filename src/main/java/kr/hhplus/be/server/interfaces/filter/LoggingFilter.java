package kr.hhplus.be.server.interfaces.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
          logRequest(requestWrapper);
          filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logResponse(responseWrapper, duration);

            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String ip = request.getRemoteAddr();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String requestBody = getContent(request.getContentAsByteArray());

        log.info("[REQUEST] IP: {}, Method: {}, URL: {}, Body: {}", ip, method, url, requestBody);
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String responseBody = getContent(response.getContentAsByteArray());

        log.info("[RESPONSE] Duration: {}ms, Status: {}, Body: {}", duration, status, responseBody);
    }

    private String getContent(byte[] content) {
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "[EMPTY]";
    }
}