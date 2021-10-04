package idv.heimlich.IntegrationTesting.common.tester.dto;

public class CheckExcelDataField {
	
	private String name;
	private String value;
	private boolean isQuery = false;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isQuery() {
		return this.isQuery;
	}

	public void setQuery(boolean isQuery) {
		this.isQuery = isQuery;
	}

	public String toString() {
		return "CheckExcelDataField [name=" + this.name + ", value="
				+ this.value + ", isQuery=" + this.isQuery + "]";
	}

}
