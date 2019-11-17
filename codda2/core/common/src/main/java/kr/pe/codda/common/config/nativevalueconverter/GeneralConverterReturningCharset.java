package kr.pe.codda.common.config.nativevalueconverter;

import java.nio.charset.Charset;

import kr.pe.codda.common.config.AbstractNativeValueConverter;

public class GeneralConverterReturningCharset extends AbstractNativeValueConverter<Charset> {

	public GeneralConverterReturningCharset() {
		super(Charset.class);
	}

	@Override
	public Charset valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Charset returnValue = null;		
		try {
			returnValue = Charset.forName(itemValue);
		} catch(Exception e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
			.append(itemValue)
			.append("] is a bad charset name, errormessage=")
			.append(e.getMessage()).toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		return returnValue;
		
	}

}
