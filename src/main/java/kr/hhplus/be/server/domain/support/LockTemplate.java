package kr.hhplus.be.server.domain.support;

import java.util.function.Supplier;

public interface LockTemplate {

  <T> T execute(Supplier<T> supplier, LockCommand lockCommand);
}
