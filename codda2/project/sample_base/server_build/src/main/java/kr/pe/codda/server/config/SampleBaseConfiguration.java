package kr.pe.codda.server.config;


import kr.pe.codda.common.config.part.PartConfigurationIF;
import kr.pe.codda.common.config.part.RunningProjectConfiguration;
import kr.pe.codda.common.exception.PartConfigurationException;
import kr.pe.codda.common.util.SequencedProperties;

public class SampleBaseConfiguration extends RunningProjectConfiguration {
	private JDFPartConfiguration jdfPartConfiguration = new JDFPartConfiguration();
	

	public JDFPartConfiguration getJDFPartConfiguration() {
		return jdfPartConfiguration;
	}
	
	@Override
	public void fromProperties(SequencedProperties sourceSequencedProperties)
			throws IllegalArgumentException, PartConfigurationException {
		jdfPartConfiguration.fromProperties(sourceSequencedProperties);
		super.fromProperties(sourceSequencedProperties);
	}
	
	@Override
	public void checkForDependencies() throws PartConfigurationException {
		jdfPartConfiguration.checkForDependencies();
		super.checkForDependencies();
	}
	
	@Override
	public void toProperties(SequencedProperties targetSequencedProperties)
			throws IllegalArgumentException, IllegalStateException {
		jdfPartConfiguration.toProperties(targetSequencedProperties);
		super.toProperties(targetSequencedProperties);
	}
	
	@Override
	public PartConfigurationIF createNewPartConfiguration(String partName) {	
		if (JDFPartConfiguration.partName.equals(partName)) {
			return new JDFPartConfiguration();
		} else {
			return super.createNewPartConfiguration(partName);
		}
	}
}
