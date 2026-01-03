package org.son.sonstudy.common.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.son.sonstudy.common.api.code.ErrorCode;
import org.son.sonstudy.common.exception.CustomException;
import org.son.sonstudy.common.jwt.component.JwtParser;
import org.son.sonstudy.common.jwt.component.JwtValidator;
import org.son.sonstudy.common.jwt.data.UserContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtValidator jwtValidator;
    private final JwtParser jwtParser;
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(
            JwtValidator jwtValidator,
            JwtParser jwtParser,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
    ) {
        this.jwtValidator = jwtValidator;
        this.jwtParser = jwtParser;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.equals("/api/user") || path.equals("/api/user/login");
        // TO DO: 인증 필요없는 앤드포인트 관리하는 클래스, 추후 개발 && SecurityConfig도 같이 변경
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String header = request.getHeader(AUTHORIZATION_HEADER);

            if (jwtValidator.isValidFormat(header)) {
                String token = header.substring(BEARER_PREFIX.length());

                jwtValidator.verifyToken(token);

                UserContext userContext = jwtParser.getUserContext(token);
                setAuthentication(userContext);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            exceptionResolver.resolveException(request, response, null, new CustomException(ErrorCode.INVALID_TOKEN_FORMAT));
        }
    }

    private void setAuthentication(UserContext userContext) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userContext,
                        null,
                        Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
