package idv.heimlich.IntegrationTesting.common.evn;

public class EVNConfigProducer {
	
	/**
	 * 取得設定檔案
	 * 
	 * @return
	 */
	public static IEVNConfig getConfig() {
		return getFactory(EVNSource.def_properties).getConfig();
	}

	public static EVNConfigFactory getFactory() {
		return getFactory(EVNSource.def_properties);
	}
	
	public static EVNConfigFactory getFactory(EVNSource type) {
		return type.getFactory();
	}

}
