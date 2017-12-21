package dk.meem.swing;

import java.util.ArrayList;
import java.util.List;

public class TableData implements java.io.Serializable {
	private static final long serialVersionUID = 1152809874465238337L;
	
	private List<RowData> data;
	
	public TableData() {
		data = new ArrayList<RowData>();
	}
	
	public void addRow() {
		data.add(new RowData());
	}
	
	public void removeRow(int rowidx) {
		data.remove(rowidx);
	}
	
	public int getColumnCount() {
		return RowData.getNames().length;
	}
	
	public static String getColumnName(int colidx) {
		return RowData.getNames()[colidx];
	}

	public int getRowCount() {
		return this.data.size();
	}

	public Object getValueAt(int rowidx, int colidx) {
		return data.get(rowidx).getColumn(colidx);
	}

	public void setValueAt(Object value, int rowidx, int colidx) {
		RowData row = data.get(rowidx);
		row.setValue(colidx, (String)value);
		data.set(rowidx,  row);
	}

	@Override
	public String toString() {
		String out = "";
		for (RowData row : data) {
			out += row.toString();
		}
		
		return out;
	}
}
