package dk.meem.swing;

import javax.swing.table.AbstractTableModel;

public class PasswordTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2101063883865472832L;
	private TableData data = new TableData();

	public PasswordTableModel() {
	}

	public void add() {
		this.add(new RowData());
	}

	public void add(RowData newrow) {
		data.addRow(newrow);
		this.fireTableRowsInserted(data.getRowCount() - 1, data.getRowCount() - 1);
	}

	public RowData getRow(int rowid) {
		return data.getRow(rowid);
	}

	public void setRow(int rowid, RowData row) {
		for (int colid=0; colid<row.maxSize(); colid++) {
			this.setValueAt(row.getColumn(colid), rowid, colid);
		}
	}

	public void remove(int row) {
		data.removeRow(row);
		this.fireTableRowsDeleted(row, row);
	}

	public int getColumnCount() {
		return data.getColumnCount();
	}

	@Override
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
		return false;
	}

	public void setValueAt(Object value, int row, int col) {
		if (row > -1) {
			data.setValueAt(value, row, col);
			fireTableCellUpdated(row, col);
		}
	}
	
	public TableData getTableData() {
		return data;
	}
	
	public void setTableData(Object data) {
		this.data = (TableData)data;
		
		for (int i=0; i<this.data.getRowCount(); i++) {
			this.fireTableRowsInserted(i,i);
		}
	}
}
