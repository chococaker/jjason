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
package net.chococaker.jjason.exception;

import net.chococaker.jjason.reader.JsonReader;

/**
 * An exception thrown by {@link JsonReader} when malformed content is encountered in a JSON input.
 *
 * @since 1.0
 */
public class MalformedJsonException extends IllegalArgumentException {
    public MalformedJsonException(String json, String reason) {
        super(reason);
        this.col = -1;
        this.json = json;
    }
    
    public MalformedJsonException(String json, char unexpectedChar, int col) {
        super("Unexpected character '" + unexpectedChar + "' at col " + col);
        this.col = col;
        this.json = json;
    }
    
    public MalformedJsonException(String json, char unexpectedChar, char expectedChar, int col) {
        super("Unexpected character '" + unexpectedChar + "', expected '" + expectedChar + "' at col " + col);
        this.col = col;
        this.json = json;
    }
    
    /**
     * @return A new end-of-file exception
     */
    public static MalformedJsonException eof(String json) {
        return new MalformedJsonException("Reached end-of-file", json);
    }
    
    private final int col;
    private final String json;
    
    public String getJson() {
        return json;
    }
    
    /**
     * @return The error's column if it exists, -1 otherwise
     */
    public int getCol() {
        return col;
    }
}
