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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public abstract class SequencedPropertiesUtil {
	// private static Logger log = LoggerFactory.getLogger(SequencedPropertiesUtil.class);
	
	
	public static SequencedProperties loadSequencedPropertiesFile(
			String sourcePropertiesFilePathString, Charset sourcePropertiesFileCharset) throws FileNotFoundException,  IOException {
		SequencedProperties sourceSequencedProperties = new SequencedProperties();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(sourcePropertiesFilePathString);
			isr = new InputStreamReader(fis, sourcePropertiesFileCharset);
			
			sourceSequencedProperties.load(isr);
		} finally {
			if (null != isr) {
				try {
					isr.close();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
		return sourceSequencedProperties;
	}

	public static void createNewSequencedPropertiesFile(
			SequencedProperties sourceProperties, String sourcePropertiesTitle,
			String sourcePropertiesFilePathString,
			Charset sourcePropertiesFileCharset) throws IOException {
		Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
		
		File sourcePropertiesFile = new File(sourcePropertiesFilePathString);
				
		
		boolean isSuccess = sourcePropertiesFile.createNewFile();
		if (! isSuccess) {
			String errorMessage = new StringBuilder()
					.append("fail to create a new file becase the file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] exist").toString();
			throw new IOException(errorMessage);
		}
		
		log.log(Level.INFO, "the sequenced properties file[" + sourcePropertiesFilePathString +"] was created");

		if (! sourcePropertiesFile.isFile()) {
			String errorMessage = new StringBuilder()
					.append("the sequenced properties file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] is not a regular file").toString();
			throw new IOException(errorMessage);
		}

		if (! sourcePropertiesFile.canWrite()) {
			String errorMessage = new StringBuilder()
					.append("the sequenced properties file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] is a unwritable file").toString();
			throw new IOException(errorMessage);
		}

		doSaveSequencedPropertiesFile(sourceProperties, sourcePropertiesTitle, sourcePropertiesFileCharset,
				sourcePropertiesFile);
	}
	
	public static void overwriteSequencedPropertiesFile(
			SequencedProperties sourceProperties, String sourcePropertiesTitle,
			String sourcePropertiesFilePathString,
			Charset sourcePropertiesFileCharset) throws FileNotFoundException,  IOException {		
		File sourcePropertiesFile = new File(sourcePropertiesFilePathString);
				
		if (! sourcePropertiesFile.exists()) {
			String errorMessage = new StringBuilder()
					.append("the file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] does not exist").toString();
			throw new IOException(errorMessage);
		}

		if (! sourcePropertiesFile.isFile()) {
			String errorMessage = new StringBuilder()
					.append("the sequenced properties file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] is not a regular file").toString();
			throw new IOException(errorMessage);
		}

		if (!sourcePropertiesFile.canWrite()) {
			String errorMessage = new StringBuilder()
					.append("the sequenced properties file whose path is the parameter sourcePropertiesFilePathString[")
					.append(sourcePropertiesFilePathString)
					.append("] is a unwritable file").toString();
			throw new IOException(errorMessage);
		}

		doSaveSequencedPropertiesFile(sourceProperties, sourcePropertiesTitle, sourcePropertiesFileCharset,
				sourcePropertiesFile);
	}

	private static void doSaveSequencedPropertiesFile(SequencedProperties sourceProperties,
			String sourcePropertiesTitle, Charset sourcePropertiesFileCharset, File sourcePropertiesFile)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(sourcePropertiesFile);
			osw = new OutputStreamWriter(fos, sourcePropertiesFileCharset);
			sourceProperties.store(osw, sourcePropertiesTitle);
			
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}
	}	
	
}
