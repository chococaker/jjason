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
package net.chococaker.jjason.reader;

import net.chococaker.jjason.JsonArray;
import net.chococaker.jjason.JsonElement;
import net.chococaker.jjason.JsonObject;
import net.chococaker.jjason.JsonPrimitive;
import net.chococaker.jjason.exception.MalformedJsonException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static net.chococaker.jjason.util.JsonUtil.unescape;

/**
 * Reads a {@link CharSequence} and parses it into a {@link T}.
 *
 * @param <T> The type of JsonElement.
 * @see CharSequence
 * @see JsonObject
 * @since 1.0
 */
public abstract class JsonReader<T extends JsonElement> {
    public JsonReader(CharSequence json) {
        this.json = json.toString();
        this.iter = new JsonStringIterator(this.json);
    }
    
    // all tokens
    protected static final char BLOCK_OPEN = '{';
    protected static final char ARRAY_OPEN = '[';
    protected static final char BLOCK_CLOSE = '}';
    protected static final char ARRAY_CLOSE = ']';
    protected static final char COLON = ':';
    protected static final char COMMA = ',';
    
    protected static final Set<Character> TOKENS = Collections.unmodifiableSet(new HashSet<Character>() {{
        add(BLOCK_OPEN); add(BLOCK_CLOSE);
        add(ARRAY_OPEN); add(ARRAY_CLOSE);
        add(COLON);
        add(COMMA);
        add('t'); add('f'); // boolean
        add('n'); // null
        add('0'); add('1'); add('2'); add('3'); add('4'); add('5'); add('6'); add('7'); add('8'); add('9'); // numbers
        add('"'); // strings
    }});
    
    protected static final String NULL_STRING = "null";
    
    protected final String json;
    protected final JsonStringIterator iter;
    
    protected final StringBuilder sb = new StringBuilder();
    
    /**
     * @return A {@link JsonElement} parsed in accordance to
     * <a href="https://json-schema.org/specification">2020-12 JSON Specifications</a>.
     * @see <a href="https://json-schema.org/specification">2020-12 JSON Specifications</a>
     */
    public abstract T read();
    
    /**
     * @return A {@link JsonReader} for a {@link JsonArray}
     */
    public static JsonReader<JsonArray> arrayReader(String arrayJson) {
        return new JsonArrayReader(arrayJson);
    }
    
    /**
     * @return A {@link JsonReader} for a {@link JsonObject}
     */
    public static JsonReader<JsonObject> objectReader(String objectJson) {
        return new JsonObjectReader(objectJson);
    }
    
    protected JsonPrimitive readBoolean() {
        boolean expectedBoolean = (iter.current() == 't'); // can only be true or false
        String booleanString = Boolean.toString(expectedBoolean);
        int index = 0;
        while (true) {
            index++;
            char c = iter.next();
            if (booleanString.length() - 1 < index) {
                return new JsonPrimitive(expectedBoolean);
            }
            
            char expectedChar = booleanString.charAt(index);
            if (expectedChar != c) {
                throw new MalformedJsonException(json, iter.current(), c, iter.getIndex());
            }
        }
    }
    
    protected JsonPrimitive readNumber() {
        char c = iter.current();
        while (true) {
            if (!isDigit(c)
                    && (c < 'a' || c > 'f')
                    && (c < 'A' || c > 'F') // also contains scientific notation 'E'
                    && c != '+'
                    && c != '-'
                    && c != '.') {
                if (c == BLOCK_CLOSE || c == COMMA || isWhitespace(c)) {
                    iter.back();
                    break;
                } else {
                    throw new MalformedJsonException(json, c, iter.getIndex());
                }
            }
            sb.append(c);
            c = iter.next();
        }
        
        String sbString = sb.toString();
        sb.setLength(0);
        try { // TODO: improve this system!
            int i = Integer.parseInt(sbString);
            return new JsonPrimitive(i);
        } catch (NumberFormatException ignored) {
            try {
                double d = Double.parseDouble(sbString);
                sb.setLength(0);
                return new JsonPrimitive(d);
            } catch (NumberFormatException ignored$) {
                try {
                    long l = Long.parseLong(sbString);
                    return new JsonPrimitive(l);
                } catch (NumberFormatException ignored$$) {
                    try {
                        BigInteger i = new BigInteger(sbString);
                        return new JsonPrimitive(i);
                    } catch (NumberFormatException ignored$$$) {
                        try {
                            BigDecimal d = new BigDecimal(sbString);
                            return new JsonPrimitive(d);
                        } catch (NumberFormatException ignored$$$$) {
                            throw new MalformedJsonException(json, "Invalid number: '" + sbString + "'");
                        }
                    }
                }
            }
        }
    }
    
