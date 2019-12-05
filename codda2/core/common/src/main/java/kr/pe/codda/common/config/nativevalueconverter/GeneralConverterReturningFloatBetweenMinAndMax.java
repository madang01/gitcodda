package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;

public class GeneralConverterReturningFloatBetweenMinAndMax extends AbstractMinMaxConverter<Float> {	
	public GeneralConverterReturningFloatBetweenMinAndMax(Float min, Float max) {
		super(min, max, ComparableComparator.<Float>comparableComparator(), Float.class);		
	}

	@Override
	protected Float innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Float returnedValue = null;
		try {
			returnedValue = Float.valueOf(itemValue);
			
			/**
			 * WARNING! Float 은 특수하게 Float 로 표현할 수 없는 값이라도 NumberFormatException 으로 떨구지 않고 아래와 같은 상태 플래그 값을 설정함.
			 */
			if (returnedValue.isInfinite() || returnedValue.isNaN()) {
				String errorMessage = new StringBuilder("the parameter itemValue[")
						.append(itemValue).append("] is not a number of ")
						.append(getGenericType().getName())
						.toString();
				throw new IllegalArgumentException(errorMessage);
			}

		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(getGenericType().getName())
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnedValue;
	}
}
