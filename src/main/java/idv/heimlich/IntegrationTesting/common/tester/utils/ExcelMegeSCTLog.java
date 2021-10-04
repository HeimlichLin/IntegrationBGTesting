package idv.heimlich.IntegrationTesting.common.tester.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 檔案匯入SCT檔案
 */
public class ExcelMegeSCTLog {
	
	@SuppressWarnings("deprecation")
	public void merge(File folder) throws IOException {
		File excelPath = folder;
		String realName = "";
		File logFile = null;
		for (final File f : folder.getParentFile().listFiles()) {
			if (f.getName().contains("xls")) {
				excelPath = f;
				realName = f.getName().replace(".xls", "");
			} else if (f.getName().contains("log")) {
				logFile = f;
			}
		}
		if (excelPath != null && logFile != null) {
			final List<String> str = FileUtils.readLines(logFile, "utf-8");
			final FileInputStream fileInputStream = new FileInputStream(excelPath);//
			final Workbook wb = new HSSFWorkbook(fileInputStream);
			final Sheet sheet = wb.createSheet("SCT執行紀錄");
			final Row row0 = sheet.createRow(0);
			final Cell cell = row0.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue("執行命令");

			int row = 1;
			for (final String line : str) {
				final Row rowUnit = sheet.createRow(row);
				final Cell cellUtls = rowUnit.createCell(0);
				cellUtls.setCellType(CellType.STRING);
				cellUtls.setCellValue(line);
				row++;
			}
			final FileOutputStream fOut = new FileOutputStream(new File(folder.getParentFile(), "" + realName + "-sct.xls"));
			wb.write(fOut);
			fOut.flush();
			fOut.close();
		}

	}

}
