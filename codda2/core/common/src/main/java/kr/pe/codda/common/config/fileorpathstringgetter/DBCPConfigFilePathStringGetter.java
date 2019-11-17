package kr.pe.codda.common.config.fileorpathstringgetter;

import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;

/**
 * DBCP 설정 파일 전체 경로명을 정의한 클래스. 특이사항으로 '부가정보들'로 'dbcp 이름' 을 사용한다.
 * 
 * @author "Won Jonghoon"
 *
 */
public class DBCPConfigFilePathStringGetter extends AbstractFileOrPathStringGetter{
	public DBCPConfigFilePathStringGetter(String itemID) {
		super(itemID);
	}

	@Override
	public String getFileOrPathStringDependingOnInstalledPath(String installedPathString,
			String mainProjectName, String ... etcParamters) {
		if (0 == etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters must have dbcp paramter");
		}
		
		if (1 < etcParamters.length) {
			throw new IllegalArgumentException("the paramter etcParamters must have only one paramter that is 'dbcp name' but two more paramters");
		}
		return ProjectBuildSytemPathSupporter
				.getProjectDBCPConfigFilePathString(installedPathString, mainProjectName, etcParamters[0]);
	}
}
