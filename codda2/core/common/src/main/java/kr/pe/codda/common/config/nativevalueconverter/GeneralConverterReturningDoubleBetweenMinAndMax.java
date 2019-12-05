package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;

public class GeneralConverterReturningDoubleBetweenMinAndMax extends AbstractMinMaxConverter<Double> {	
	public GeneralConverterReturningDoubleBetweenMinAndMax(Double min, Double max) {
		super(min, max, ComparableComparator.<Double>comparableComparator(), Double.class);		
	}

	@Override
	protected Double innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "the parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "the parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Double returnedValue = null;
		try {
			returnedValue = Double.valueOf(itemValue);
			
			/**
			 * WARNING! Double 은 특수하게 Double 로 표현할 수 없는 값이라도 NumberFormatException 으로 떨구지 않고 아래와 같은 상태 플래그 값을 설정함.
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