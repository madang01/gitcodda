package kr.pe.codda.server.dbcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.AllDBCPPartConfiguration;
import kr.pe.codda.common.config.subset.DBCPParConfiguration;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;

/**
 * <pre>
 * DB 연결 폴 관리자인 아파치 commons-dbcp 를 신놀이 설정 파일에서 
 * 지정한 DB 관련 환경 변수값에 맞쳐 설정하여 이용하는 클래스.
 * </pre>
 * 
 * @author Won Jonghoon
 * 
 */
public final class DBCPManager {
	private Logger log = Logger.getLogger(CommonStaticFinalVars.CORE_LOG_NAME);
	private ConcurrentHashMap<String, BasicDataSource> dbcpName2BasicDataSourceHash = new ConcurrentHashMap<String, BasicDataSource>();
	private ConcurrentHashMap<BasicDataSource, String> basicDataSource2dbcpConnectionPoolNameHash = new ConcurrentHashMap<BasicDataSource, String>();

	private List<String> dbcpNameList = null;
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스
	 */
	private static final class DBCPManagerHolder {
		static final DBCPManager singleton = new DBCPManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 구현 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static DBCPManager getInstance() {
		return DBCPManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 */
	private DBCPManager() {
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();

		AllDBCPPartConfiguration allDBCPPart = runningProjectConfiguration.getAllDBCPPartConfiguration();

		dbcpNameList = allDBCPPart.getDBCPNameList();

		for (String dbcpName : dbcpNameList) {
			DBCPParConfiguration dbcpPart = allDBCPPart.getDBCPPartConfiguration(dbcpName);
			if (null == dbcpPart) {
				log.log(Level.WARNING, "the dbcp name[" + dbcpName + "] is bad, check dbcp part of config file");
				continue;
			}
			File dbcpConfigFile = dbcpPart.getDBCPConfigFile();

			Properties dbcpConnectionPoolConfig = new Properties();
			FileInputStream fis = null;
			InputStreamReader isr = null;
			try {
				fis = new FileInputStream(dbcpConfigFile);
				isr = new InputStreamReader(fis, "UTF-8");
				dbcpConnectionPoolConfig.load(isr);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("when dbcp connection pool[")
						.append(dbcpName)
						.append("]'s config file[")
						.append(dbcpConfigFile.getAbsolutePath())
						.append("] io error, errormessage=")
						.append(e.getMessage()).toString();
				log.log(Level.WARNING, errorMessage);
				continue;
			} finally {
				if (null != isr) {
					try {
						isr.close();
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("fail to close the dbcp[")
								.append(dbcpName)
								.append("] properties file's input stream reader").toString();
						
						log.log(Level.WARNING, errorMessage, e);
					}
				}

				if (null != fis) {
					try {
						fis.close();
					} catch (Exception e) {
						String errorMessage = new StringBuilder()
								.append("fail to close the dbcp[")
								.append(dbcpName)
								.append("] properties file's file input stream").toString();
						
						log.log(Level.WARNING, errorMessage, e);
					}
				}
			}

			String driverClassName = dbcpConnectionPoolConfig.getProperty("driver");
			if (null == driverClassName) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				String errorMessage = new StringBuilder()
						.append("dbcp connection pool[")
						.append(dbcpName)
						.append("]'s JDBC Driver name is null, dbcpConnectionPoolConfig=")
						.append(dbcpConnectionPoolConfig.toString()).toString();
				
				log.log(Level.WARNING, errorMessage);
				continue;
			}

			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				String errorMessage = new StringBuilder()
						.append("dbcp connection pool[")
						.append(dbcpName)
						.append("'s JDBC Driver[")
						.append(driverClassName)
						.append("] not exist, dbcpConnectionPoolConfig=")
						.append(dbcpConnectionPoolConfig.toString()).toString();
				
				log.log(Level.WARNING, errorMessage);
				continue;
			}

			BasicDataSource basicDataSource = null;
			try {
				basicDataSource = BasicDataSourceFactory.createDataSource(dbcpConnectionPoolConfig);
			} catch (Exception e) {
				dbcpConnectionPoolConfig.setProperty("password", "");
				
				String errorMessage = new StringBuilder()
						.append("fail to create dbcp name '")
						.append(dbcpName)
						.append("''s data source, dbcpConnectionPoolConfig=")
						.append(dbcpConnectionPoolConfig.toString()).toString();
				
				log.log(Level.WARNING, errorMessage);
				continue;
			}		

			dbcpName2BasicDataSourceHash.put(dbcpName, basicDataSource);
			basicDataSource2dbcpConnectionPoolNameHash.put(basicDataSource, dbcpName);

			log.log(Level.INFO, "successfully dbcp[" +  dbcpName +"] was registed");
		}
	}

	public String getDBCPConnectionPoolName(DataSource dataSource) {
		if (!(dataSource instanceof BasicDataSource)) {
			String classNameOfTheParamterDataSource = dataSource.getClass().getName();
			
			String errorMessage = new StringBuilder()
					.append("the parameter dataSouce[")
					.append(classNameOfTheParamterDataSource)
					.append("] is not a BasicDataSource class instance").toString();
			
			log.log(Level.WARNING, errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		return basicDataSource2dbcpConnectionPoolNameHash.get(dataSource);
	}

	
	/**
	 * 파라미터 dbcpName 로 지정한 이름을 갖는 dbcp 를 반환한다.
	 * @param dbcpName dbcp 이름
	 * @return dbcp(=data base connection pool)
	 * @throws IllegalArgumentException the parameter dbcpName is null or the parameter dbcpName is not a element of the dbcp name list of config file
	 * @throws DBCPDataSourceNotFoundException the dbcp name list of config file is empty or it failed to create a dbcp connection pool having the dbcp name that is the parameter dbcpName
	 */
	public BasicDataSource getBasicDataSource(String dbcpName) throws IllegalArgumentException, DBCPDataSourceNotFoundException {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the parameter dbcpName is null");
		}
		
		if (dbcpNameList.isEmpty()) {
			throw new DBCPDataSourceNotFoundException("the dbcp name list of config file is empty");
		}
		
		if (! dbcpNameList.contains(dbcpName)) {
			throw new IllegalArgumentException(new StringBuilder("the parameter dbcpName[")
			.append(dbcpName)
			.append("] is bad, it is a element of the dbcp name list of config file").toString());
		}		
				
		BasicDataSource basicDataSource = dbcpName2BasicDataSourceHash.get(dbcpName);
		
		if (null == basicDataSource) {
			throw new DBCPDataSourceNotFoundException(
					new StringBuilder("it failed to create a dbcp connection pool having dbcp name[")
					.append(dbcpName).append("]").toString());
		}

		return basicDataSource;
	}

	public void closeAllDataSource() {
		Enumeration<BasicDataSource> basicDataSourceEnum = dbcpName2BasicDataSourceHash.elements();
		while (basicDataSourceEnum.hasMoreElements()) {
			BasicDataSource basicDataSource = basicDataSourceEnum.nextElement();
			try {
				if (null != basicDataSource) {
					basicDataSource.close();
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "unknown error", e);
			}
		}
	}

	protected void finalize() throws Throwable {
		closeAllDataSource();
	}
}
