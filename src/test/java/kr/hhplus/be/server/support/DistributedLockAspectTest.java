package kr.hhplus.be.server.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.support.LockCommand;
import kr.hhplus.be.server.domain.support.LockTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private LockTemplate lockTemplate;

  @InjectMocks
  private DistributedLockAspect distributedLockAspect;

  @Test
  @DisplayName("AOP가 정상적으로 동작해야 한다")
  void testAroundDistributedLock() throws Throwable {
    // given
    Class<CouponFacade> paymentFacadeClass = CouponFacade.class;
    Method method = paymentFacadeClass.getMethod("issue", Long.class, Long.class);

    when(joinPoint.getSignature()).thenReturn(new MethodSignature() {
      @Override
      public Class getReturnType() {
        return null;
      }

      @Override
      public Method getMethod() {
        return method;
      }

      @Override
      public Class[] getParameterTypes() {
        return new Class[0];
      }

      @Override
      public String[] getParameterNames() {
        return Arrays.stream(method.getParameters())
            .map(Parameter::getName)
            .toArray(String[]::new);
      }

      @Override
      public Class[] getExceptionTypes() {
        return new Class[0];
      }

      @Override
      public String toShortString() {
        return "";
      }

      @Override
      public String toLongString() {
        return "";
      }

      @Override
      public String getName() {
        return "";
      }

      @Override
      public int getModifiers() {
        return 0;
      }

      @Override
      public Class getDeclaringType() {
        return null;
      }

      @Override
      public String getDeclaringTypeName() {
        return "";
      }
    });
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 1L});

    // when
    distributedLockAspect.aroundDistributedLock(joinPoint);

    // then
    verify(lockTemplate, atLeastOnce()).execute(any(), any(LockCommand.class));
  }
}