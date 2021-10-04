package idv.heimlich.IntegrationTesting.common.tester.utils;

import com.tradevan.common.db.DoXdaoSession;

import idv.heimlich.IntegrationTesting.common.tester.CheckDataCompoment;
import idv.heimlich.IntegrationTesting.common.tester.impl.CheckExcelDataReaderImp;

public class CheckDataLookupHelper {
	
	public static void check(String file, String outFile, DoXdaoSession doXdaoSession) {
		final CheckDataCompoment CheckDataCompoment = new CheckExcelDataReaderImp(file, outFile,  doXdaoSession);
		CheckDataCompoment.check();
	}

}
