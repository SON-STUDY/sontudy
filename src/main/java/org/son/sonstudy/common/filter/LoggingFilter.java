package org.son.sonstudy.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String traceId = generateTraceId();
        MDC.put(TRACE_ID_KEY, traceId);

        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = extractIp(request);
            String userId = extractUserId();

            log.info(
                    "[요청] {} {} {} {}",
                    StructuredArguments.keyValue("method", method),
                    StructuredArguments.keyValue("uri", uri),
                    StructuredArguments.keyValue("ip", ip),
                    StructuredArguments.keyValue("userId", userId)
            );

            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
            long startTime = System.currentTimeMillis();

            filterChain.doFilter(request, wrappedResponse);

            long duration = System.currentTimeMillis() - startTime;
            int status = wrappedResponse.getStatus();

            log.info(
                    "[응답] {} {}",
                    StructuredArguments.keyValue("status", status),
                    StructuredArguments.keyValue("executionTime", (int) duration)
            );

            wrappedResponse.copyBodyToResponse();
        } finally {
            MDC.clear();
        }
    }

    private String generateTraceId() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }

    private String extractIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserContext userContext) {
            return userContext.userId();
        }

        return "anonymous";
    }
}
