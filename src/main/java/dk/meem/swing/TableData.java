package dk.meem.swing;

import java.util.ArrayList;
import java.util.List;

public class TableData implements java.io.Serializable {
	private static final long serialVersionUID = 1152809874465238337L;
	
	private List<RowData> data;
	
	public TableData() {
		data = new ArrayList<RowData>();
	}
	
	public void addRow(RowData newrow) {
		data.add(newrow);
	}
	
	public void removeRow(int rowidx) {
		data.remove(rowidx);
	}

	public RowData getRow(int rowidx) {
		return data.get(rowidx);
	}

	public int getColumnCount() {
		return RowData.getNames().length;
	}
	
	public String getColumnName(int colidx) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableData other = (TableData) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	
}
