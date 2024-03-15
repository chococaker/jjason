/*
 * Copyright 2023 chococaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.chococaker.jjason;

import net.chococaker.jjason.util.JsonUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A wrapper for JSON primitives.
 *
 * @see Integer
 * @see Long
 * @see Double
 * @see Boolean
 * @see String
 * @see BigInteger
 * @see BigDecimal
 * @since 1.0
 */
public final class JsonPrimitive implements JsonElement {
    public JsonPrimitive(int value) {
        this.object = value;
    }
    
    public JsonPrimitive(long value) {
        this.object = value;
    }
    
    public JsonPrimitive(double value) {
        this.object = value;
    }
    
    public JsonPrimitive(boolean value) {
        this.object = value;
    }
    
    public JsonPrimitive(String value) {
        this.object = value;
    }
    
    public JsonPrimitive(BigDecimal bigDecimal) {
        this.object = bigDecimal;
    }
    
    public JsonPrimitive(BigInteger bigInteger) {
        this.object = bigInteger;
    }
    
    private static final long serialVersionUID = 548123L;
    
    public static final JsonPrimitive NULL = new JsonPrimitive((BigInteger) null);
    
    private final Object object;
    
    @Override
    public int hashCode() {
        return Objects.hash(object);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JsonPrimitive jsonPrimitive = (JsonPrimitive) o;
        return Objects.equals(object, jsonPrimitive.object);
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "RedundantSuppression"})
    public JsonPrimitive clone() {
        return this;
    }
    
    /**
     * @return a JSON value-safe {@link String} representation of the object. In other words, it is
     * safe to put this value after the key of a JSON datapoint: <code>"key":[toString]</code>
     */
    @Override
    public String toString() {
        if (object instanceof String) {
            String s = (String) object;
            
            return "\"" + JsonUtil.escape(s) + "\"";
        } else {
            return toNonEscapedString();
        }
    }
    
    /**
     * @return A string that has not gone the escaping process to turn into valid JSON. The only
     * difference would be if the element held by the {@link JsonPrimitive} is a {@link String}.
     */
    public String toNonEscapedString() {
        return "" + object;
    }
    
    /**
     * @return A Java primitive type wrapper (int -> {@link Integer}), a big type ({@link BigInteger}
     * or {@link BigDecimal}), a {@link String}, or null.
     */
    public Object get() {
        return object;
    }
    
    public boolean isNumeric() {
        return object instanceof Integer
                || object instanceof Double
                || object instanceof Long
                || object instanceof BigDecimal
                || object instanceof BigInteger;
    }
    
    public boolean isJavaPrimitive() {
        return object instanceof Integer
                || object instanceof Double
                || object instanceof Long
                || object instanceof Boolean;
    }
    
    public boolean getAsBoolean() {
        return getAs(boolean.class);
    }
    
    public int getAsInt() {
        return getAs(int.class);
    }
    
    public double getAsDouble() {
        return getAs(double.class);
    }
    
    public long getAsLong() {
        return getAs(long.class);
    }
    
    public BigDecimal getAsBigDecimal() {
        return getAs(BigDecimal.class);
    }
    
    public BigInteger getAsBigInteger() {
        return getAs(BigInteger.class);
    }
    
    public String getAsString() {
        return getAs(String.class);
    }
    
    /**
     * Retrieves the wrapped object as type {@link T}.
     *
     * @see T
     */
    public <T> T getAs(Class<T> type) {
        try {
            return type.cast(object);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Wrapped object is not instance of " + type.getName());
        }
    }
    
    /**
     * @param clazz The type to check.
     * @return Whether the wrapped object is of a certain type.
     */
    public boolean holdsType(Class<?> clazz) {
        return clazz.isInstance(object);
    }
}
