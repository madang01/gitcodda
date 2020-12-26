/*
Copyright 2013, Won Jonghoon

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package kr.codda.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import kr.codda.main.HelperServer;

/**
 * @author Won Jonghoon
 *
 */
public abstract class CommonStaticUtil {
	public static final Charset PAGE_CHARSET = Charset.forName("UTF-8"); 
	
	
	public static String readErrorPageContents() throws IOException {
		
		return readPageContents("/webapp/error.html");
	}
	
	
	public static String readPageContents(String fileURL) throws IOException {
		
		String content = null;
		InputStream is = HelperServer.class.getResourceAsStream(fileURL);
		try {
			
			int size = is.available();

			byte[] buffer = new byte[size];

			is.read(buffer);
			
			content = new String(buffer, PAGE_CHARSET);
		} finally {
			if (null != is) {
				is.close();
			}
		}
		return content;
	}
}
