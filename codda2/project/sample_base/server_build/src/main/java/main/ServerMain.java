package main;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerTaskException;
import kr.pe.codda.impl.inner_message.MemberRegisterDecryptionReq;
import kr.pe.codda.impl.task.server.MemberRegisterReqServerTask;
import kr.pe.codda.server.AnyProjectServer;
import kr.pe.codda.server.MainServerManager;
import kr.pe.codda.server.config.SampleBaseConfiguration;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;

public class ServerMain {

	public static void main(String argv[]) {
		Logger log = LoggerFactory.getLogger(CommonStaticFinalVars.BASE_PACKAGE_NAME);
		
		SampleBaseConfiguration sampleBaseConfiguration = new SampleBaseConfiguration();
		
		CoddaConfiguration coddaConfiguration = CoddaConfigurationManager.getInstance().getCoddaConfiguration();
		
		coddaConfiguration.setRunningProjectConfiguration(sampleBaseConfiguration);
		
		try {
			coddaConfiguration.loadConfigFile();
		} catch (Exception e) {
			log.error("fail to load config file, errmsg=" + e.getMessage(), e);
			System.exit(1);
		}
		
		try {
			MemberRegisterReqServerTask memberRegisterReqServerTask = new MemberRegisterReqServerTask();
			
			ServerDBUtil.initializeDBEnvoroment(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME);
			
			{
				String userID = "admin";
				byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
						(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
						(byte) '$' };
				String nickname = "관리자";
				String email = "admin@codda.pe.kr";
				String ip = "127.0.0.1";
				
				

				MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
				memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
				memberRegisterDecryptionReq.setUserID(userID);
				memberRegisterDecryptionReq.setNickname(nickname);
				memberRegisterDecryptionReq.setEmail(email);
				memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
				memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
				memberRegisterDecryptionReq.setIp(ip);
				
				try {
					ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (dsl) -> {
						
						memberRegisterReqServerTask.doWork(dsl, memberRegisterDecryptionReq);
					});
					
				} catch (ServerTaskException e) {
					String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
							.append(userID)
							.append("] 입니다").toString();
					String actualErrorMessag = e.getMessage();
					
					// log.warn(actualErrorMessag, e);
					
					if (! expectedErrorMessage.equals(actualErrorMessag)) {
						throw e;
					}
				}
				
			}
			
			{
				String userID = "guest";
				byte[] passwordBytes = { (byte) 't', (byte) 'e', (byte) 's',
						(byte) 't', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
						(byte) '$' };
				String nickname = "손님";
				String email = "guest@codda.pe.kr";
				String ip = "127.0.0.1";
				
				MemberRegisterDecryptionReq memberRegisterDecryptionReq = new MemberRegisterDecryptionReq();
				memberRegisterDecryptionReq.setMemberRoleType(MemberRoleType.MEMBER);
				memberRegisterDecryptionReq.setUserID(userID);
				memberRegisterDecryptionReq.setNickname(nickname);
				memberRegisterDecryptionReq.setEmail(email);
				memberRegisterDecryptionReq.setPasswordBytes(passwordBytes);
				memberRegisterDecryptionReq.setRegisteredDate(new java.sql.Timestamp(System.currentTimeMillis()));
				memberRegisterDecryptionReq.setIp(ip);

				try {
					ServerDBUtil.execute(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (dsl) -> {
						
						memberRegisterReqServerTask.doWork(dsl, memberRegisterDecryptionReq);
					});
				} catch (ServerTaskException e) {
					String expectedErrorMessage = new StringBuilder("기존 회원과 중복되는 아이디[")
							.append(userID)
							.append("] 입니다").toString();
					String actualErrorMessag = e.getMessage();
					
					// log.warn(actualErrorMessag, e);
					
					if (! expectedErrorMessage.equals(actualErrorMessag)) {
						throw e;
					}
				}
				
			}
			
			AnyProjectServer mainProjectServer = MainServerManager.getInstance().getMainProjectServer();
			mainProjectServer.startServer();
		} catch (Throwable e) {
			log.warn("unknown error", e);
		}
	}
}
