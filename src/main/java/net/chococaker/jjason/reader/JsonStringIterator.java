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

import net.chococaker.jjason.exception.MalformedJsonException;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

// a StringCharacterIterator except it throws a MalformedJsonException
// when next() is called instead of returning '\uFFFF' (EOF char)
public class JsonStringIterator implements CharacterIterator {
    public JsonStringIterator(String json) {
        this(json, new StringCharacterIterator(json));
    }
    
    private JsonStringIterator(String json, StringCharacterIterator iter) {
        this.json = json;
        this.iter = iter;
    }
    
    public static final char DONE = '\uFFFF';
    
    private final String json;
    private final StringCharacterIterator iter;
    
    @Override
    public char next() {
        char c = iter.next();
        if (c == DONE) {
            throw MalformedJsonException.eof(json);
        }
        return c;
    }
    
    public char back() {
        iter.setIndex(iter.getIndex() - 1);
        return iter.current();
    }
    
    @Override
    public char previous() {
        return iter.previous();
    }
    
    @Override
    public char setIndex(int position) {
        return iter.setIndex(position);
    }
    
    @Override
    public int getBeginIndex() {
        return iter.getBeginIndex();
    }
    
    @Override
    public int getEndIndex() {
        return iter.getEndIndex();
    }
    
    @Override
    public char first() {
        return iter.first();
    }
    
    @Override
    public char last() {
        return iter.last();
    }
    
    @Override
    public char current() {
        return iter.current();
    }
    
    @Override
    public int getIndex() {
        return iter.getIndex();
    }
    
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "RedundantSuppression"})
    public JsonStringIterator clone() {
        return new JsonStringIterator(json, (StringCharacterIterator) iter.clone());
    }
}
