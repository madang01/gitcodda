package kr.pe.codda.server.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.util.JarFileFilter;

public class JarUtil {	
	public static ConcurrentHashMap<String, JarClassEntryContents> getJarClassEntryContensHash(String jarLibrayPathString) throws FileNotFoundException {
		if (null == jarLibrayPathString) {
			throw new IllegalArgumentException("parameter jarLibrayPathName is null");
		}
		
		// 		Jar extension file
		File[] jarExtensionFileList = getJarExtensionFileList(jarLibrayPathString);
		
		if (null == jarExtensionFileList) {
			String errorMessage = new StringBuilder()
					.append("fail to get a jar file short name list in path that is the parameter jarLibrayPathString").toString();
			Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		ConcurrentHashMap<String, JarClassEntryContents> jarClassEntryContentsHash = new ConcurrentHashMap<String, JarClassEntryContents>();
		
		for (File jarExtensionFile : jarExtensionFileList) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(jarExtensionFile);
			} catch (IOException e1) {
				String errorMessage = new StringBuilder()
						.append("fail to create JarFile.class instance using the jar extension file[")
						.append(jarExtensionFile.getAbsolutePath())
						.append("]").toString();				
				Logger.getLogger("JarTestMain").log(Level.WARNING, errorMessage);
				continue;
			}
			try {
				
				Enumeration<JarEntry> jarEntries = jarFile.entries();
				while (jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();				
					
					if (jarEntry.isDirectory()) continue;
					
					long jarEntrySize = jarEntry.getSize();
					String jarEntryName = jarEntry.getName();
					if (jarEntrySize > CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE) {
						String errorMessage = new StringBuilder()
								.append("the size[")
								.append(jarEntrySize)
								.append("] of class file[")
								.append(jarEntryName)
								.append("] in jar file[")
								.append(jarExtensionFile.getAbsolutePath())
								.append("] is larger than max size=[")
								.append(CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE)
								.append("]").toString();
						
						Logger.getLogger("JarTestMain").log(Level.WARNING, errorMessage);
						continue;
					}
					
					if (jarEntryName.endsWith(".class")) {
											
						int inx = jarEntryName.lastIndexOf(".class");
						
						String classFullName = jarEntryName.substring(0, inx).replace('/', '.');
						
						
						int classFileBufferSize = (int)jarEntrySize;
						byte[] classFileBuffer = new byte[classFileBufferSize];
						int offset = 0;
						int len = classFileBufferSize;
						
						InputStream is = null;
						try {							
							is = jarFile.getInputStream(jarEntry);	
							
							int inputStreamSize = is.available();
							if (inputStreamSize != classFileBufferSize) {
								String errorMessage = new StringBuilder()
										.append("the jar[")
										.append(jarExtensionFile.getAbsolutePath())
										.append("] entry[")
										.append(jarEntryName)
										.append("]'s input stream size[")
										.append(inputStreamSize)
										.append("] is different from jar entry size[")										
										.append(jarEntrySize)
										.append("]").toString();
								
								Logger.getLogger("JarUtil").warning(errorMessage);
								continue;
							}
							
							do {
								int readBytes = is.read(classFileBuffer, offset, len);
								offset += readBytes;
								len -= readBytes;
								if (len < 0) {
									String errorMessage = new StringBuilder()
											.append("the jar[")
											.append(jarExtensionFile.getAbsolutePath())
											.append("] entry[")
											.append(jarEntryName)
											.append("]'s read bytes[")
											.append(offset)
											.append("] is greater than jar entry size[")										
											.append(jarEntrySize)
											.append("]").toString();
									
									Logger.getLogger("JarUtil").warning(errorMessage);
								}
							} while (is.available() > 0);
							
						} finally  {
							if (null != is) {
								try {
									is.close();
								} catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
						JarClassEntryContents jarClassEntryContents = new JarClassEntryContents(jarExtensionFile.getAbsolutePath(), classFullName, classFileBuffer);
						jarClassEntryContentsHash.put(classFullName, jarClassEntryContents);
					}
				}
			} catch (IOException e) {
				String errorMessage = new StringBuilder()
						.append("fail to create a jar[")
						.append(jarExtensionFile.getAbsolutePath())
						.append("] entry contents hash map bease of IOException").toString();
				Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage, e);
				continue;
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to create a jar[")
						.append(jarExtensionFile.getAbsolutePath())
						.append("] entry contents hash map bease of unknown error").toString();
				Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage, e);
				continue;
			} finally {
				if (null != jarFile) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return jarClassEntryContentsHash;
	}

	private static File[] getJarExtensionFileList(String jarLibrayPathString) throws FileNotFoundException {		
		File jarLibrayPath = new File(jarLibrayPathString);
		if (!jarLibrayPath.exists()) {
			String errorMessage = new StringBuilder()
					.append("the file whose path is the parameter jarLibrayPathString[")
					.append(jarLibrayPathString)
					.append("] do not exist").toString();
			
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.isDirectory()) {
			String errorMessage = new StringBuilder()
					.append("the file whose path is the parameter jarLibrayPathString[")
					.append(jarLibrayPathString)
					.append("] is not a directory").toString();
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.canRead()) {
			String errorMessage = new StringBuilder()
					.append("the file whose path is the parameter jarLibrayPathString[")
					.append(jarLibrayPathString)
					.append("] is a unreadable file").toString();
			throw new FileNotFoundException(errorMessage);
		}
		
		
		
		return jarLibrayPath.listFiles(new JarFileFilter());
	}
}
