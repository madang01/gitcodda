package codda2_core_all;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.CustomLogFormatter;

public class testLog {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		Handler handler = new ConsoleHandler();

		CustomLogFormatter formatter = new CustomLogFormatter();
		handler.setFormatter(formatter);

		rootLogger.setLevel(Level.INFO);
		rootLogger.addHandler(handler);

	}

	@Test
	public void testLog2() {
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

		String errorMessage = "한글 테스트3";

		log.log(Level.WARNING, errorMessage);

		errorMessage = "한글 테스트5";

		log.log(Level.WARNING, errorMessage);
		
		errorMessage = "한글 테스트6";
		
		log.log(Level.SEVERE, errorMessage, new Throwable());
	}
}