    protected JsonPrimitive readString() {
        iter.next();
        
        while (iter.current() != '"') {
            char c = iter.current();
            if (c == '\\') {
                c = iter.next();
            }
            
            sb.append(c);
            iter.next();
        }
        
        String done = sb.toString();
        sb.setLength(0);
        try {
            return new JsonPrimitive(unescape(done));
        } catch (IllegalArgumentException e) {
            throw new MalformedJsonException(json, e.getMessage());
        }
    }
    
    protected JsonPrimitive readNull() {
        char c = iter.current();
        for (int i = 0; i < NULL_STRING.length(); i++) {
            char expected = NULL_STRING.charAt(i);
            if (c != expected) {
                throw new MalformedJsonException(json, c, expected, iter.getIndex());
            }
            
            if (i < NULL_STRING.length() - 1) { // do not execute on last iteration
                c = iter.next();
            }
        }
        
        return JsonPrimitive.NULL;
    }
    
    protected JsonArray readArray() {
        next();
        JsonArray array = new JsonArray();
        
        char c = iter.current();
        while (true) {
            if (c == 't' || c == 'f') {
                array.add(readBoolean());
            } else if (c == 'n') {
                array.add(readNull());
            } else if (c == '"') {
                array.add(readString());
            } else if (c == ARRAY_OPEN) {
                array.add(readArray());
            } else if (c == BLOCK_OPEN) {
                array.add(read());
            } else {
                // all invalid characters are handled here
                // so no need for extra handling
                array.add(readNumber());
            }
            
            next();
            c = iter.current();
            if (c == ARRAY_CLOSE) {
                return array;
            } else if (c != COMMA) {
                throw new MalformedJsonException(json, c, iter.getIndex());
            }
            next();
            c = iter.current();
        }
    }
    
    protected JsonObject readObject() {
        next();
        JsonObject jsonObject = new JsonObject();
        char c = iter.current();
        
        while (c != BLOCK_CLOSE) {
            String key = (String) readString().get();
            if (jsonObject.get(key) != null) {
                throw new MalformedJsonException(json, "Duplicate keys '" + readString() + '\'');
            }
            next();
            c = iter.current();
            if (c != COLON) {
                throw new MalformedJsonException(json, c, COLON, iter.getIndex());
            }
            next();
            c = iter.current();
            if (c == 't' || c == 'f') {
                jsonObject.set(key, readBoolean());
            } else if (c == 'n') {
                jsonObject.set(key, readNull());
            } else if (c == '"') {
                jsonObject.set(key, readString());
            } else if (c == ARRAY_OPEN) {
                jsonObject.set(key, readArray());
            } else if (c == BLOCK_OPEN) {
                jsonObject.set(key, readObject());
            } else {
                // all invalid characters are handled here
                // so no need for extra handling
                jsonObject.set(key, readNumber());
            }
            
            next();
            c = iter.current();
            
            if (c == BLOCK_CLOSE) {
                return jsonObject;
            }
            if (c != COMMA) {
                throw new MalformedJsonException(json, c, iter.getIndex());
            }
            
            next();
        }
        
        return jsonObject;
    }
    
    // go to next token/value starter
    protected void next() {
        char c = iter.next();
        while (isWhitespace(c)) {
            c = iter.next();
        }
        
        if (!TOKENS.contains(c)) {
            throw new MalformedJsonException(json, c, iter.getIndex());
        }
    }
}
