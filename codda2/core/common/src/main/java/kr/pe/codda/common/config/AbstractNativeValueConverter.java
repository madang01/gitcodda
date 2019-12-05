package kr.pe.codda.common.config;

public abstract class AbstractNativeValueConverter<T> {	
	
	public abstract T valueOf(String itemValue) throws IllegalArgumentException;
	
	
	private Class<T> genericType = null;
	
	public AbstractNativeValueConverter(Class<T> genericType) {
		if (null == genericType) {
			String errorMessage = new StringBuilder("the parameter genericType is null")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		this.genericType = genericType;
	}
	
		
	public Class<T> getGenericType() {
		return genericType;
	}
}
