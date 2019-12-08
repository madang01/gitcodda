package kr.pe.codda.client.classloader;

import java.io.File;

import kr.pe.codda.common.classloader.SystemClassDeterminer;
import kr.pe.codda.common.classloader.SystemClassDeterminerIF;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class ClientClassLoaderFactory {
	private String clientClassloaderClassPathString = null;
	private String clientClassloaderReousrcesPathString = null;
	private SystemClassDeterminerIF excludedDynamicClassManager = new SystemClassDeterminer();
	
	public ClientClassLoaderFactory(String clientClassloaderClassPathString,
			String clientClassloaderReousrcesPathString) throws CoddaConfigurationException {
		if (null == clientClassloaderClassPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderClassPathString is null");
		}

		File clientAPPINFClassPath = new File(clientClassloaderClassPathString);
		
		if (!clientAPPINFClassPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the client APP-INF class path[")
					.append(clientClassloaderClassPathString)
					.append("] doesn't exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (!clientAPPINFClassPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the client APP-INF class path[")
					.append(clientClassloaderClassPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (null == clientClassloaderReousrcesPathString) {
			throw new IllegalArgumentException("the parameter clientClassloaderReousrcesPathString is null");
		}
		
		File projectResourcesPath = new File(clientClassloaderReousrcesPathString);
		
		if (! projectResourcesPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the project resources path[")
					.append(clientClassloaderReousrcesPathString)
					.append("] doesn't exist").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		if (! projectResourcesPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the project resources path[")
					.append(clientClassloaderReousrcesPathString)
					.append("] isn't a directory").toString();
		 	throw new CoddaConfigurationException(errorMessage);
		}
		
		this.clientClassloaderClassPathString = clientClassloaderClassPathString;
		this.clientClassloaderReousrcesPathString = clientClassloaderReousrcesPathString;		
	}
	
	public SimpleClassLoader createClientClassLoader() {
		return new SimpleClassLoader(clientClassloaderClassPathString, clientClassloaderReousrcesPathString, excludedDynamicClassManager);
	}
}
