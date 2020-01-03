/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

/**
 * 출처 : http://stackoverflow.com/questions/3619796/how-to-read-a-properties-file-in-java-in-the-original-order
 * 저자 : Wayne Johnson
 * 참고 : stackoverflow 사이트에서 "Wayne Johnson" 님 일시 Oct 24 '12 at 15:08 에 답글
 */
package kr.pe.codda.common.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("serial")
public class SequencedProperties extends Properties {

    @SuppressWarnings("rawtypes")
	private List keyList = new ArrayList();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public synchronized Enumeration keys() {
        return Collections.enumeration(keyList);
    }

    @SuppressWarnings("unchecked")
    public synchronized Object put(Object key, Object value) {
        if (! containsKey(key)) {
            keyList.add(key);
        }

        return super.put(key, value);
    }

    @Override
    public synchronized Object remove(Object key) {
        keyList.remove(key);

        return super.remove(key);
    }

    
	@Override
	@SuppressWarnings("unchecked")
    public synchronized void putAll(@SuppressWarnings("rawtypes") Map values) {
        for (Object key : values.keySet()) {
            if (! containsKey(key)) {
                keyList.add(key);
            }
        }

        super.putAll(values);
    }
    
    @Override
    public void store(Writer writer, String title) throws IOException {
    	writer.append("# ");
    	writer.append(title);
    	writer.append(System.getProperty("line.separator"));
    	
    	writer.append("# ");
    	writer.append(new java.util.Date().toString());
    	writer.append(System.getProperty("line.separator"));
    	
    	for (Object key : keyList) {
    		writer.append((String)key);
    		writer.append("=");
    		String tempValue = getProperty((String)key);
    		writer.append(tempValue.replaceAll("\\\\", "\\\\\\\\"));
    		writer.append(System.getProperty("line.separator"));
    	}
    	
    	writer.close();
    }
}