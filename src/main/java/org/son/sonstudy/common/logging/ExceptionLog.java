package org.son.sonstudy.common.logging;

import net.logstash.logback.argument.StructuredArguments;
import java.util.ArrayList;
import java.util.List;

public final class ExceptionLog {

    private final String exceptionType;
    private final List<Object> args = new ArrayList<>();

    private ExceptionLog(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public static ExceptionLog of(String exceptionType) {
        return new ExceptionLog(exceptionType);
    }

    public ExceptionLog field(String key, Object value) {
        args.add(StructuredArguments.keyValue(key, String.valueOf(value)));
        return this;
    }

    public Object[] build() {
        args.add(StructuredArguments.keyValue("exceptionType", exceptionType));
        return args.toArray();
    }

    public Object[] buildWithThrowable(Throwable t) {
        args.add(StructuredArguments.keyValue("exceptionType", exceptionType));
        args.add(t);
        return args.toArray();
    }
}