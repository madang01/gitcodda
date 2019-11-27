package kr.pe.codda.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {
	public String getHead(Handler h) {
		return "Codda Log Start\n";
	}

	public String format(LogRecord rec) {
		Thread currentThread = Thread.currentThread();
		
		
		
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

		buf.append("(");
		buf.append(rec.getSourceClassName());
		buf.append("#");
		buf.append(rec.getSourceMethodName());
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
