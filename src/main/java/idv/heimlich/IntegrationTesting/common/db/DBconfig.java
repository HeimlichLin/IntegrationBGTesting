package idv.heimlich.IntegrationTesting.common.db;

import com.tradevan.taurus.xdao.XdaoSession;

public enum DBconfig {
	
	PFTZBPool {
		@Override
		public AbstractXdaoSessionManager getXdaoSessionManager() {
			return new XdaoSessionFTZBManager();
		}
		
	},
	PCLMSPool {
		@Override
		public AbstractXdaoSessionManager getXdaoSessionManager() {
			return new XdaoSessionManager();
		}
	
	},

	;
	final String connid;

	private DBconfig() {
		this.connid = name();
	}

	 public abstract AbstractXdaoSessionManager getXdaoSessionManager();
	 
	 public XdaoSession getXdaoSession(){
		 return this.getXdaoSessionManager().getXdaoSession();
	 }

}
