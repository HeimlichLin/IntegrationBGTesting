package idv.heimlich.IntegrationTesting.common.db;

import com.tradevan.common.db.MapperFactory;
import com.tradevan.common.db.dao.GeneralDAO;

public class PoDaoMapper implements MapperFactory {	

	@Override
	public <PO> GeneralDAO<PO> lookupDAO(PO arg0) {
		return TableMapper.lookupDAO(arg0);
	}

	@Override
	public <PO> GeneralDAO<PO> lookupDAO(Class<PO> arg0) {
		return  TableMapper.lookupDAO(arg0);
	}

}
