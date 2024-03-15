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
import net.chococaker.jjason.exception.MalformedJsonException;

final class JsonArrayReader extends JsonReader<JsonArray> {
    public JsonArrayReader(CharSequence json) {
        super(json);
    }
    
    @Override
    public JsonArray read() {
        if (iter.current() != ARRAY_OPEN) {
            next();
            if (iter.current() != ARRAY_OPEN)
                throw new MalformedJsonException(json, iter.current(), ARRAY_OPEN, iter.getIndex());
        }
        
        return readArray();
    }
}
