/*
 * Copyright 2023 choco
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

import java.util.*;
import java.util.stream.Collectors;

import static net.chococaker.jjason.util.JsonUtil.convertNull;
import static net.chococaker.jjason.util.JsonUtil.escape;

/**
 * Represents a tree of {@link JsonElement JsonElements}.
 *
 * @since 1.0
 */
public final class JsonObject implements JsonElement, Iterable<Map.Entry<String, JsonElement>> {
    public JsonObject() {
    }
    
    public JsonObject(Map<String, JsonElement> values) {
        this.values.putAll(values);
    }
    
    private static final long serialVersionUID = 548123L;
    
    private final Map<String, JsonElement> values = new HashMap<>();
    
    /**
     * @return A set of values associated with the {@link JsonObject}.
     */
    public Set<String> keySet() {
        return values.keySet();
    }
    
    /**
     * @param key The key for the desired value
     * @return The {@link JsonElement} of the specified key
     */
    public JsonElement get(String key) {
        return values.get(key);
    }
    
    /**
     * @param key The key for the desired value
     * @return The {@link JsonPrimitive} of the specified key
     */
    public JsonPrimitive getAsJsonPrimitive(String key) {
        return get(key).getAsJsonPrimitive();
    }
    
    /**
     * @param key The key for the desired value
     * @return The {@link JsonObject} of the specified key
     */
    public JsonObject getAsJsonObject(String key) {
        return get(key).getAsJsonObject();
    }
    
    /**
     * @param key The key for the desired value
     * @return The {@link JsonArray} of the specified key
     */
    public JsonObject getAsJsonArray(String key) {
        return get(key).getAsJsonObject();
    }
    
    /**
     * Sets the value at a key. Does NOT perform clone when setting the value.
     *
     * @param key   The key
     * @param value The {@link JsonElement} value
     * @return TRUE if there was a value at that key before this operation.
     */
    public boolean set(String key, JsonElement value) {
        return values.put(key, convertNull(value)) == null;
    }
    
    /**
     * @param key The key
     * @return TRUE if a value was removed.
     */
    public boolean remove(String key) {
        return values.remove(key) != null;
    }
    
    /**
     * @return a JSON value-safe {@link String} representation of the object. In other words, it is
     * safe to put this value after the key of a JSON datapoint: <code>"key":[toString]</code>
     * @throws StackOverflowError when a {@link JsonArray} or {@link JsonObject} in this collection
     *                            contains a reference to this object, or is this object
     */
    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append('{');
        
        String stringified = values.entrySet().stream()
                .map(e -> '"' + escape(e.getKey()) + "\":" + e.getValue())
                .collect(Collectors.joining(","));
        
        json.append(stringified)
                .append('}');
        
        return json.toString();
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "RedundantSuppression"})
    public JsonObject clone() {
        Map<String, JsonElement> cloneValues = new HashMap<>();
        
        for (Map.Entry<String, JsonElement> elem : this) {
            cloneValues.put(elem.getKey(), elem.getValue().clone());
        }
        
        return new JsonObject(cloneValues);
    }
    
    /**
     * @return An {@link Iterator} for the key-value pairs of the {@link JsonObject}
     * @see Iterator
     */
    @Override
    public Iterator<Map.Entry<String, JsonElement>> iterator() {
        return values.entrySet().iterator();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JsonObject other = (JsonObject) o;
        return Objects.equals(values, other.values);
    }
}
