package dk.meem.swing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class RowDataTest {

	@Test
	public void test() {
		RowData row = new RowData();

		assert(row.maxSize() > 0);
		
		try {
		for (int i=0; i<row.maxSize(); i++) {
			row.setValue(i, "Text" + i);
			assertEquals(row.getColumn(i), "Text" + i);
			assert(row.getColumnName(i) instanceof String);
		}
		} catch (Exception e) {
			fail("Exception thrown during test: " + e.getMessage());
		}
	}
	
	@Test public void testSerialization() {
		String tmpfilename = "tempdata.ser";
		
		try {
			RowData row = new RowData();

			for (int i = 0; i < row.maxSize(); i++) {
				row.setValue(i, "Text" + i);
			}

			FileOutputStream fos = new FileOutputStream(tmpfilename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(row);
			oos.close();
		} catch (Exception ex) {
			fail("Exception thrown during test: " + ex.toString());
		}

		try {
			FileInputStream fis = new FileInputStream(tmpfilename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			RowData row = (RowData)ois.readObject();
			ois.close();
			
			for (int i = 0; i < row.maxSize(); i++) {
				assertEquals(row.getColumn(i), "Text"+i);
			}
			
			new File(tmpfilename).delete();
			
			
		} catch (Exception ex) {
			fail("Exception thrown during test: " + ex.toString());
		}
	}

}
