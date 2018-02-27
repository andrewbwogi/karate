/*
 * The MIT License
 *
 * Copyright 2017 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import static com.intuit.karate.ScriptValue.Type.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author pthomas3
 */
public class ScriptValueTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ScriptValueTest.class);
   
    @Test
    public void testTypeDetection() {
        DocumentContext doc = JsonPath.parse("{ foo: 'bar' }");
        ScriptValue sv = new ScriptValue(doc);
        assertEquals(JSON, sv.getType());
        doc = JsonPath.parse("[1, 2]");
        sv = new ScriptValue(doc);
        assertEquals(JSON, sv.getType());  
        Object temp = doc.read("$");
        assertTrue(temp instanceof List);
    }
    @Test
    public void testGetAsStringWhenNull() {
        // The getAsString-method should return a JSON-formatted string.
        // This json-string should be null when the ScriptValue-instance
        // was created with the 2-arguments-constructor, with the first argument
        // equal to null.
        ScriptValue sv = new ScriptValue(null, "test");
        assertNull( sv.getAsString() );
    }


    
}
