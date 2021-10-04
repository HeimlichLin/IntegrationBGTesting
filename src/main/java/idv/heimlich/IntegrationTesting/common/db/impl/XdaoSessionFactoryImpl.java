package idv.heimlich.IntegrationTesting.common.db.impl;

import idv.heimlich.IntegrationTesting.common.db.DBconfig;
import idv.heimlich.IntegrationTesting.common.db.XdaoSessionFactory;
import idv.heimlich.IntegrationTesting.common.db.XdaoSessionManager;
import idv.heimlich.IntegrationTesting.common.log.LogFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.tradevan.common.db.DoXdaoSession;

public class XdaoSessionFactoryImpl implements XdaoSessionFactory {
	
	private static Map<String, DBconfig> SESSIONMANAGERMAPPER;
	
	static {
		final Map<String, DBconfig> sessionManagerMapper = new HashMap<String, DBconfig>();
		Set<DBconfig> set = EnumSet.allOf(DBconfig.class);
		for (DBconfig configSet : set) {
			sessionManagerMapper.put(configSet.name(), configSet);
		}
		SESSIONMANAGERMAPPER = Collections.unmodifiableMap(sessionManagerMapper);
	}

	@Override
	public DoXdaoSession getXdaoSession(String conn) {
		final String connid = StringUtils.defaultIfEmpty(conn, XdaoSessionManager.PROP_DEFAULT_CONN_ID);
		DBconfig dbConfig = SESSIONMANAGERMAPPER.get(connid);
		Objects.requireNonNull(connid, "無此定義coonid" + conn);
		LogFactory.getInstance().debug("use connid:" + connid);
		return (DoXdaoSession) dbConfig.getXdaoSession();
	}

}