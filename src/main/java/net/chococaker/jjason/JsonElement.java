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

import java.io.Serializable;

/**
 * Represents JSON value, which can be either a {@link JsonPrimitive}, {@link JsonObject}, or
 * {@link JsonArray}.
 *
 * @since 1.0
 */
public interface JsonElement extends Cloneable, Serializable {
    /**
     * @return A deep clone of the element
     */
    JsonElement clone();
    
    /**
     * Performs a simple <code>instanceof</code> check to see if the {@link JsonElement} is a
     * {@link JsonObject}
     *
     * @return Whether this object is a {@link JsonObject}
     */
    default boolean isJsonObject() {
        return this instanceof JsonObject;
    }
    
    /**
     * Performs a simple <code>instanceof</code> check to see if the {@link JsonElement} is a
     * {@link JsonArray}
     *
     * @return Whether this object is a {@link JsonArray}
     */
    default boolean isJsonArray() {
        return this instanceof JsonArray;
    }
    
    /**
     * Performs a simple <code>instanceof</code> check to see if the {@link JsonElement} is a
     * {@link JsonPrimitive}
     *
     * @return Whether this object is a {@link JsonPrimitive}
     */
    default boolean isJsonPrimitive() {
        return this instanceof JsonPrimitive;
    }
    
    /**
     * @return This object cast to a {@link JsonObject}
     * @throws ClassCastException If the cast was not able to be performed.
     */
    default JsonObject getAsJsonObject() {
        return (JsonObject) this;
    }
    
    /**
     * @return This object cast to a {@link JsonArray}
     * @throws ClassCastException If the cast was not able to be performed.
     */
    default JsonArray getAsJsonArray() {
        return (JsonArray) this;
    }
    
    /**
     * @return This object cast to a {@link JsonPrimitive}
     * @throws ClassCastException If the cast was not able to be performed.
     */
    default JsonPrimitive getAsJsonPrimitive() {
        return (JsonPrimitive) this;
    }
    
    /**
     * @return a JSON value-safe {@link String} representation of the object. In other words, it is
     * safe to put this value after the key of a JSON datapoint: <code>"key":[toString]</code>
     */
    @Override
    String toString();
}
