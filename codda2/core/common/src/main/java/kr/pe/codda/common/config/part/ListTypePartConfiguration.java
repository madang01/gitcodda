package kr.pe.codda.common.config.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;

public class ListTypePartConfiguration<T extends PartConfigurationIF> implements PartConfigurationIF {
	private final String partName;
	private final Class<T> clazz;
	
	private final String nameListKey;
	
	private final ArrayList<String> nameList = new ArrayList<String>();
	private final HashMap<String, T> partConfigurationHash = new HashMap<String, T>();

	public ListTypePartConfiguration(String partName, Class<T> clazz) {
		if (null == partName) {
			throw new IllegalArgumentException("the parameter partName is null");
		}
		
		if (partName.isEmpty()) {
			throw new IllegalArgumentException("the parameter partName is empty");
		}
		
		if (! CommonStaticUtil.isEnglishAndDigit(partName)) {
			throw new IllegalArgumentException("the parameter partName has one more characters that are not english and not digit");
		}
		
		if (null == clazz) {
			throw new IllegalArgumentException("the parameter clazz is null");
		}
		
		this.partName = partName;
		this.clazz = clazz;
		
		
		nameListKey = new StringBuilder().append(partName)
				.append(".name_list.value").toString();
	}
	

	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		
		String nameListValue = sourceSequencedProperties.getProperty(nameListKey);

		if (null == nameListValue) {
			String errorMessage = new StringBuilder()
					.append("the ")
					.append(partName)
					.append(" list key(=").append(nameListKey)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(nameListKey, errorMessage);
		}

		nameListValue = nameListValue.trim();
		nameList.clear();
		partConfigurationHash.clear();
		
		StringTokenizer tokens = new StringTokenizer(nameListValue, ",");

		int inx=0;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String name = token.trim();
			
			if (name.isEmpty()) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element is empty").toString();
				throw new PartConfigurationException(nameListKey, errorMessage);
			}
			
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(name)) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element has a leading or tailing white space").toString();
				throw new PartConfigurationException(nameListKey, errorMessage);
			}
						
			if (nameList.contains(name)) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element[").append(name)
						.append("] already was registered").toString();
				throw new PartConfigurationException(nameListKey, errorMessage);
			}
			
			final T partConfiguration;
			try {
				partConfiguration = clazz.getDeclaredConstructor(String.class).newInstance(name);
			} catch (Exception e) {
				String errorMessage = new StringBuilder()
						.append("fail to create a instance of the ")
						.append(clazz.getName())
						.append(" class of the ")
						.append(partName).append(" list[")
						.append(inx)
						.append("]'s element[").append(name)
						.append("]").toString();
				throw new PartConfigurationException(nameListKey, errorMessage);
			}
			
			partConfiguration.fromProperties(sourceSequencedProperties);
			
			nameList.add(name);
			partConfigurationHash.put(name, partConfiguration);
			inx++;
		}
	}
	
	@Override
	public String getPartName() {
		return partName;
	}

	@Override
	public void checkForDependencies() throws PartConfigurationException {	
		
		for (String name : nameList) {
			PartConfigurationIF partConfiguration = partConfigurationHash.get(name);
			partConfiguration.checkForDependencies();
		}
	}
	
	private String convertNameListToNameListValue() {
		StringBuilder nameListValueBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String name : nameList) {
			if (isFirst) {
				isFirst = false;
			} else {
				nameListValueBuilder.append(", ");
			}

			nameListValueBuilder.append(name);
		}

		return nameListValueBuilder.toString();
	}

	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		
		targetSequencedProperties.put(nameListKey, convertNameListToNameListValue());
		
		for (String name : nameList) {
			PartConfigurationIF partConfiguration = partConfigurationHash.get(name);
			partConfiguration.toProperties(targetSequencedProperties);
		}
	}
	
	public List<String> getNameList() {
		return nameList;
	}	
	
	public T getProjectPartConfiguration(String name) {
		return partConfigurationHash.get(name);
	}
	
	public void addProjectPartConfiguration(String name, T newPartConfiguration)
			throws IllegalArgumentException {
		
		if (null == name) {
			String errorMessage = "the paramter name is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (name.isEmpty()) {
			String errorMessage = "the paramter name is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(name)) {
			String errorMessage = "the paramter name is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (nameList.contains(name)) {
			String errorMessage = new StringBuilder()
					.append("the ").append(partName).append(" list's element[").append(name)
					.append("] already was registered").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == newPartConfiguration) {
			String errorMessage = "the paramter newPartConfiguration is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		nameList.add(name);
		partConfigurationHash.put(name, newPartConfiguration);
	}
	
	public boolean removeProjectPartConfiguration(String name) {
		if (null == name) {
			String errorMessage = "the paramter name is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (name.isEmpty()) {
			String errorMessage = "the paramter name is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! nameList.contains(name)) {
			return false;
		}
		
		nameList.remove(name);
		partConfigurationHash.remove(name);
		
		return true;
	}
	
}
