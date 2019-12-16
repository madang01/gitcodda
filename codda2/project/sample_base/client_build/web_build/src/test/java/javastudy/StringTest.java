package javastudy;

import java.util.HashMap;

import org.junit.Test;

import kr.pe.codda.common.sessionkey.ClientSessionKeyIF;

public class StringTest {
	
	@Test
	public void testSplit() {
		String queryString = "ttt=1";
		String pairStrings[] = queryString.split("=");
		
		System.out.printf("pairStrings.length=%d", pairStrings.length);
		System.out.println();
		
		
		// JsonParser jsonParser = new JsonParser();
	}
	
	@Test
	public void testNullCast() {
		HashMap<String, Object> hash = new HashMap<String, Object>();
		
		ClientSessionKeyIF clientSessionKey = (ClientSessionKeyIF)hash.get("ttte");
		
		if (null == clientSessionKey) {
			System.out.println("성공");
		}
		
	}
}
