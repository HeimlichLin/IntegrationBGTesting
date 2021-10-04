package idv.heimlich.IntegrationTesting.common.evn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class EVNConfig implements IEVNConfig {

	private Properties properties;
	
	@Override
	public String getSshEncoding() {
		return getString("ssh.encoding", "Big5");
	}

	@Override
	public String getConnectionIP() {
		return getString("bp.connectionIP");
	}

	@Override
	public String getUserName() {
		return getString("bp.userName");
	}

	@Override
	public String getPassword() {
		return getString("bp.password");
	}

	@Override
	public String getBaseDir() {
		return getString("base_dir");
	}

	private String getString(String key, String def) {
		String value = this.getString(key);
		return StringUtils.defaultString(value, def);
	}

	private String getString(String key) {
		final Properties properties = inti();
		final String value = ((String) properties.get(key)).trim();
		System.out.println(key + ":" + value);
		return value.trim();
	}

	public Properties inti() {
		if (this.properties == null) {
			final ClassLoader classLoader = EVNConfig.class.getClassLoader();
			final InputStream io = classLoader
					.getResourceAsStream("evn.properties");
			this.properties = new Properties();
			try {
				this.properties.load(new InputStreamReader(io, "utf-8"));
			} catch (final FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		return this.properties;
	}

}
