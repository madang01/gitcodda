package kr.pe.codda.common.etc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StreamCharsetFamilyTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStreamCharsetFamily_theParameterStreamCharsetIsNull() {
		try {
			new StreamCharsetFamily(null);
			
			fail("no IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			
		} catch(Exception e) {
			
		}
	}

}
