package dk.meem.swing;

import static org.junit.Assert.*;
import org.junit.Test;

public class PasswordTableModelTest {

	@Test
	public void test() {
		try {
			PasswordTableModel tm = new PasswordTableModel();
			assertEquals(tm.getRowCount(), 0);

			tm.add();
			assertEquals(tm.getRowCount(), 1);
			
			RowData row2 = new RowData();
			tm.add(row2);
			assertEquals(tm.getRowCount(), 2);

			RowData row3 = new RowData("first", "second", "third", "fourth");
			tm.add(row3);
			assertEquals(tm.getRowCount(), 3);
			assertEquals(tm.getValueAt(2, 0), "first");
			assertEquals(tm.getValueAt(2, 1), "second");
			assertEquals(tm.getValueAt(2, 2), "third");
			assertEquals(tm.getValueAt(2, 3), "fourth");
			
			tm.remove(0);
			assertEquals(tm.getRowCount(), 2);
			assertEquals(tm.getValueAt(1, 0), "first");
			assertEquals(tm.getValueAt(1, 1), "second");
			assertEquals(tm.getValueAt(1, 2), "third");
			assertEquals(tm.getValueAt(1, 3), "fourth");
		} catch (Exception e) {
			fail("Exception during test. " + e.getMessage());
		}
		
	}

}
