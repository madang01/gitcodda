package kr.pe.codda.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.io.StreamBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.ReadWriteMode;

public abstract class CommonStaticUtil {
	public static final Base64.Encoder Base64Encoder = Base64.getEncoder();
	public static final Base64.Decoder Base64Decoder = Base64.getDecoder();

	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다. 주의점) 주어진 문자열이 빈 문자열일 경우 true 를 던진다.
	 * 
	 * @param value
	 *            앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부
	 * @throws IllegalArgumentException
	 *             null 주어진 문자열이 null 인 경우 던진다.
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value) throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}

		String trimValue = value.trim();
		boolean returnValue = !trimValue.equals(value);

		return returnValue;
	}

	public static String getFilePathStringFromResourcePathAndRelativePathOfFile(String resourcesPathString,
			String relativePath) {
		if (null == resourcesPathString) {
			throw new IllegalArgumentException("the paramter resourcesPathString is null");
		}

		if (null == relativePath) {
			throw new IllegalArgumentException("the paramter relativePath is null");
		}

		String realResourceFilePathString = null;

		String headSeparator = null;
		if (relativePath.indexOf("/") == 0)
			headSeparator = "";
		else
			headSeparator = File.separator;

		String subRealPathString = null;
		if (File.separator.equals("/")) {
			subRealPathString = relativePath;
		} else {
			subRealPathString = relativePath.replaceAll("/", "\\\\");
		}

		realResourceFilePathString = new StringBuilder(resourcesPathString).append(headSeparator)
				.append(subRealPathString).toString();

		return realResourceFilePathString;
	}

	/**
	 * 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열을 반환한다.
	 * 
	 * @param sourceString
	 *            변환을 원하는 문자열
	 * @param lineSeparatorType
	 *            지정한 칼럼 마다 삽입을 원하는 문자열 구분, BR: <br/>
	 *            , NEWLINE: newline
	 * @param wantedColumnSize
	 *            원하는 문자열 가로 칼럼수
	 * @return 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열
	 */
	public static String splitString(String sourceString, LineSeparatorType lineSeparatorType, int wantedColumnSize) {
		if (null == sourceString) {
			throw new IllegalArgumentException("the paramter sourceString is null");
		}

		if (sourceString.equals("")) {
			throw new IllegalArgumentException("the paramter sourceString is a empty string");
		}

		if (hasLeadingOrTailingWhiteSpace(sourceString)) {
			throw new IllegalArgumentException("the paramter sourceString has leading or tailing white space");
		}

		if (null == lineSeparatorType) {
			throw new IllegalArgumentException("the paramter lineSeparatorGubun is null");
		}

		if (wantedColumnSize <= 0) {
			throw new IllegalArgumentException("the paramter wantedColumnSize is less or equals to zero");
		}

		String lineSeparator = null;
		if (lineSeparatorType == LineSeparatorType.BR) {
			lineSeparator = "<br/>";
		} else {
			lineSeparator = CommonStaticFinalVars.NEWLINE;
		}

		int size = sourceString.length();
		StringBuilder resultStringBuilder = new StringBuilder();
		int i = 0;
		for (; i + wantedColumnSize < size; i += wantedColumnSize) {
			resultStringBuilder.append(sourceString.substring(i, i + wantedColumnSize));
			resultStringBuilder.append(lineSeparator);
		}
		resultStringBuilder.append(sourceString.substring(i));
		return resultStringBuilder.toString();
	}

	public static String getMultiLineToolTip(String message, int colSize) {
		if (null == message) {
			throw new IllegalArgumentException("the parameter 'message' is null");
		}

		String tooltip = new StringBuilder("<html>")
				.append(CommonStaticUtil.splitString(message, LineSeparatorType.BR, colSize)).append("</html>")
				.toString();
		return tooltip;
	}

	public static void copyTransferToFile(File sourceFile, File targetFile) throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException("the parameter 'sourceFile' is null");
		}

		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);

			FileChannel souceFileChannel = fis.getChannel();
			FileChannel targetFileChannel = fos.getChannel();

			souceFileChannel.transferTo(0, souceFileChannel.size(), targetFileChannel);
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "fail to close the file[{}] input stream", targetFile.getAbsolutePath());
			}
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (Exception e) {
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}

	public static File getValidPath(String sourcePathString, ReadWriteMode readWriteMode) throws RuntimeException {
		if (null == sourcePathString) {
			throw new IllegalArgumentException("the parameter 'sourcePathString' is null");
		}

		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s] doesn't exist", sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s] is not a directory", sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (readWriteMode.equals(ReadWriteMode.ONLY_READ) || readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canRead()) {
				String errorMessage = String.format("The path[%s] has a permission to read", sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}

		if (readWriteMode.equals(ReadWriteMode.ONLY_WRITE) || readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canWrite()) {
				String errorMessage = String.format("The path[%s] has a permission to write", sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		return sourcePath;
	}

	public static void createNewFile(File targetFile, String contents, Charset targetCharset)
			throws FileNotFoundException, IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}

		boolean isSuccess = targetFile.createNewFile();
		if (!isSuccess) {
			String errorMessage = String.format("the file[%s] exist", targetFile.getAbsolutePath());
			throw new FileNotFoundException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = String.format("the file[%s] is not a regular file", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format("the file[%s] can not be written", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream",
				// fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}

	public static void overwriteFile(File targetFile, String contents, Charset targetCharset) throws IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}

		if (!targetFile.exists()) {
			String errorMessage = String.format("the file[%s] doesn't exist", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = String.format("the file[%s] is not a regular file", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format("the file[%s] can not be written", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile, false);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream",
				// fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, "fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}

	public static void saveFile(File targetFile, String contents, Charset targetCharset) throws IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}

		if (targetFile.exists()) {
			overwriteFile(targetFile, contents, targetCharset);
		} else {
			createNewFile(targetFile, contents, targetCharset);
		}
	}

	public static String getPrefixWithTabCharacters(int depth, int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String.format("the parameter numberOfAdditionalTabs[%d] is less than zero",
					numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}
		StringBuilder stringBuilder = new StringBuilder();

		addPrefixWithTabCharacters(stringBuilder, depth, numberOfAdditionalTabs);

		return stringBuilder.toString();
	}

	public static void addPrefixWithTabCharacters(StringBuilder contentsStringBuilder, int depth,
			int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String.format("the parameter numberOfAdditionalTabs[%d] is less than zero",
					numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}

		int numberOfTabCharacters = depth + numberOfAdditionalTabs;
		for (int i = 0; i < numberOfTabCharacters; i++) {
			contentsStringBuilder.append("\t");
		}
	}

	/**
	 * 낱글자 한글 포함한 한글 여부를 반환한다
	 * 
	 * @param c
	 * @return 낱글자 한글 포함한 한글 여부
	 */
	public static boolean isHangul(final char c) {
		boolean isHangul = false;
		if (c >= 'ㄱ' && c <= 'ㅎ') {
			isHangul = true;
		} else if (c >= 'ㅏ' && c <= 'ㅣ') {
			isHangul = true;
		} else if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	/**
	 * 초성 중성 종성이 다 갖추어진'가' 에서 '힣' 까지의 한글 여부를 반환한다
	 * 
	 * @param c
	 * @return 초성 중성 종성이 다 갖추어진'가' 에서 '힣' 까지의 한글 여부
	 */
	public static boolean isFullHangul(final char c) {
		boolean isHangul = false;
		if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	public static boolean isEnglish(final char c) {
		boolean isAlphabet = ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		
		return isAlphabet;
	}

	

	/**
	 * Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isPunct(final char c) {
		boolean isPunct = false;

		if (c >= '!' && c <= '/') {
			isPunct = true;
		} else if (c >= ':' && c <= '@') {
			isPunct = true;
		} else if (c >= '[' && c <= '`') {
			isPunct = true;
		} else if (c >= '{' && c <= '~') {
			isPunct = true;
		}
		return isPunct;
	}

	/**
	 * SPACE(0x20) or TAB('\t') 문자 여부를 반환
	 * 
	 * @param c
	 * @return SPACE(0x20) or TAB('\t') 문자 여부
	 */
	public static boolean isSpaceOrTab(char c) {
		boolean isWhiteSpace = false;

		if ((' ' == c) || ('\t' == c)) {
			isWhiteSpace = true;
		}

		return isWhiteSpace;
	}

	/**
	 * 개행 문자('\r' or '\n') 여부를 반환
	 * 
	 * @param c
	 * @return 개행 문자('\r' or '\n') 여부
	 */
	public static boolean isLineSeparator(char c) {
		boolean isLineSeparator = false;
		// || ('\u0085' == c) || ('\u2028' == c) || ('\u2029' == c)
		if (('\r' == c) || ('\n' == c)) {
			isLineSeparator = true;
		}

		return isLineSeparator;
	}

	public static boolean isEnglishAndDigit(String sourceString) {
		for (char c : sourceString.toCharArray()) {
			if (! isEnglish(c) && ! Character.isDigit(c)) {
				return false;
			}
		}
		
		return true;
	}

	public static boolean isEnglishAndDigitWithRegular(String sourceString) {
		String regex = "[a-zA-Z0-9]+";

		boolean isValid = sourceString.matches(regex);
		return isValid;
	}

	public static byte[] readFileToByteArray(File sourceFile, long maxSize) throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException("the parameter sourceFile is null");
		}

		if (!sourceFile.exists()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[").append(sourceFile.getAbsolutePath())
					.append("] doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!sourceFile.isFile()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[").append(sourceFile.getAbsolutePath())
					.append("] is not a normal file").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!sourceFile.canRead()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[").append(sourceFile.getAbsolutePath())
					.append("] can't be read").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (sourceFile.length() > maxSize) {
			String errorMessage = new StringBuilder("the parameter sourceFile[").append(sourceFile.getAbsolutePath())
					.append("]'s size[").append(sourceFile.length()).append("] is greater than max[").append(maxSize)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		byte[] result = new byte[(int) sourceFile.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sourceFile);
			fis.read(result);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}

		return result;
	}

	public static Object getNewObjectFromClassloader(ClassLoader targetClassLoader, String classFullName)
			throws DynamicClassCallException {
		Class<?> retClass = null;

		try {
			retClass = targetClassLoader.loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			String errorMessage = new StringBuilder().append("fail to find the class[").append(classFullName)
					.append("], errmsg=").append(e.getMessage()).toString();
			throw new DynamicClassCallException(errorMessage);
		}

		Object retObject = null;
		try {
			retObject = retClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, InstantiationException errmsg=").append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, IllegalAccessException errmsg=").append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, IllegalArgumentException errmsg=").append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, InvocationTargetException errmsg=").append(targetException.getMessage())
					.toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, targetException);

			throw new DynamicClassCallException(errorMessage);
		} catch (NoSuchMethodException e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, NoSuchMethodException errmsg=").append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (SecurityException e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, SecurityException errmsg=").append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("the classloader[").append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ").append(classFullName)
					.append(" class, unknwon error errmsg=").append(e.getMessage()).toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		}

		return retObject;
	}
	
	public static AbstractMessage O2M(AbstractMessageDecoder messageDecoder, SingleItemDecoderIF singleItemDecoder,  int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws BodyFormatException {
		if (null == messageDecoder) {
			throw new IllegalArgumentException("the parameter messageDecoder is null");
		}
		
		if (null == singleItemDecoder) {
			throw new IllegalArgumentException("the parameter singleItemDecoder is null");
		}
		
		if (null == messageID) {
			throw new IllegalArgumentException("the parameter messageID is null");
		}

		if (null == readableMiddleObject) {
			throw new IllegalArgumentException("the parameter readableMiddleObject is null");
		}
		
		AbstractMessage outputMessage = null;
		try {
			outputMessage = messageDecoder.decode(singleItemDecoder, readableMiddleObject);
			outputMessage.messageHeaderInfo.mailboxID = mailboxID;
			outputMessage.messageHeaderInfo.mailID = mailID;
		} catch (BodyFormatException e) {
			/*
			 * String errorMessage = new
			 * StringBuilder("fail to decode the var 'readableMiddleObject' of the output message"
			 * ) .append("mailboxID=") .append(mailboxID) .append(", mailID=")
			 * .append(mailID) .append(", messageID=") .append(messageID)
			 * .append("], errmsg=") .append("") .append(e.getMessage()) .toString();
			 * 
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.log(Level.WARNING, errorMessage);
			 * 
			 * SelfExnRes selfExnRes = new SelfExnRes();
			 * selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
			 * selfExnRes.messageHeaderInfo.mailID = mailID;
			 * selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
			 * selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class))
			 * ;
			 * 
			 * selfExnRes.setErrorMessageID(messageID);
			 * selfExnRes.setErrorReason(errorMessage);
			 * 
			 * return selfExnRes;
			 */
			throw e;
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder("unknow error::fail to decode the var 'readableMiddleObject' of the output message")
					.append("mailboxID=")
					.append(mailboxID)
					.append(", mailID=")
					.append(mailID)
					.append(", messageID=")
					.append(messageID)
					.append("], errmsg=")
					.append("")
					.append(e.getMessage())
					.toString();
			
			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage);
			
			/*
			 * Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			 * log.log(Level.WARNING, errorMessage);
			 * 
			 * SelfExnRes selfExnRes = new SelfExnRes();
			 * selfExnRes.messageHeaderInfo.mailboxID = mailboxID;
			 * selfExnRes.messageHeaderInfo.mailID = mailID;
			 * selfExnRes.setErrorPlace(SelfExn.ErrorPlace.CLIENT);
			 * selfExnRes.setErrorType(SelfExn.ErrorType.valueOf(BodyFormatException.class))
			 * ;
			 * 
			 * selfExnRes.setErrorMessageID(messageID);
			 * selfExnRes.setErrorReason(errorMessage);
			 * 
			 * return selfExnRes;
			 * 
			 */
			throw new BodyFormatException(errorMessage);
		} finally {
			if (readableMiddleObject instanceof StreamBuffer) {
				StreamBuffer sb = (StreamBuffer)readableMiddleObject;
				try {
					sb.close();
				} catch(Exception e) {
					String errorMessage = new StringBuilder()
							.append("fail to close the message body stream[messageID=")
							.append(messageID)
							.append(", mailboxID=")
							.append(mailboxID)
							.append(", mailID=")
							.append(mailID)
							.append("] body stream").toString();
					Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
					log.log(Level.WARNING, errorMessage, e);
				}
			}
		}
		
		outputMessage.messageHeaderInfo.mailboxID = mailboxID;
		outputMessage.messageHeaderInfo.mailID = mailID;
		return outputMessage;
	}
}
