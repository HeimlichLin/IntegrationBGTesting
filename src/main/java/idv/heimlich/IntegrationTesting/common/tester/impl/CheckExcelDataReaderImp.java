package idv.heimlich.IntegrationTesting.common.tester.impl;

import idv.heimlich.IntegrationTesting.common.log.LogFactory;
import idv.heimlich.IntegrationTesting.common.tester.CheckDataCompoment;
import idv.heimlich.IntegrationTesting.common.tester.dto.CheckExcelDataDTO;
import idv.heimlich.IntegrationTesting.common.tester.dto.CheckExcelDataField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.tradevan.common.db.DoXdaoSession;
import com.tradevan.common.db.utils.sql.SqlBuider;
import com.tradevan.common.exception.ApBusinessException;
import com.tradevan.common.object.FileLoader;
import com.tradevan.commons.collection.DataList;
import com.tradevan.taurus.xdao.XdaoException;

/**
 * 檢查EXCEL檔案
 */
public class CheckExcelDataReaderImp implements CheckDataCompoment {

	final String filePath;
	final String outFile;
	final DoXdaoSession doXdaoSession;

	public CheckExcelDataReaderImp(String filePath, String outFile, DoXdaoSession doXdaoSession) {
		this.filePath = filePath;
		this.outFile = outFile;
		this.doXdaoSession = doXdaoSession;
	}

	private List<CheckExcelDataDTO> load(String filePath) throws Exception {
		final File file = FileLoader.getResourcesFile(SqlBuider.class, filePath);
		final FileInputStream fileInputStream = new FileInputStream(file);//
		final Workbook wb = new HSSFWorkbook(fileInputStream);
		final int nuber = wb.getNumberOfSheets();
		final List<CheckExcelDataDTO> checkExcelDataDTOs = new ArrayList<CheckExcelDataDTO>();
		for (int i = 0; i < nuber; i++) {
			final String sheetString = wb.getSheetName(i);
			if (wb.isSheetVeryHidden(i)) {
				continue;
			}
			if (!(sheetString.startsWith("＠"))) {
				continue;
			}
			final Sheet hssfsheet = wb.getSheetAt(i);
			final int rowNo = hssfsheet.getLastRowNum();
			final Row row0 = hssfsheet.getRow(0);

			final List<CheckExcelDataField> checkExcelDataFieldList = new ArrayList<CheckExcelDataField>();
			for (int j = 0; j < 100; j++) {
				final Cell cell = row0.getCell((short) j);
				if (cell != null) {
					if (StringUtils.isBlank(cell.getStringCellValue())) {
						break;
					}
					final CheckExcelDataField checkExcelDataField = new CheckExcelDataField();
					checkExcelDataField.setName(cell.getStringCellValue());
					checkExcelDataFieldList.add(checkExcelDataField);
				} else {
					break;
				}
			}

			final Row row1 = hssfsheet.getRow(1);
			for (int j = 0; j < checkExcelDataFieldList.size(); j++) {
				final Cell cell = row1.getCell((short) j);
				if (cell != null) {
					final String isQueryString = StringUtils.defaultString(cell.getStringCellValue()).trim();
					final CheckExcelDataField checkExcelDataField = checkExcelDataFieldList.get(j);
					checkExcelDataField.setQuery(StringUtils.isNotBlank(isQueryString));
				}
			}

			for (int j = 2; j <= rowNo; j++) {
				final CheckExcelDataDTO checkexceldatadto = new CheckExcelDataDTO(sheetString.replace("＠", ""));
				checkExcelDataDTOs.add(checkexceldatadto);
				final Row row = hssfsheet.getRow(j);
				if (row == null) {
					break;
				}
				final List<CheckExcelDataField> checkExcelDataFields = new ArrayList<CheckExcelDataField>();
				for (int k = 0; k < checkExcelDataFieldList.size(); k++) {
					final Cell cell = row.getCell((short) k);
					final CheckExcelDataField checkExcelDataField = checkExcelDataFieldList.get(k);
					final CheckExcelDataField newCheckExcelDataField = new CheckExcelDataField();
					newCheckExcelDataField.setName(checkExcelDataField.getName());
					newCheckExcelDataField.setQuery(checkExcelDataField.isQuery());
					newCheckExcelDataField.setValue(getValue(cell));
					checkExcelDataFields.add(newCheckExcelDataField);

				}
				checkexceldatadto.getCheckExcelDataField().addAll(checkExcelDataFields);
			}

		}

		return checkExcelDataDTOs;
	}

