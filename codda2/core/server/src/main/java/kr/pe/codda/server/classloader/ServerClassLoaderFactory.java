package kr.pe.codda.server.classloader;

import java.io.File;

import kr.pe.codda.common.classloader.SystemClassDeterminer;
import kr.pe.codda.common.classloader.SystemClassDeterminerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ServerClassLoaderFactory {
	private String serverAPPINFClassPathString = null;
	private String projectResourcesPathString = null;
	private SystemClassDeterminerIF excludedDynamicClassManager = new SystemClassDeterminer();
	
	public ServerClassLoaderFactory(String serverAPPINFClassPathString,
			String projectResourcesPathString) throws CoddaConfigurationException {
		if (null == serverAPPINFClassPathString) {
			throw new IllegalArgumentException("the parameter serverAPPINFClassPathString is null");
		}
		
		File serverAPPINFClassPath = new File(serverAPPINFClassPathString);
		
		if (! serverAPPINFClassPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] do not exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! serverAPPINFClassPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter serverAPPINFClassPathString[")
					.append(serverAPPINFClassPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (null == projectResourcesPathString) {
			throw new IllegalArgumentException("the parameter projectResourcesPathString is null");
		}
		
		File projectResourcesPath = new File(projectResourcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] doesn't exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the path whose path is the parameter projectResourcesPathString[")
					.append(projectResourcesPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		this.projectResourcesPathString = projectResourcesPathString;
	}
	
	public SimpleClassLoader createServerClassLoader() {
		return new SimpleClassLoader(serverAPPINFClassPathString, projectResourcesPathString, excludedDynamicClassManager);
	}
}
