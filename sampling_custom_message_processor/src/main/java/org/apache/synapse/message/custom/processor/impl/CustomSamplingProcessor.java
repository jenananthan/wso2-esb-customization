package org.apache.synapse.message.custom.processor.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.registry.api.Registry;
import org.wso2.carbon.registry.api.RegistryException;
import org.wso2.carbon.registry.api.Resource;

import org.apache.synapse.message.processor.impl.sampler.SamplingProcessor;;

public class CustomSamplingProcessor extends SamplingProcessor {
	private final static String REGISTRY_PATH = "reg";

	@Override
	public void setParameters(Map<String, Object> parameters) {
		if (parameters.get(REGISTRY_PATH) != null) {
			String resourcePath = (String) parameters.get(REGISTRY_PATH);
			parameters = populateParamsFromReg(resourcePath, parameters);
		}
		System.out.println("############################## set params################");
		super.setParameters(parameters);
	}

	private Map<String, Object> populateParamsFromReg(String resourcePath, Map<String, Object> parameters) {
		Properties prop = readPropertyFile(resourcePath);
		if (prop != null) {
			for (String key : prop.stringPropertyNames()) {
				String value = prop.getProperty(key);
				parameters.put(key, value);
			}
		}
		return parameters;
	}

	private Properties readPropertyFile(String resourcePath) {
		Properties prop = null;
		try {
			CarbonContext cCtx = CarbonContext.getThreadLocalCarbonContext();
			Registry registry = cCtx.getRegistry(RegistryType.USER_GOVERNANCE);
			Resource resource = registry.get(resourcePath);
			Object content = resource.getContent();
			String output = new String((byte[]) content);
			System.out.println(output);
			prop = parseProperties(output);
		} catch (RegistryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}

	public Properties parseProperties(String fileContent) throws IOException {
		final Properties properties = new Properties();
		properties.load(new StringReader(fileContent));
		return properties;
	}
}
