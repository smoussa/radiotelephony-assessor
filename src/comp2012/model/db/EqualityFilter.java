package comp2012.model.db;

public class EqualityFilter implements Filter {

	private String table;
	private String column;
	private String value;

	public EqualityFilter(String table, String column, String value) {
		this.table = table;
		this.column = column;
		this.value = value;
	}

	@Override
	public String getWhere() {
		return table + "." + column + " = \"" + value + "\"";
	}

}
