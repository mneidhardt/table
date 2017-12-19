package dk.meem.swing;

import javax.swing.table.AbstractTableModel;

import com.google.gson.Gson;

public class PasswordTableModel extends AbstractTableModel {
	private TableData data = new TableData();

	public PasswordTableModel() {
	}

	public void add() {
		data.addRow();
		this.fireTableRowsInserted(data.getRowCount() - 1, data.getRowCount() - 1);
	}

	public void remove(int row) {
		data.removeRow(row);
		this.fireTableRowsDeleted(row, row);
	}

	public int getColumnCount() {
		return data.getColumnCount();
	}

	public String getColumnName(int col) {
		return data.getColumnName(col);
	}

	public int getRowCount() {
		return data.getRowCount();
	}

	public Object getValueAt(int row, int col) {
		return data.getValueAt(row, col);
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public void setValueAt(Object value, int row, int col) {
		if (row > -1) {
			data.setValueAt(value, row, col);
			fireTableCellUpdated(row, col);
		}
	}
	
	public String getData() {
		return data.toString();
		//return "no json today";
	}

}