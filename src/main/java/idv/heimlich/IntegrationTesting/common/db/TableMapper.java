package idv.heimlich.IntegrationTesting.common.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tradevan.common.db.dao.GeneralDAO;
import com.tradevan.common.db.utils.ObjectsUtils;
import com.tradevan.common.exception.ApBusinessException;

/**
 * TABLE對應 DAO設定
 */
public enum TableMapper {
	
//	WAREHSE(WarehsePo.class, WarehseDAOImpl.class), //
	;//
	
	final String entityClas;
	final String daoClas;
	final static Map<String, TableMapper> MAP;
	
	private <PO, T extends GeneralDAO<PO>> TableMapper(final Class<PO> entityClas, final Class<T> daoClas) {
		this.entityClas = entityClas.getName();
		this.daoClas = daoClas.getName();
	}

	static {
		final Map<String, TableMapper> map = new HashMap<String, TableMapper>();
		for (final TableMapper mapper : TableMapper.values()) {
			map.put(mapper.entityClas, mapper);
		}
		MAP = Collections.unmodifiableMap(map);
	}

	@SuppressWarnings("unchecked")
	public static <PO> GeneralDAO<PO> lookupDAO(final Class<PO> object) {
		final String daoClass = lookupDAONameByClass(object);
		return (GeneralDAO<PO>) ObjectsUtils.newInstance(daoClass);
	}
	
	@SuppressWarnings("unchecked")
	public static <PO> GeneralDAO<PO> lookupDAO(final PO object) {
		final String daoClass = lookupDAOClassName(object);
		return (GeneralDAO<PO>) ObjectsUtils.newInstance(daoClass);
	}

	private static String lookupDAOClassName(final Object object) {
		if (!MAP.containsKey(object.getClass().getName())) {
			throw new ApBusinessException("此無物件無定義" + object.getClass().getName());
		}

		return MAP.get(object.getClass().getName()).daoClas;
	}

	private static String lookupDAONameByClass(final Class<?> object) {
		if (!MAP.containsKey(object.getName())) {
			throw new ApBusinessException("此無物件無定義" + object.getName());
		}
		return MAP.get(object.getName()).daoClas;
	}

}
