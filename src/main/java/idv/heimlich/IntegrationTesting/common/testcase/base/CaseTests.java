package idv.heimlich.IntegrationTesting.common.testcase.base;

import idv.heimlich.IntegrationTesting.common.date.YYYYMMDDUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tradevan.common.object.FileLoader;

/**
 * 測試集
 */
public enum CaseTests implements ITestCase  {
	// N1
	TSFT_N1FETCH_001("N1.Z保證金繳納", "clprocN1") //
	;
	
//	static final String OUTPUT_EXCEL_TEMPLATE = "/測試報告/%s/%s/"; // D下
	static final String OUTPUT_EXCEL_TEMPLATE = "測試報告/%s/%s/"; // project下 
	static final String basePath = "test-sets"; //
	final String testNo;
	final String memo;
	final String initSQLFile;
	final String checlExcelFile;
	final String cmmdFile;
	final String outFile;
	final String outfolder;
	final List<String> commands = new ArrayList<String>();
	
	CaseTests(String memo, String callSCT) {
		this.testNo = name();
		this.memo = memo;
		this.outfolder = String.format(OUTPUT_EXCEL_TEMPLATE, YYYYMMDDUtils.getText(), name());
		this.initSQLFile = this.getFilePath(name() + ".sql");// 初始化檔案位置
		this.checlExcelFile = this.getFilePath(name() + ".xls");// 檢查檔案位置
		this.cmmdFile = this.getFilePath(name());// 命令檔案位置
		this.outFile = outfolder + "A010_單元測試規格表_" + name() + "_" + YYYYMMDDUtils.getText() + ".xls";
		// 檔名：A010_單元測試規格表_{測試代碼}_{年月日}.xls
		this.commands.add("cd /APCLMS");
		this.commands.add("oxPCLMS.sct down");
		this.commands.add("cd /APCLMS/SCT");
		this.commands.add(callSCT);
	}
	
	@Override
	public String getTestNo() {
		return testNo;
	}

	@Override
	public String getMemo() {
		return memo;
	}

	@Override
	public String getOutfolder() {
		return outfolder;
	}

	@Override
	public String getSCTlog() {
		return this.getOutfolder() + "sct-op.log";
	}
	
	/**
	 * 檔案路徑
	 * 
	 * @param file
	 * @return
	 */
	public String getFilePath(String file) {
		return basePath + "/" + this.name() + "/" + file;
	}	
	
	public String getCaseFolder() {
		return "test-sets/" + this.name() + "/";
	}
	
	public File getComFile() {
		return FileLoader.getResourcesFile(CaseTests.class, this.getCmmdFile());
	}

	public String getInitSQLFile() {
		return initSQLFile;
	}

	public String getCheclExcelFile() {
		return checlExcelFile;
	}

	public String getOutFile() {
		return outFile;
	}

	public String getCmmdFile() {
		return cmmdFile;
	}

}
