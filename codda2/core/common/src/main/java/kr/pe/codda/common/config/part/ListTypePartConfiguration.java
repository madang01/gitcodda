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
	
	private final String subPartNameListKey;
	// private final String subPartNameListItemViewTypeKey;
	
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
		
		
		subPartNameListKey = new StringBuilder().append(partName)
				.append(RunningProjectConfiguration.SUB_PART_NAME_LIST_KEY_SECOND_PREFIX)
				.append(".value").toString();
		
		
	}
	

	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		
		String subPartNameListValue = sourceSequencedProperties.getProperty(subPartNameListKey);

		if (null == subPartNameListValue) {
			String errorMessage = new StringBuilder()
					.append("the ")
					.append(partName)
					.append(" list key(=").append(subPartNameListKey)
					.append(") was not found in the parameter sourceSequencedProperties").toString();
			throw new PartConfigurationException(subPartNameListKey, errorMessage);
		}

		subPartNameListValue = subPartNameListValue.trim();
		nameList.clear();
		partConfigurationHash.clear();
		
		StringTokenizer tokens = new StringTokenizer(subPartNameListValue, ",");

		int inx=0;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			String name = token.trim();
			
			if (name.isEmpty()) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element is empty").toString();
				throw new PartConfigurationException(subPartNameListKey, errorMessage);
			}
			
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(name)) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element has a leading or tailing white space").toString();
				throw new PartConfigurationException(subPartNameListKey, errorMessage);
			}
						
			if (nameList.contains(name)) {
				String errorMessage = new StringBuilder()
						.append("the ").append(partName).append(" list[")
						.append(inx)
						.append("]'s element[").append(name)
						.append("] already was registered").toString();
				throw new PartConfigurationException(subPartNameListKey, errorMessage);
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
				throw new PartConfigurationException(subPartNameListKey, errorMessage);
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
	
	private String convertSubPartNameListToSubPartNameListValue() {
		StringBuilder subPartNameListValueBuilder = new StringBuilder();
		boolean isFirst = true;
		for (String name : nameList) {
			if (isFirst) {
				isFirst = false;
			} else {
				subPartNameListValueBuilder.append(", ");
			}

			subPartNameListValueBuilder.append(name);
		}

		return subPartNameListValueBuilder.toString();
	}

	@Override
	public void toProperties(SequencedProperties targetSequencedProperties) throws IllegalArgumentException {
		
		targetSequencedProperties.put(subPartNameListKey, convertSubPartNameListToSubPartNameListValue());
		
		String subPartNameListItemViewTypeKey = new StringBuilder().append(partName)
				.append(RunningProjectConfiguration.SUB_PART_NAME_LIST_KEY_SECOND_PREFIX)
				.append(".item_view_type").toString();
		
		targetSequencedProperties.put(subPartNameListItemViewTypeKey, "list");
		
		
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
