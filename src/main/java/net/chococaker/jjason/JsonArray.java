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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static net.chococaker.jjason.util.JsonUtil.convertNull;

/**
 * A {@link List} of {@link JsonElement JsonElements}.
 *
 * @see List
 * @see JsonElement
 * @since 1.0
 */
public final class JsonArray extends ArrayList<JsonElement> implements JsonElement {
    public JsonArray() {
        super();
    }
    
    public JsonArray(Collection<? extends JsonElement> elements) {
        super(elements);
    }
    
    private static final long serialVersionUID = 548123L;
    
    @Override
    public boolean add(JsonElement element) {
        return super.add(convertNull(element));
    }
    
    @Override
    public void add(int index, JsonElement element) {
        super.add(index, convertNull(element));
    }
    
    @Override
    public JsonElement set(int index, JsonElement element) {
        return super.set(index, convertNull(element));
    }
    
    public JsonPrimitive getAsJsonPrimitive(int index) {
        return get(index).getAsJsonPrimitive();
    }
    
    public JsonObject getAsJsonObject(int index) {
        return get(index).getAsJsonObject();
    }
    
    public JsonArray getAsJsonArray(int index) {
        return get(index).getAsJsonArray();
    }
    
    @Override
    public boolean addAll(Collection<? extends JsonElement> c) {
        requireNotSelf(c);
        requireNonCircularOrNullRefs(c);
        return super.addAll(c);
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends JsonElement> c) {
        requireNotSelf(c);
        requireNonCircularOrNullRefs(c);
        return super.addAll(index, c);
    }
    
    // UnaryOperator should never replace an element with null
    @Override
    public void replaceAll(UnaryOperator<JsonElement> operator) {
        super.replaceAll(operator);
        requireNonCircularOrNullRefs(this);
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "RedundantSuppression"})
    public JsonArray clone() {
        JsonArray cloneArray = new JsonArray();
        
        for (JsonElement elem : this) {
            cloneArray.add(elem.clone());
        }
        
        return cloneArray;
    }
    
    /**
     * @return a JSON value-safe {@link String} representation of the object. In other words, it is
     * safe to put this value after the key of a JSON datapoint: <code>"key":[toString]</code>
     * @throws StackOverflowError when a {@link JsonArray} or {@link JsonObject} in this collection
     *                            contains a reference to this object, or is this object
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('[');
        String listified = this.stream()
                .map(JsonElement::toString)
                .collect(Collectors.joining(","));
        result.append(listified);
        result.append(']');
        
        return result.toString();
    }
    
    private void requireNotSelf(Collection<?> c) {
        if (equals(c))
            throw new IllegalArgumentException("Statement results in circular reference");
    }
    
    // verifies that this collection does not contain null elements nor objects equal to this
    // collection
    private void requireNonCircularOrNullRefs(Collection<?> c) {
        if (c instanceof List<?>) {
            List<?> list = (List<?>) c;
            int indexOfNull = list.indexOf(null);
            if (indexOfNull != -1) {
                throw new NullPointerException("null list element at index " + indexOfNull);
            }
        } else if (c.contains(null)) {
            throw new NullPointerException("null collection element");
        } else if (c.contains(this)) {
            throw circularReferenceException();
        }
    }
    
    private static IllegalArgumentException circularReferenceException() {
        return new IllegalArgumentException("Statement results in circular reference");
    }
}
