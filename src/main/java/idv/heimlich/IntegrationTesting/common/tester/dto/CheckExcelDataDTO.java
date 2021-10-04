package idv.heimlich.IntegrationTesting.common.tester.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.tradevan.taurus.xdao.SqlWhere;

public class CheckExcelDataDTO {
	
	private String table;
	private List<CheckExcelDataField> checkExcelDataField = new ArrayList<CheckExcelDataField>();

	public CheckExcelDataDTO(String table) {
		this.table = table;
	}

	public String getTable() {
		return this.table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<CheckExcelDataField> getCheckExcelDataField() {
		return this.checkExcelDataField;
	}

	public void setCheckExcelDataField(
			List<CheckExcelDataField> checkExcelDataField) {
		this.checkExcelDataField = checkExcelDataField;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from ");
		buffer.append(this.table);
		ArrayList<String> qList = new ArrayList<String>();
		Iterator<CheckExcelDataField> arg3 = this.checkExcelDataField.iterator();

		while (arg3.hasNext()) {
			CheckExcelDataField checkExcelDataField = (CheckExcelDataField) arg3
					.next();
			if (checkExcelDataField.isQuery()) {
				qList.add(checkExcelDataField.getName() + "="
						+ checkExcelDataField.getValue());
			}
		}

		buffer.append(" where " + this.getSqlWhere());
		return buffer.toString() + "\n";
	}

	public SqlWhere getSqlWhere() {
		SqlWhere sqlWhere = new SqlWhere();
		Iterator<CheckExcelDataField> arg2 = this.getCheckExcelDataField().iterator();

		while (arg2.hasNext()) {
			CheckExcelDataField checkExcelDataField = (CheckExcelDataField) arg2
					.next();
			if (checkExcelDataField.isQuery()
					&& StringUtils.isNotBlank(checkExcelDataField.getValue())) {
				sqlWhere.add(checkExcelDataField.getName(),
						checkExcelDataField.getValue());
			}
		}

		return sqlWhere;
	}

}
