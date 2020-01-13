package kr.pe.codda.common.buildsystem;

import static org.junit.Assert.fail;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.exception.BuildSystemException;

public class EclipseBuilderTest extends AbstractJunitTest 	{

	@Test
	public void testSaveCoddaCoreAllEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.saveCoddaCoreAllEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	/*
	@Test	
	public void testSaveCoddaCoreLoggerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.createCoddaCoreLoggerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	*/

	@Test
	public void testSaveCoddaCoreHelperEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.saveCoddaCoreHelperEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testSaveCoddaSampleBaseServerEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.saveCoddaSampleBaseServerEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testSaveCoddaSampleBaseAppClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.saveCoddaSampleBaseAppClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
	
	@Test
	public void testSaveCoddaSampleBaseWebClientEclipeWorkbenchFiles() {
		try {
			EclipseBuilder eclipseBuilder = new EclipseBuilder(installedPath.getAbsolutePath());
			eclipseBuilder.saveCoddaSampleBaseWebClientEclipeWorkbenchFiles();
		} catch (BuildSystemException e) {
			log.warn("error", e);
			fail("실패");
		}
	}
}
