package kr.pe.codda.server.lib;

import org.junit.Test;

import junitlib.AbstractBoardTest;

public class UserActivityTypeTest extends AbstractBoardTest {

	@Test
	public void test() {
		for (MemberActivityType userActivityType : MemberActivityType.values()) {
			log.info("userActivityType {}={}::{}", userActivityType.getName(), userActivityType.getValue(), (char)userActivityType.getValue());
		}
	}

}
