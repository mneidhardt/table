package dk.meem.swing;

import static org.junit.Assert.*;
import org.junit.Test;

public class TableDataTest {

	@Test
	public void test() {
		TableData td = new TableData();
		assertEquals(td.getRowCount(), 0);
		td.addRow(new RowData());
		assertEquals(td.getRowCount(), 1);
		
		assertEquals(td.getColumnCount(), RowData.getNames().length);
		
		td.setValueAt("String1", 0, 0);
		td.setValueAt("String2", 0, 1);
		td.setValueAt("String3", 0, 2);
		td.setValueAt("String4", 0, 3);
		
		assertEquals(td.getValueAt(0, 0), "String1");
		
		td.addRow(new RowData());
		assertEquals(td.getRowCount(), 2);

		td.removeRow(1);
		assertEquals(td.getRowCount(), 1);
		assertEquals(td.getValueAt(0, 0), "String1");
	}
}
