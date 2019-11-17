package kr.pe.codda.server.classloader;

import java.io.File;
import java.util.HashMap;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.task.AbstractServerTask;

public class ServerTaskManger implements ServerTaskMangerIF {
	// private InternalLogger log = InternalLoggerFactory.getInstance(ServerDynamicObjectManger.class);

	// private final Object monitor = new Object();

	private ServerClassLoaderFactory serverClassLoaderFactory = null;
	private SimpleClassLoader currentWorkingServerClassLoader = null;

	private final HashMap<String, ServerTaskInfomation> messageID2ServerTaskInformationHash = new HashMap<String, ServerTaskInfomation>();
	// private ReentrantLock lock = new ReentrantLock();

	public ServerTaskManger(ServerClassLoaderFactory serverClassLoaderFactory) {
		this.serverClassLoaderFactory = serverClassLoaderFactory;

		this.currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
	}

	private ServerTaskInfomation getNewServerTaskFromWorkBaseClassload(String messageID)
			throws DynamicClassCallException {
		String serverTaskClassFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(currentWorkingServerClassLoader, serverTaskClassFullName);
		
		if (! (retObject instanceof AbstractServerTask)) {
			String errorMessage = new StringBuilder()
			.append("this instance of ")
			.append(serverTaskClassFullName)
			.append(" class that was created by server dynamic classloader[")
			.append(currentWorkingServerClassLoader.hashCode())			
			.append("] class is not a instance of AbstractServerTask class").toString();

			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractServerTask serverTask = (AbstractServerTask) retObject;
		String serverTaskClassFilePathString = currentWorkingServerClassLoader.getClassFilePathString(serverTaskClassFullName);

		// serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;
		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(serverTaskClassFilePathString);

		return new ServerTaskInfomation(serverTaskClassFile, serverTask);
	}
	
	
	private ServerTaskInfomation getServerTaskInfomation(String messageID)
			throws DynamicClassCallException {
		ServerTaskInfomation serverTaskInfomation = messageID2ServerTaskInformationHash.get(messageID);
		if (null == serverTaskInfomation) {
			// lock.lock();			
			serverTaskInfomation = getNewServerTaskFromWorkBaseClassload(messageID);

			messageID2ServerTaskInformationHash.put(messageID, serverTaskInfomation);
						
		} else if (serverTaskInfomation.isModifed()) {
			/** 새로운 서버 클래스 로더로 교체 */
			currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
			serverTaskInfomation = getNewServerTaskFromWorkBaseClassload(messageID);
			messageID2ServerTaskInformationHash.put(messageID, serverTaskInfomation);
				
		}
		return serverTaskInfomation;
	}
	
	@Override
	public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException {
		ServerTaskInfomation serverTaskInfomation = getServerTaskInfomation(messageID);
	
		return serverTaskInfomation.getServerTask();
	}
}
