package dk.meem.swing;

import java.util.ArrayList;
import java.util.List;

public class RowData implements java.io.Serializable {
	private static final long serialVersionUID = 1973625176498757216L;
	private static final String[] names = { "Name", "URL", "UserID", "Password" };
	private List<String> rowdata;
	
	public RowData() {
		this.rowdata = new ArrayList<String>();
		
		this.rowdata.add(null);
		this.rowdata.add(null);
		this.rowdata.add(null);
		this.rowdata.add(null);
	}

	public RowData(String name, String url, String userid, String password) {
		this.rowdata = new ArrayList<String>();
		
		this.rowdata.add(name);
		this.rowdata.add(url);
		this.rowdata.add(userid);
		this.rowdata.add(password);
	}

	public static String[] getNames() {
		return names;
	}
	
	public int maxSize() {
		return names.length;
	}
	
	public static String getColumnName(int idx) {
		return names[idx];
	}
	
	public String getColumn(int idx) {
		return this.rowdata.get(idx);
	}
	
	public void setValue(int idx, String newvalue) {
		this.rowdata.set(idx,  newvalue);
	}
	
	public String toString() {
		return String.join(",", rowdata);
	}
}
