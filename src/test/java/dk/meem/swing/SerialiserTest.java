package dk.meem.swing;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class SerialiserTest {

	@Test
	public void test() {
		char [] password = "password".toCharArray();
		String filename = "testserialiser.bin";
		
		try {
			TableData td = new TableData();
			RowData row = new RowData("col11", "col12", "col13", "col14");
			td.addRow(row);
			
			Serialiser ser = new Serialiser();

			ser.storeData(password, filename, td);
			
			TableData td2 = ser.restoreData(password, filename);
			
			assertTrue(td2 instanceof TableData);
			assertEquals(td2.getRowCount(), 1);
			assertEquals(td, td2);
			assertEquals(row, td2.getRow(0));
			
			new File(filename).delete();
			
		} catch (Exception e) {
			fail("Exception during test: " + e.getMessage());
		}
	}

}
