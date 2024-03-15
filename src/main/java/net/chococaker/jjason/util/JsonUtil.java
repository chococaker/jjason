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
package net.chococaker.jjason.util;

import net.chococaker.jjason.JsonElement;
import net.chococaker.jjason.JsonPrimitive;
import net.chococaker.jjason.exception.MalformedJsonException;

import java.text.StringCharacterIterator;

/**
 * JSON utility methods.
 *
 * @since 1.0
 */
public final class JsonUtil {
    private JsonUtil() {
    }
    
    /**
     * Escapes an ordinary String into a string that can be contained by quotation marks in JSON.
     * <p>
     * Ex. [breakline] -> \n
     *
     * @param s The unescaped string
     * @return The escaped string
     */
    public static String escape(String s) {
        StringBuilder returned = new StringBuilder();
        
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\n':
                    returned.append("\\n");
                    break;
                case '\t':
                    returned.append("\\t");
                    break;
                case '\b':
                    returned.append("\\b");
                    break;
                case '\f':
                    returned.append("\\f");
                    break;
                case '\"':
                    returned.append("\\\"");
                    break;
                case '\\':
                    returned.append("\\\\");
                    break;
                
                default:
                    if (isUnicode(c)) {
                        if (c <= 0xf) {
                            returned.append("\\u000");
                        } else if (c <= 0xff) {
                            returned.append("\\u00");
                        } else if (c <= 0xfff) {
                            returned.append("\\u0");
                        } else {
                            returned.append("\\u");
                        }
                        returned.append(Integer.toHexString(c));
                    } else {
                        returned.append(c);
                    }
                    break;
            }
        }
        
        return returned.toString();
    }
    
    private static final byte UNICODE_LENGTH = 4; // the length of the actual part escaped unicode part
    
    /**
     * Converts an escaped JSON string to a normal Java string.
     * <p>
     * Ex. \n -> [breakline]
     *
     * @param s The escaped string
     * @return The unescaped string
     * @throws IllegalArgumentException When an unexpected character or escape is encountered
     */
    public static String unescape(String s) {
        StringCharacterIterator iter = new StringCharacterIterator(s);
        
        StringBuilder sb = new StringBuilder();
        
        while (iter.getIndex() < iter.getEndIndex()) {
            char c = iter.current();
            requireNonDone(s, c);
            
            if (c == '\n') {
                throw new IllegalArgumentException("Unexpected breakline in string");
            }
            
            if (isUnicode(c)) {
                throw new IllegalArgumentException("Unexpected unicode in string '" + s + "'");
            }
            
            if (c == '\\') {
                c = iter.next();
                requireNonDone(s, c);
                switch (c) {
                    case '\\':
                    case '"':
                    case '\'':
                    case '/':
                        break;
                    
                    case 'b':
                        c = '\b';
                        break;
                    case 'f':
                        c = '\f';
                        break;
                    case 'n':
                        c = '\n';
                        break;
                    case 'r':
                        c = '\r';
                        break;
                    case 't':
                        c = '\t';
                        break;
                    
                    case 'u': // unicode
                        StringBuilder unicodePart = new StringBuilder();
                        for (int i = 0; i < UNICODE_LENGTH; i++) {
                            char next = iter.next();
                            requireNonDone(s, next);
                            unicodePart.append(next);
                        }
                        
                        c = (char) Integer.parseInt(unicodePart.toString(), 16);
                        break;
                    
                    default: {
                        throw new IllegalArgumentException("Unexpected escape for char '" + c + "' at index " + iter.getIndex());
                    }
                }
            }
            
            sb.append(c);
            iter.next();
        }
        
        return sb.toString();
    }
    
    /**
     * Swaps out a null {@link JsonElement} with {@link JsonPrimitive#NULL}.
     */
    public static JsonElement convertNull(JsonElement element) {
        return element == null ? JsonPrimitive.NULL : element;
    }
    
    // prevent unexpected errors when encountering end-of-file
    private static void requireNonDone(String json, char c) {
        if (c == StringCharacterIterator.DONE)
            throw MalformedJsonException.eof(json);
    }
    
    private static boolean isUnicode(char c) {
        return Character.UnicodeBlock.of(c) != Character.UnicodeBlock.BASIC_LATIN;
    }
}