	@SuppressWarnings("deprecation")
	public void checkFile(List<CheckExcelDataDTO> CheckExcelDataDTOs) throws Throwable {
		Throwable throwable = null;

		final File file = FileLoader.getResourcesFile(SqlBuider.class, this.filePath);
		final FileInputStream fileInputStream = new FileInputStream(file);//
		final Workbook wb = new HSSFWorkbook(fileInputStream);
		final Sheet sheet = wb.createSheet("產出結果");
		LogFactory.getInstance().info("報告產出位置：" + this.outFile);
		int i = 0;

		final Row row0 = sheet.createRow(0);
		final Cell cell0Head = row0.createCell(0);
		cell0Head.setCellType(CellType.STRING);
		cell0Head.setCellValue("TABLE");
		final Cell cell1Head = row0.createCell(1);
		cell1Head.setCellType(CellType.STRING);
		cell1Head.setCellValue("檢查結果");
		final Cell cell2Head = row0.createCell(2);
		cell2Head.setCellType(CellType.STRING);
		cell2Head.setCellValue("檢查結果");

		for (final CheckExcelDataDTO checkExcelDataDTO : CheckExcelDataDTOs) {
			final Row row = sheet.createRow(i + 1);

			final Cell cell0 = row.createCell(0);
			cell0.setCellType(CellType.STRING);
			cell0.setCellValue(checkExcelDataDTO.getTable());

			final Cell cell1 = row.createCell(1);
			cell1.setCellType(CellType.STRING);
			cell1.setCellValue(checkExcelDataDTO.toString() + ";");

			final Cell cell2 = row.createCell(2);
			cell2.setCellType(CellType.STRING);
			try {
				this.checkTrueOrFalse(this.doXdaoSession, checkExcelDataDTO);
				cell2.setCellValue("Y");
			} catch (final Exception e) {
				throwable = e;
				cell2.setCellValue("N");
			}
			i++;
		}

		final FileOutputStream fOut = new FileOutputStream(this.outFile);
		wb.write(fOut);
		fOut.flush();
		fOut.close();

		if (throwable != null) {
			throw throwable;
		}

	}

	private void checkTrueOrFalse(DoXdaoSession xdaoSession, CheckExcelDataDTO checkExcelDataDTO) throws XdaoException {

		
		final DataList dataList = xdaoSession.select(checkExcelDataDTO.getTable(), checkExcelDataDTO.getSqlWhere());
		if (dataList.size() == 0) {
			LogFactory.getInstance().debug("[x]select * from {} where {}", checkExcelDataDTO.getTable(), checkExcelDataDTO.getSqlWhere());
			throw new ApBusinessException("檢查發現錯誤=" + checkExcelDataDTO.getTable() + " sql where:"
					+ checkExcelDataDTO.getSqlWhere());

		}
		LogFactory.getInstance().debug("[o]select * from {} where {}", checkExcelDataDTO.getTable(), checkExcelDataDTO.getSqlWhere());
	}

	private String getValue(Cell cell) {
		String retString = "";
		if (cell == null) {
			retString = "";
		} else {
			final CellType cellType = cell.getCellType();
			if (cellType.equals(CellType.NUMERIC)) {
				final double d = cell.getNumericCellValue();
				retString = new BigDecimal(d).toPlainString();
			} else if (cellType.equals(CellType.STRING)) {
				retString = cell.getStringCellValue();
			} else if (cellType.equals(CellType.FORMULA)) {
				retString = cell.getStringCellValue();
			} else if (cellType.equals(CellType.BLANK)) {
				retString = "";
			} else if (cellType.equals(CellType.BOOLEAN)) {
				retString = cell.getBooleanCellValue() + "";
			} else if (cellType.equals(CellType.ERROR)) {
				retString = "";
			}
		}

		return retString.trim();
	}

	@Override
	public void check() {
		try {
			final List<CheckExcelDataDTO> checkExcelDataDTOs = this.load(this.filePath);
			this.checkFile(checkExcelDataDTOs);
		} catch (final ApBusinessException e) {
			throw e;
		} catch (final Throwable e) {
			throw new ApBusinessException("錯誤", e);
		}
	}

}
