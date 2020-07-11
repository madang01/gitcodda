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

package kr.pe.codda.common.config;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Ignore;

import kr.pe.codda.common.config.subset.DBCPPartConfigurationManager;
import kr.pe.codda.common.config.subset.SubProjectPartConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.config.subset.ProjectPartConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.SessionKey;

public class CoddaConfigurationManagerTest {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);

	@Ignore
	public void testGetInstance() {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				
				.getCoddaConfiguration();
		DBCPPartConfigurationManager allDBCPPart = runningProjectConfiguration.getDBCPPartConfigurationManager();
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		AbstractProjectPartConfiguration mainProjectPart = runningProjectConfiguration.getMainProjectPartConfiguration();
		SubProjectPartConfigurationManager allSubProjectPart = runningProjectConfiguration.getSubProjectPartConfigurationManager();
		
		
		List<String> dbcpNameList = allDBCPPart.getDBCPNameList();
		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpPart = allDBCPPart.getDBCPPartConfiguration(dbcpName);
			
			if (null == dbcpPart) {
				String infoMessage = new StringBuilder()
						.append("dbcpPart[")
						.append(dbcpName)
						.append("] is null").toString();
				
				log.info(infoMessage);
				fail("dbcpPart is null");
			}
			
			
			if (dbcpPart.toString().indexOf("null") >= 0) {
				fail("Maybe dbcp part's one more variables are null");
			}
			
			// log.info(dbcpPart.toString());
		}
		
		log.info(commonPart.toString());
		
		if (commonPart.toString().indexOf("null") >= 0) {
			/** if RSA Keypair source is API, then rsaKeyPairPathOfSessionKey is null. so first null no problem. */
			if (!commonPart.getRsaKeypairSourceOfSessionKey().equals(SessionKey.RSAKeypairSourceType.SERVER)) {
				fail("Maybe common part's one more variables are null");
			}
			
			/** second null check */
			if (commonPart.toString().indexOf("null", commonPart.toString().indexOf("rsaKeySizeOfSessionKey")) >= 0) {
				fail("Maybe common part's three more variables are null");
			}
		}
		
		log.info(mainProjectPart.toString());
		
		if (mainProjectPart.toString().indexOf("null") >= 0) {
			fail("Maybe main project part's one more variables are null");
		}
		
		List<String> subProjectNameList = allSubProjectPart.getSubProjectNamelist();
		for (String subProjectName : subProjectNameList) {
			AbstractProjectPartConfiguration projectPart = allSubProjectPart.getSubProjectPartConfiguration(subProjectName);
			
			if (null == projectPart) {
				String infoMessage = new StringBuilder()
						.append("sub projectPart[")
						.append(subProjectName)
						.append("] is null").toString();
				
				log.info(infoMessage);
				fail("projectPart is null");
			}
			
			// log.info(projectPart.toString());
			if (projectPart.toString().indexOf("null") >= 0) {
				fail("Maybe sub project part's one more variables are null");
			}
		}
	}
}
