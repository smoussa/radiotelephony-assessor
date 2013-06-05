package comp2012.model.db;

public class RelationalFilter implements Filter {

	private String table;
	private String column;
	private String operator;
	private String value;

	public RelationalFilter(String table, String column, String operator,
			String value) {
		this.table = table;
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	@Override
	public String getWhere() {
		return table + "." + column + " " + operator + " " + value;
	}

}
