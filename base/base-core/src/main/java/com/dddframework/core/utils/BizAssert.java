package com.dddframework.core.utils;

import com.dddframework.core.contract.IR;
import com.dddframework.core.contract.enums.ResultCode;
import com.dddframework.core.contract.exception.ServiceException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 业务断言类，断言不通过将抛出ServiceException
 */
@UtilityClass
public class BizAssert {
    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, ServiceException::new);
     * </pre>
     *
     * @param <X>        异常类型
     * @param expect     布尔值
     * @param falseThrow 指定断言不通过时抛出的异常
     * @throws X if expect is {@code false}
     */
    public <X extends Throwable> void isTrue(Boolean expect, Supplier<? extends X> falseThrow) throws X {
        if (!expect) {
            throw falseThrow.get();
        }
    }

    public void isTrue(Boolean expect, Integer code, String falseThrow, Object... params) {
        if (!expect) {
            throw new ServiceException(code, falseThrow, params);
        }
    }

    public void isTrue(Boolean expect, String ifFalse, Object... params) {
        if (!expect) {
            throw new ServiceException(ifFalse, params);
        }
    }

    public void equals(Object a, Object b, Integer code, String ifNotEquals, Object... params) {
        if (!Objects.equals(a, b)) {
            throw new ServiceException(code, ifNotEquals, params);
        }
    }

    public void equals(Object a, Object b, String ifNotEquals, Object... params) {
        if (!Objects.equals(a, b)) {
            throw new ServiceException(ifNotEquals, params);
        }
    }

    public void equals(Object a, Object b) {
        if (!Objects.equals(a, b)) {
            throw new ServiceException("a must equals with b");
        }
    }

    public void notEquals(Object a, Object b, Integer code, String ifEquals, Object... params) {
        if (Objects.equals(a, b)) {
            throw new ServiceException(code, ifEquals, params);
        }
    }

    public void notEquals(Object a, Object b, String ifEquals, Object... params) {
        if (Objects.equals(a, b)) {
            throw new ServiceException(ifEquals, params);
        }
    }

    public void notEquals(Object a, Object b) {
        if (Objects.equals(a, b)) {
            throw new ServiceException("a must not equals with b");
        }
    }

    public void contains(Collection collection, Object element, Integer code, String ifNotContains, Object... params) {
        if (collection == null || !collection.contains(element)) {
            throw new ServiceException(code, ifNotContains, params);
        }
    }

    public void contains(Collection collection, Object element, String ifNotContains, Object... params) {
        if (collection == null || !collection.contains(element)) {
            throw new ServiceException(ifNotContains, params);
        }
    }

    public void contains(Collection collection, Object element) {
        if (collection == null || !collection.contains(element)) {
            throw new ServiceException("collection must contains the element");
        }
    }

    public void notContains(Collection collection, Object element, Integer code, String ifContains, Object... params) {
        if (collection != null && collection.contains(element)) {
            throw new ServiceException(code, ifContains, params);
        }
    }

    public void notContains(Collection collection, Object element, String ifContains, Object... params) {
        if (collection != null && collection.contains(element)) {
            throw new ServiceException(ifContains, params);
        }
    }

    public void notContains(Collection collection, Object element) {
        if (collection != null && collection.contains(element)) {
            throw new ServiceException("collection must contains the element");
        }
    }

    public void after(LocalDateTime a, LocalDateTime b, Integer code, String ifBefore, Object... params) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException(code, ifBefore, params);
        }
    }

    public void after(LocalDateTime a, LocalDateTime b, String ifBefore, Object... params) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException(ifBefore, params);
        }
    }

    public void after(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException("a must after b");
        }
    }

    public void after(LocalDate a, LocalDate b, Integer code, String ifBefore, Object... params) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException(code, ifBefore, params);
        }
    }

    public void after(LocalDate a, LocalDate b, String ifBefore, Object... params) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException(ifBefore, params);
        }
    }

    public void after(LocalDate a, LocalDate b) {
        if (a == null || b == null || !a.isAfter(b)) {
            throw new ServiceException("a must after b");
        }
    }

    public void gt(Integer a, Integer b, Integer code, String ifLessEquals, Object... params) {
        if (a == null || b == null || a <= b) {
            throw new ServiceException(code, ifLessEquals, params);
        }
    }

    public void gt(Integer a, Integer b, String ifLessEquals, Object... params) {
        if (a == null || b == null || a <= b) {
            throw new ServiceException(ifLessEquals, params);
        }
    }

    public void gt(Integer a, Integer b) {
        if (a == null || b == null || a <= b) {
            throw new ServiceException("a must > b");
        }
    }

    public void ge(Integer a, Integer b, Integer code, String ifLess, Object... params) {
        if (a == null || b == null || a < b) {
            throw new ServiceException(code, ifLess, params);
        }
    }

    public void ge(Integer a, Integer b, String ifLess, Object... params) {
        if (a == null || b == null || a < b) {
            throw new ServiceException(ifLess, params);
        }
    }

    public void ge(Integer a, Integer b) {
        if (a == null || b == null || a < b) {
            throw new ServiceException("a must >= b");
        }
    }

    public void ge(BigDecimal a, BigDecimal b, Integer code, String ifLess, Object... params) {
        if (a == null || b == null || a.compareTo(b) < 0) {
            throw new ServiceException(code, ifLess, params);
        }
    }

    public void ge(BigDecimal a, BigDecimal b, String ifLess, Object... params) {
        if (a == null || b == null || a.compareTo(b) < 0) {
            throw new ServiceException(ifLess, params);
        }
    }

    public void ge(BigDecimal a, BigDecimal b) {
        if (a == null || b == null || a.compareTo(b) < 0) {
            throw new ServiceException("a must >= b");
        }
    }

    public void gt(BigDecimal a, BigDecimal b, Integer code, String ifLess, Object... params) {
        if (a == null || b == null || a.compareTo(b) <= 0) {
            throw new ServiceException(code, ifLess, params);
        }
    }

    public void gt(BigDecimal a, BigDecimal b, String ifLess, Object... params) {
        if (a == null || b == null || a.compareTo(b) <= 0) {
            throw new ServiceException(ifLess, params);
        }
    }

    public void gt(BigDecimal a, BigDecimal b) {
        if (a == null || b == null || a.compareTo(b) <= 0) {
            throw new ServiceException("a must > b");
        }
    }

    public <T> T notNull(T dontNull, Integer code, String ifNull, Object... params) {
        if (dontNull == null) {
            throw new ServiceException(code, ifNull, params);
        }
        return dontNull;
    }

    public <T> T notNull(T dontNull, String ifNull, Object... params) {
        if (dontNull == null) {
            throw new ServiceException(ifNull, params);
        }
        return dontNull;
    }

    public <T> T notNull(T dontNull) {
        if (dontNull == null) {
            throw new ServiceException("this object must not be null");
        }
        return dontNull;
    }

    public <T> void isNull(T nullVal, Integer code, String ifNotNull, Object... params) {
        if (nullVal != null) {
            throw new ServiceException(code, ifNotNull, params);
        }
    }

    public <T> void isNull(T nullVal, String ifNotNull, Object... params) {
        if (nullVal != null) {
            throw new ServiceException(ifNotNull, params);
        }
    }

    public <T> void isNull(T nullVal) {
        if (nullVal != null) {
            throw new ServiceException("this object must be null");
        }
    }

    public void notBlank(String dontBlank, Integer code, String ifBlank, Object... params) {
        if (dontBlank == null || dontBlank.length() == 0) {
            throw new ServiceException(code, ifBlank, params);
        }
    }

    public void notBlank(String dontBlank, String ifBlank, Object... params) {
        if (dontBlank == null || dontBlank.length() == 0) {
            throw new ServiceException(ifBlank, params);
        }
    }

    public void notBlank(String dontBlank) {
        if (dontBlank == null || dontBlank.length() == 0) {
            throw new ServiceException("this string must not be null or blank");
        }
    }

    public void isBlank(String blankVal, Integer code, String ifNotBlank, Object... params) {
        if (blankVal != null && blankVal.length() != 0) {
            throw new ServiceException(code, ifNotBlank, params);
        }
    }

    public void isBlank(String blankVal, String ifNotBlank, Object... params) {
        if (blankVal != null && blankVal.length() != 0) {
            throw new ServiceException(ifNotBlank, params);
        }
    }

    public void isBlank(String blankVal) {
        if (blankVal != null && blankVal.length() != 0) {
            throw new ServiceException("this string must be null or blank");
        }
    }

    public <E, T extends Iterable<E>> T notEmpty(T dontEmpty, Integer code, String ifEmpty, Object... params) {
        if (dontEmpty == null || !dontEmpty.iterator().hasNext()) {
            throw new ServiceException(code, ifEmpty, params);
        }
        return dontEmpty;
    }

    public <E, T extends Iterable<E>> T notEmpty(T dontEmpty, String ifEmpty, Object... params) {
        if (dontEmpty == null || !dontEmpty.iterator().hasNext()) {
            throw new ServiceException(ifEmpty, params);
        }
        return dontEmpty;
    }

    public <E, T extends Iterable<E>> T notEmpty(T dontEmpty) {
        if (dontEmpty == null || !dontEmpty.iterator().hasNext()) {
            throw new ServiceException("this collection must not be null or empty");
        }
        return dontEmpty;
    }

    public <E, T extends Iterable<E>> void isEmpty(T emptyVal, Integer code, String ifNotEmpty, Object... params) {
        if (emptyVal != null && emptyVal.iterator().hasNext()) {
            throw new ServiceException(code, ifNotEmpty, params);
        }
    }

    public <E, T extends Iterable<E>> void isEmpty(T emptyVal, String ifNotEmpty, Object... params) {
        if (emptyVal != null && emptyVal.iterator().hasNext()) {
            throw new ServiceException(ifNotEmpty, params);
        }
    }

    public <E, T extends Iterable<E>> void isEmpty(T emptyVal) {
        if (emptyVal != null && emptyVal.iterator().hasNext()) {
            throw new ServiceException("this collection must not be null or empty");
        }
    }

    public <T> T[] notEmpty(T[] dontEmpty, Integer code, String ifEmpty, Object... params) {
        if (dontEmpty == null || dontEmpty.length == 0) {
            throw new ServiceException(code, ifEmpty, params);
        }
        return dontEmpty;
    }

    public <T> T[] notEmpty(T[] objects, String ifEmpty, Object... params) {
        if (objects == null || objects.length == 0) {
            throw new ServiceException(ifEmpty, params);
        }
        return objects;
    }

    public <T> T[] notEmpty(T[] dontEmpty) {
        if (dontEmpty == null || dontEmpty.length == 0) {
            throw new ServiceException("this arrays must not be null or empty");
        }
        return dontEmpty;
    }

    public <T> T[] isEmpty(T[] emptyVal, Integer code, String ifNotEmpty, Object... params) {
        if (emptyVal == null || emptyVal.length != 0) {
            throw new ServiceException(code, ifNotEmpty, params);
        }
        return emptyVal;
    }

    public <T> void isEmpty(T[] emptyVal, String ifNotEmpty, Object... params) {
        if (emptyVal == null || emptyVal.length != 0) {
            throw new ServiceException(ifNotEmpty, params);
        }
    }

    public <T> void isEmpty(T[] emptyVal) {
        if (emptyVal == null || emptyVal.length != 0) {
            throw new ServiceException("this arrays must be null or empty");
        }
    }

    public <T> T isOk(IR r, Integer code, String ifNotOk, Object... params) {
        if (r == null || !r.isOk()) {
            throw new ServiceException(code, ifNotOk, params);
        }
        return r.getData();
    }

    public <T> T isOk(IR r, String ifNotOk, Object... params) {
        if (r == null || !r.isOk()) {
            throw new ServiceException(ifNotOk, params);
        }
        return r.getData();
    }

    public <T> T isOk(IR r, Supplier<String> notOk) {
        if (r == null || !r.isOk()) {
            throw new ServiceException(notOk.get());
        }
        return r.getData();
    }

    public <T> T isOk(IR r) {
        if (r == null || !r.isOk()) {
            throw new ServiceException(r == null ? "this result must not be null" : r.getMsg());
        }
        return r.getData();
    }

    public <T> T notNull(IR r, Integer code, String ifDataNull, Object... params) {
        if (r == null || !r.isOk() || r.getData() == null) {
            throw new ServiceException(code, ifDataNull, params);
        }
        return r.getData();
    }

    public <T> T notNull(IR r, String ifDataNull, Object... params) {
        if (r == null || !r.isOk() || r.getData() == null) {
            throw new ServiceException(ifDataNull, params);
        }
        return r.getData();
    }

    public <T> T notNull(IR r, Supplier<String> whenDataNull) {
        if (r == null || !r.isOk() || r.getData() == null) {
            throw new ServiceException(whenDataNull.get());
        }
        return r.getData();
    }

    public <T> T notNull(IR r) {
        if (r == null || !r.isOk() || r.getData() == null) {
            throw new ServiceException(r == null ? "this result must not be null" : r.getMsg());
        }
        return r.getData();
    }
}
