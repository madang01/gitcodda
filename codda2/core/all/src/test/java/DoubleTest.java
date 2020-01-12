import static org.junit.Assert.fail;

import org.junit.Test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



public class DoubleTest {

	@Test
	public void test_큰수일경우에도파싱에러없는것확인() {
		double max = Double.longBitsToDouble(0x7fefffffffffffffL);
		
		System.out.printf("%f", max);
		System.out.println();
		
		
		try {
			Double t = Double.parseDouble("279769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.000000");
			t.isInfinite();
			t.isNaN();
			
			System.out.printf("%f", t);
			System.out.println();
		} catch(NumberFormatException e) {			
			
			fail("11111");
		}
	}
}
