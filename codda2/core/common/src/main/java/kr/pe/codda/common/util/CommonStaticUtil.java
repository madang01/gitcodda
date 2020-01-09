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

package kr.pe.codda.common.util;

import java.io.File;
import java.io.FileInputStream;
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
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.message.codec.AbstractMessageDecoder;
import kr.pe.codda.common.protocol.MessageProtocolIF;
import kr.pe.codda.common.protocol.SingleItemDecoderIF;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.ReadWriteMode;

/**
 * 정적인 공통 메소드들을 제공하는 유틸 클래스
 * 
 * @author Won Jonghoon
 *
 */
public abstract class CommonStaticUtil {
	public static final Base64.Encoder Base64Encoder = Base64.getEncoder();
	public static final Base64.Decoder Base64Decoder = Base64.getDecoder();

	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다, 단 빈 문자열의 경우 공백 문자가 미 포함한것으로 판단하여 거짓을 반환한다.
	 * 
	 * @param value 앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부, 단 빈 문자열의 경우 공백 문자가 미 포함한것으로 판단하여 거짓을 반환한다.
	 * @throws IllegalArgumentException 앞뒤로 공백 문자 여부을 알고 싶은 문자열이 null 인 경우 던지는 예외
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value) throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}

		char[] t = value.toCharArray();

		if (0 == t.length) {
			return false;
		}

		boolean hasLeadingOrTailingWhiteSpace = Character.isWhitespace(t[0]) || Character.isWhitespace(t[t.length - 1]);

		return hasLeadingOrTailingWhiteSpace;
	}

	/**
	 * @param resourcesPathString 리소스 경로 문자열, WARNING! 리소스 경로 문자열이 실제 운영체제에 존재하는지 검사 안함.
	 * @param relativePathString 상대 경로 문자열, 예) /dbcp or dbcp, WARNING! 상대 경로 문자열이 진짜로 상대 경로인지 검사 안함.
	 * @return 파라미터 'resourcesPathString' 기준 파라미터 'relativePath' 로 지정한 상대 경로에 있는  
	 */
	public static String buildFilePathStringFromResourcePathAndRelativePathOfFile(String resourcesPathString,
			String relativePathString) {
		if (null == resourcesPathString) {
			throw new IllegalArgumentException("the paramter resourcesPathString is null");
		}

		if (null == relativePathString) {
			throw new IllegalArgumentException("the paramter relativePathString is null");
		}

		if (relativePathString.isEmpty()) {
			throw new IllegalArgumentException("the paramter relativePathString is empty");
		}

		String prefix = "";

		String ch = relativePathString.substring(0, 1);
		if (!"/".equals(ch)) {
			prefix = File.separator;
		}		

		final String suffix;
		if (File.separator.equals("/")) {
			suffix = relativePathString;
		} else {
			suffix = relativePathString.replace("/", "\\");
		}

		String realResourceFilePathString = new StringBuilder(resourcesPathString).append(prefix)
				.append(suffix).toString();

		return realResourceFilePathString;
	}

	/**
	 * @param relativePathString 상대 경로 문자열, WARNING! 상대 경로 문자열이 진짜로 상대 경로인지 검사 안함.
	 * @return 상대 경로 문자열의 경로 구분을 운영 체제 경로 구분에 맞도록 수정한 문자열
	 */
	public static String toOSPathString(String relativePathString) {
		if (File.separator.equals("/")) {
			return relativePathString;
		} else {
			return relativePathString.replace("/", "\\");
		}
	}

	/**
	 * 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열을 반환한다.
	 * 
	 * @param sourceString      변환을 원하는 문자열
	 * @param lineSeparatorType 지정한 칼럼 마다 삽입을 원하는 문자열 구분, BR: <br>
	 *                          , NEWLINE: newline
	 * @param wantedColumnSize  원하는 문자열 가로 칼럼수
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


	/**
	 * 소스 파일를 목적지 파일로 복사한다. 참고) JAVA NIO {@link FileChannel#transferTo(long, long, java.nio.channels.WritableByteChannel)} 이용함.
	 * @param sourceFile 소스 파일
	 * @param targetFile 목적지 파일
	 * @throws IOException 입출력 에러시 던지는 예외
	 */
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

	/**
	 * 참고) 유효한 경로는 경로가 OS 에서 존재하는 디렉토리이어야 하고 파라미터 'readWriteMode' 로 지정한 파일 권한을 만족하는 경로를 뜻한다.
	 * 
	 * @param sourcePathString 경로 문자열
	 * @param readWriteMode 원하는 읽기 쓰기 모드, null 가능. null 일 경우 파일 권한 검사 생략함.
	 * @return 유효성이 검증된 경로
	 * @throws IllegalArgumentException 파라미터 'sourcePathString' 가 null 인 경우 혹은 유효하지 않는 경로인 경우 던지는 예외.  
	 * 
	 */
	public static File createValidPath(String sourcePathString, ReadWriteMode readWriteMode) throws IllegalArgumentException {
		if (null == sourcePathString) {
			throw new IllegalArgumentException("the parameter 'sourcePathString' is null");
		}
		

		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = new StringBuilder().append("the parameter sourcePathString[").append(sourcePathString)
					.append("] is path that does not exist").toString();
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = new StringBuilder().append("the parameter sourcePathString[").append(sourcePathString)
					.append("] is path that is not a directory").toString();
			throw new RuntimeException(errorMessage);
		}

		if (ReadWriteMode.ONLY_READ.equals(readWriteMode) || ReadWriteMode.READ_WRITE.equals(readWriteMode)) {
			if (! sourcePath.canRead()) {
				String errorMessage = new StringBuilder().append("the parameter sourcePathString[")
						.append(sourcePathString).append("] is a unreadable path").toString();
				throw new RuntimeException(errorMessage);
			}
		}

		if (ReadWriteMode.ONLY_WRITE.equals(readWriteMode) || ReadWriteMode.READ_WRITE.equals(readWriteMode)) {
			if (! sourcePath.canWrite()) {
				String errorMessage = new StringBuilder().append("the parameter sourcePathString[")
						.append(sourcePathString).append("] is a unwritable path").toString();
				throw new RuntimeException(errorMessage);
			}
		}
		return sourcePath;
	}

	/**
	 * 지정한 내용을 갖는 신규 파일을 지정한 문자셋을 저장한다. 파일이 존재하면 에러를 던진다.
	 * 
	 * @param targetFile    저장할 신규 파일
	 * @param contents      파일 내용
	 * @param targetCharset 문자셋
	 * @throws IOException 파일이 이미 존재할 경우 혹은 입출력 처리시 에러 발생시 던지는 예외
	 */
	public static void createNewFile(File targetFile, String contents, Charset targetCharset) throws IOException {
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
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] is file that already exist").toString();
			throw new IOException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] is not regular file").toString();
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] is a unwritable file").toString();
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

	/**
	 * 기존 파일에 지정한 문자셋으로 지정한 내용으로 덮어쓴다. 파일이 존재하지 않으면 에러를 던진다.
	 * 
	 * @param targetFile    저장할 기존 파일
	 * @param contents      파일 내용
	 * @param targetCharset 문자셋
	 * @throws IOException 파일이 존재하지 않거나 입출력 에러시 던지는 예외
	 */
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
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] does not exist").toString();
			throw new IOException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] is not a regular file").toString();
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = new StringBuilder().append("the parameter targetFile[")
					.append(targetFile.getAbsolutePath()).append("] is a unwritable file").toString();
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

	/**
	 * 신규 파일인 경우 지정한 파일에 지정한 문자셋으로 지정한 내용을 저장하고 기존 파일인 경우 지정한 문자셋으로 지정한 내용으로 덮어 쓴다
	 * 
	 * @param targetFile    저장할 파일
	 * @param contents      파일 내용
	 * @param targetCharset 문자셋
	 * @throws IOException 입출력 할때 에러 발생시 던지는 예외
	 */
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

	/*
	public static String getPrefixWithTabCharacters(int depth, int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = new StringBuilder().append("the parameter depth[").append(depth)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = new StringBuilder().append("the numberOfAdditionalTabs depth[")
					.append(numberOfAdditionalTabs).append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		StringBuilder stringBuilder = new StringBuilder();

		addPrefixWithTabCharacters(stringBuilder, depth, numberOfAdditionalTabs);

		return stringBuilder.toString();
	}
	*/

	/**
	 * 파라미터 'contentsStringBuilder' 에 파라미터 'depth' 와 파라미터 'numberOfAdditionalTabs' 의
	 * 함 만큼 탭 문자 추가
	 * 
	 * @param contentsStringBuilder  내용을 담고 있는 스트링 빌더
	 * @param depth                  기준의 탭 깊이
	 * @param numberOfAdditionalTabs 기준 대비 상대적 탭 깊이
	 */
	public static void addPrefixWithTabCharacters(StringBuilder contentsStringBuilder, int depth,
			int numberOfAdditionalTabs) {
		if (null == contentsStringBuilder) {
			throw new IllegalArgumentException("the parameter contentsStringBuilder is null");
		}

		if (depth < 0) {
			String errorMessage = new StringBuilder().append("the parameter depth[").append(depth)
					.append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = new StringBuilder().append("the numberOfAdditionalTabs depth[")
					.append(numberOfAdditionalTabs).append("] is less than zero").toString();
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
	 * @param c 판단 대상 문자
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
	 * @param c 판단 대상 문자
	 * @return 초성 중성 종성이 다 갖추어진'가' 에서 '힣' 까지의 한글 여부
	 */
	public static boolean isFullHangul(final char c) {
		boolean isHangul = false;
		if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	/**
	 * @param c 알파멧 소문자와 대문자 여부를 판단하고 싶은 문자
	 * @return 알파멧 소문자와 대문자 여부
	 */
	public static boolean isEnglish(final char c) {
		boolean isAlphabet = ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));

		return isAlphabet;
	}

	/**
	 * Punctuation: One of !"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~
	 * 
	 * @param c 판단 대상 문자
	 * @return 출력 가능한 특수문자 여부
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
	 * @param c 판단 대상 문자
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
	 * @param c 판단 대상 문자
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

	/**
	 * @param sourceString 검사 대상 문자열
	 * @return 검사 대상 문자열의 영문이나 숫자 포함 여부
	 */
	public static boolean isEnglishAndDigit(String sourceString) {
		for (char c : sourceString.toCharArray()) {
			if (!isEnglish(c) && !Character.isDigit(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * WARNING! 정규식을 이용한 방식과 아닌 방식의 속도 차를 검증하기 위한 단위 테스트 용도로 만든 메소드로 실전에서는 쓰지 말것!  
	 * 
	 * @param sourceString 검사 대상 문자열
	 * @return 검사 대상 문자열의 영문이나 숫자 포함 여부
	 */
	public static boolean isEnglishAndDigitWithRegular(String sourceString) {
		String regex = "[a-zA-Z0-9]+";

		boolean isValid = sourceString.matches(regex);
		return isValid;
	}

	/**
	 * @param sourceFile 읽을 대상 파일
	 * @param maxSize 허용할 최대 크기, 메모리상 너무 큰 파일을 읽어 오는것을 막기 위한 장치
	 * @return 파일로 부터 읽어온 바이트 배열
	 * @throws IOException 입출력 에러 발생시 던지는 예외
	 */
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

	/**
	 * 
	 * @param targetClassLoader 생성할 객체를 만들어줄 클래스 로더
	 * @param classFullName 생성할 객체의 클래스 전체 이름
	 * @return 파라미터 'targetClassLoader'(=생성할 객체를 만들어줄 클래스 로더) 에서 생성한 파라마티 'classFullName'(=생성할 객체의 클래스 전체 이름) 을 갖는 클래스 객체 
	 * @throws DynamicClassCallException 동적 클래스 객체 생성시 에러가 있을 경우 던지는 예외
	 */
	public static Object createtNewObject(ClassLoader targetClassLoader, String classFullName)
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

	/**
	 * 파라미터 '' 로 지정한 경로를 삭제한다. 유효한 경로는 OS 상에 존재하는 디렉토리를 뜻한다. 참고) 재귀 호출로 구현되었다.
	 * 
	 * @param path 경로
	 * @throws IOException  입출력 에러 발생시 던지는 예외
	 * @throws IllegalArgumentException 파라미터 'path' 가 null 이거나 혹은 파라미터 'path'가 유효하지 않은 경로일 경우  던지는 예외
	 */
	public static void deleteDirectory(File path) throws IllegalArgumentException, IOException {
		if (null == path) {
			throw new IllegalArgumentException("the parameter path is null");
		}

		if (!path.exists()) {
			String errorMessage = new StringBuilder().append("the paramter path[").append(path.getAbsolutePath())
					.append("] doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!path.isDirectory()) {
			String errorMessage = new StringBuilder().append("the paramter path[").append(path.getAbsolutePath())
					.append("] is not directory").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		for (File childFile : path.listFiles()) {

			if (childFile.isDirectory()) {
				deleteDirectory(childFile);
			} else {
				if (! childFile.delete()) {
					String errorMessage = new StringBuilder().append("fail to delete the child path[")
							.append(childFile.getAbsolutePath()).append("]").toString();
					throw new IOException(errorMessage);
				}
			}
		}

		if (! path.delete()) {
			String errorMessage = new StringBuilder().append("fail to delete the parameter path[")
					.append(path.getAbsolutePath()).append("]").toString();

			throw new IOException(errorMessage);
		}
	}

	/**
	 * '중간 객체'를 메시지로 변환한다. 참고) 파라미터로 넘어온 '중간 객체'는 에러 여부에 상관없이 무조건 자원 해제된다.
	 * 
	 * @param messageProtocol      '중간 객체' 를 만든 주체이자 해체시킬 프로토콜
	 * @param messageDecoder       메시지 디코더
	 * @param singleItemDecoder    단일 항목 디코더
	 * @param mailboxID            메일 박스 식별자
	 * @param mailID               메일 식별자
	 * @param messageID            메시지 식별자
	 * @param receivedMiddleObject 수신한 메시지 내용이 담긴 중간 객체
	 * @return 중간 객체에서 변환된 메시지
	 * @throws BodyFormatException      수신한 메시지 내용이 담긴 중간 객체를 메시지로 변환하는 과정에서 에러 발생시
	 *                                  던지는 예외
	 * @throws IllegalArgumentException 파라미터 값이 잘못되었을 경우 던지는 예외
	 */
	public static AbstractMessage O2M(MessageProtocolIF messageProtocol, AbstractMessageDecoder messageDecoder,
			SingleItemDecoderIF singleItemDecoder, int mailboxID, int mailID, String messageID,
			Object receivedMiddleObject) throws IllegalArgumentException, BodyFormatException {
		try {
			if (null == messageProtocol) {
				throw new IllegalArgumentException("the parameter messageProtocol is null");
			}

			if (null == messageDecoder) {
				throw new IllegalArgumentException("the parameter messageDecoder is null");
			}

			if (null == singleItemDecoder) {
				throw new IllegalArgumentException("the parameter singleItemDecoder is null");
			}

			if (null == messageID) {
				throw new IllegalArgumentException("the parameter messageID is null");
			}

			if (null == receivedMiddleObject) {
				throw new IllegalArgumentException("the parameter readableMiddleObject is null");
			}
			
			AbstractMessage outputMessage = messageDecoder.decode(singleItemDecoder, receivedMiddleObject);
			outputMessage.setMailboxID(mailboxID);
			outputMessage.setMailID(mailID);

			return outputMessage;
		} catch (BodyFormatException e) {
			throw e;
		} catch (Exception | Error e) {
			String errorMessage = new StringBuilder(
					"unknow error::fail to decode the var 'readableMiddleObject' of the output message")
							.append("mailboxID=").append(mailboxID).append(", mailID=").append(mailID)
							.append(", messageID=").append(messageID).append("], errmsg=").append("")
							.append(e.getMessage()).toString();

			Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
			log.log(Level.WARNING, errorMessage);

			throw new BodyFormatException(errorMessage);
		} finally {
			try {
				messageProtocol.closeReadableMiddleObject(mailboxID, mailID, messageID, receivedMiddleObject);
			} catch (Exception e1) {
				String errorMessage = new StringBuilder().append("fail to close the message body stream[messageID=")
						.append(messageID).append(", mailboxID=").append(mailboxID).append(", mailID=").append(mailID)
						.append("] body stream").toString();
				Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
				log.log(Level.WARNING, errorMessage, e1);
			}
		}
	}
}
