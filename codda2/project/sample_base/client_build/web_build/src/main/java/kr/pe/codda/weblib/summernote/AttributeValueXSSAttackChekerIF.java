package kr.pe.codda.weblib.summernote;

import kr.pe.codda.weblib.exception.WhiteParserException;

public interface AttributeValueXSSAttackChekerIF {
	public void checkXSSAttack(String attributeValue) throws WhiteParserException;
	
	public String getTagName();
	public String getAttributeName();
}
