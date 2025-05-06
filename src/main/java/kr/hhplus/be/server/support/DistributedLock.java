package kr.hhplus.be.server.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

  CacheKey key();

  String expression();

  long timeout() default 3000L;

  TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}


