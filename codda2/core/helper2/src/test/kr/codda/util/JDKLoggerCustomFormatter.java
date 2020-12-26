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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Won Jonghoon
 *
 */
public class JDKLoggerCustomFormatter extends Formatter {
	public String getHead(Handler h) {
		return "Codda Log Start\n";
	}

	public String format(LogRecord rec) {
		Thread currentThread = Thread.currentThread();
		
		StackTraceElement[] stackTraceElements = currentThread.getStackTrace();
		
		int lineNumber = -1;
		String  fileName = "unknown.java";
		
		for (StackTraceElement ste : stackTraceElements) {
			
			
			String targetClassName = ste.getClassName();
			String targetMethodName = ste.getMethodName();
			
			if (targetClassName.equals(rec.getSourceClassName()) && targetMethodName.equals(rec.getSourceMethodName())) {
				fileName = ste.getFileName();
				lineNumber = ste.getLineNumber();
				break;
			}
			
		}
		
		
		
		StringBuffer buf = new StringBuffer(1000);

		buf.append(calcDate(rec.getMillis()));

		buf.append(" [");
		buf.append(rec.getLevel());
		buf.append("] ");

		buf.append(" [");
		buf.append(currentThread.getName());
		// buf.append(rec.getThreadID());
		buf.append("] ");

		buf.append(rec.getMessage());

		buf.append(" (");
		buf.append(fileName);
		buf.append(":");
		buf.append(lineNumber);
		buf.append(") ");

		buf.append("\n");
		
		Throwable e = rec.getThrown();

		if (null != e) {
			buf.append(e.getMessage());
			buf.append("\n");

			for (StackTraceElement se : e.getStackTrace()) {
				buf.append(se.toString());
				buf.append("\n");
			}
		}

		return buf.toString();
	}

	public String getTail(Handler h) {
		return "Codda Log Start\n";
	}

	private String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}
}
