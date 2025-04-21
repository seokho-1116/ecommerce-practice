package kr.hhplus.be.server.common;

import java.lang.reflect.Field;

public class TestReflectionUtil {

  public static void setField(Object obj, String fieldName, Object value) {
    try {
      var field = getField(obj, obj.getClass(), fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Field getField(Object obj, Class<?> clazz, String fieldName) {
    try {
      if (clazz == Object.class) {
        throw new IllegalArgumentException("해당 필드를 찾을 수 없습니다. fieldName: " + fieldName);
      }

      return clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      if (obj.getClass().getSuperclass() == Object.class) {
        throw new RuntimeException(e);
      }

      return getField(obj, clazz.getSuperclass(), fieldName);
    }
  }
}
