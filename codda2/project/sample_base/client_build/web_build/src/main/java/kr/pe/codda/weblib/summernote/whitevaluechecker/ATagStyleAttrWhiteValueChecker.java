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

package kr.pe.codda.weblib.summernote.whitevaluechecker;

import kr.pe.codda.weblib.exception.WhiteParserException;
import kr.pe.codda.weblib.summernote.AttributeWhiteValueChekerIF;

public class ATagStyleAttrWhiteValueChecker implements AttributeWhiteValueChekerIF {	
	private final String tagName = "a";
	private final String attributeName = "style";
	
	private final String prefixOfRGB = "rgb(";
	private final char[] charArrayOfPrefixOfRGB = prefixOfRGB.toCharArray();

	private final char separactorOfRGB = ',';
	private final char suffixOfRGB = ')';
	
	@Override
	public void throwExceptionIfNoWhiteValue(String attributeValue) throws WhiteParserException {
		if (null == attributeValue) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("]'s value is null").toString();

			throw new WhiteParserException(errorMessage);
		}

		if ("".equals(attributeValue)) {
			return;
		}
		
		String[] itemList = attributeValue.split(";");

		if (null == itemList || 0 == itemList.length) {
			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] is a bad style becase the var items is null or empty").toString();

			throw new WhiteParserException(errorMessage);
		}

		for (String item : itemList) {
			item = item.trim();

			String[] property = item.split(":");

			if (null == property || 0 == property.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is null or empty").toString();

				throw new WhiteParserException(errorMessage);
			}

			if (2 != property.length) {
				String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
						.append("]'s attribte[").append(attributeName).append("] has a bad value[")
						.append(attributeValue).append("] becase the var itemSet of the var item[").append(item)
						.append("] is not a set of key and value").toString();

				throw new WhiteParserException(errorMessage);
			}

			String key = property[0].trim();
			String value = property[1].trim();
			
			if ("background-color".equals(key) || "color".equals(key)) {
				int i = 0;

				char[] charArrayOfValue = value.toCharArray();

				if (charArrayOfValue.length < 10) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s length is less than 10").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (charArrayOfValue[charArrayOfValue.length - 1] != suffixOfRGB) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("]'s last chracter is not a '").append(suffixOfRGB).append("'")
							.toString();

					throw new WhiteParserException(errorMessage);
				}

				for (; i < charArrayOfPrefixOfRGB.length; i++) {
					if (charArrayOfPrefixOfRGB[i] != charArrayOfValue[i]) {
						String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
								.append("]'s attribte[").append(attributeName).append("] has a bad value[")
								.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
								.append(value).append("] is bad becase it doen't begin 'rgb('").toString();

						throw new WhiteParserException(errorMessage);
					}
				}

				String redString = null;
				String greenString = null;
				String blackString = null;
				StringBuilder colorStringBuilder = new StringBuilder();

				for (; i < charArrayOfValue.length; i++) {
					char ch = charArrayOfValue[i];
					if (ch == separactorOfRGB) {
						if (null == redString) {
							redString = colorStringBuilder.toString();
							colorStringBuilder.setLength(0);
						} else {
							greenString = colorStringBuilder.toString();
							colorStringBuilder.setLength(0);
						}
					} else if (ch == suffixOfRGB) {
						if (null != greenString) {
							blackString = colorStringBuilder.toString();
						}
						break;
					} else {
						colorStringBuilder.append(ch);
					}
				}

				if (null == redString || null == greenString || null == blackString) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it doesn't have rgb").toString();

					throw new WhiteParserException(errorMessage);
				}

				redString = redString.trim();
				greenString = greenString.trim();
				blackString = blackString.trim();

				int red = -1;
				int green = -1;
				int black = -1;

				try {
					red = Integer.parseInt(redString);
					green = Integer.parseInt(greenString);
					black = Integer.parseInt(blackString);
				} catch (NumberFormatException e) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase it is not rgb").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (red < 0 && red > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase red[").append(red)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (green < 0 && green > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase green[").append(green)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				if (black < 0 && black > 255) {
					String errorMessage = new StringBuilder().append("the tag name[").append(tagName)
							.append("]'s attribte[").append(attributeName).append("] has a bad value[")
							.append(attributeValue).append("] becase the var key[").append(key).append("]'s value[")
							.append(value).append("] is bad becase black[").append(black)
							.append("] is less than zero or greater than 255").toString();

					throw new WhiteParserException(errorMessage);
				}

				// background-color: rgb(0,0,0);
				continue;
			}

			String errorMessage = new StringBuilder().append("the tag name[").append(tagName).append("]'s attribte[")
					.append(attributeName).append("] has a bad value[").append(attributeValue)
					.append("] becase the var itemSet of the var item[").append(item).append("] is a disallowed value")
					.toString();

			throw new WhiteParserException(errorMessage);
		}
	}
	
	@Override
	public String getTagName() {
		return tagName;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}
}
