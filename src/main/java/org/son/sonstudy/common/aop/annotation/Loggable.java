package org.son.sonstudy.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    LogCategory category() default LogCategory.USER;
    boolean includeArgs() default true;
    boolean includeResult() default false;
}
