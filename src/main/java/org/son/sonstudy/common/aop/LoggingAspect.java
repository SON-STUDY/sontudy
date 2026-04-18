package org.son.sonstudy.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.son.sonstudy.common.aop.annotation.Loggable;
import org.son.sonstudy.common.aop.annotation.LogCategory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        LogCategory category = loggable.category();
        long startTime = System.currentTimeMillis();

        String argsLog = loggable.includeArgs() ? buildArgsLog(joinPoint, method) : "";

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            String resultLog = loggable.includeResult() ? " result=" + result : "";
            log.info("[{}] {} 메서드 실행 성공. 실행시간={}ms{}{}",
                    category.getDescription(),
                    methodName,
                    executionTime,
                    argsLog,
                    resultLog);

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] {} 메서드 실행 실패. 실행시간={}ms{} exception={}",
                    category.getDescription(),
                    methodName,
                    executionTime,
                    argsLog,
                    ex.getMessage());
            throw ex;
        }
    }

    private String buildArgsLog(ProceedingJoinPoint joinPoint, Method method) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            return "";
        }

        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        StringBuilder sb = new StringBuilder(" args=[");

        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(paramNames[i]).append("=").append(formatArg(args[i], paramNames[i]));
        }

        sb.append("]");
        return sb.toString();
    }

    private String formatArg(Object arg, String paramName) {
        // 민감한 파라미터명을 감지하면 마스킹
        if (isSensitiveParam(paramName)) {
            return "[MASKED]";
        }

        if (arg == null) {
            return "null";
        }
        if (arg instanceof String) {
            return "\"" + arg + "\"";
        }
        if (arg instanceof byte[]) {
            return "[byte array]";
        }
        return arg.toString();
    }

    private boolean isSensitiveParam(String paramName) {
        String lower = paramName.toLowerCase();
        return lower.contains("password")
                || lower.contains("passwd")
                || lower.contains("secret")
                || lower.contains("token")
                || lower.contains("credential")
                || lower.contains("authorization");
    }
}
