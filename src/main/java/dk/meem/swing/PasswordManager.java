/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package dk.meem.swing;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PasswordManager extends JPanel implements ActionListener {
	private static final long serialVersionUID = -5470726773076828825L;
	private JTable table;
	private JFileChooser fc;
	private PasswordTableModel tablemodel;

	private String filename = null;
	private final char[] password = "MegetHemmeligtKodeord".toCharArray();  // Should come from user input.

	private int selectedRowid = -1;
	
    public PasswordManager() {
        super(new GridLayout(1,0));
                
        tablemodel = new PasswordTableModel();
        table = new JTable(tablemodel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFillsViewportHeight(true);

        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
            	System.out.println("We got fired...");
                if (e.getType() == TableModelEvent.INSERT) {
                    int last = table.getModel().getRowCount() - 1;
                    Rectangle r = table.getCellRect(last, 0, true);
                    table.scrollRectToVisible(r);
                }
            }
        });

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectedRowid = table.getSelectedRow();
                System.out.println("Selectrow = " + selectedRowid);
            }
        });
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	System.out.println("Mouse was clicked..." + e.getButton() + ", " + e.getID());
            }
        });
        
      /*  JButton btnAddRow = new JButton("Add Row");
        btnAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //tablemodel.addRow(new Object[]{"new row"});
            	System.out.println("Mouse was clicked...");
            }
        });
        */
    }

    public static char[] getPassword() {
        JPanel panel = new JPanel();
        final JPasswordField passwordField = new JPasswordField(20);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            @Override
            public void selectInitialValue() {
                passwordField.requestFocusInWindow();
            }
        };
        pane.createDialog(null, "Password").setVisible(true);
        //return passwordField.getPassword().length == 0 ? null : new String(passwordField.getPassword());
        return passwordField.getPassword();
    }
    
    public JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		//menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
		
		menu.add(this.addItem(KeyEvent.VK_S, Constants.SAVEDATA));
		menu.add(this.addItem(KeyEvent.VK_K, Constants.SAVEDATAAS));
		menu.add(this.addItem(KeyEvent.VK_O, Constants.OPENDATA));
		menu.add(this.addItem(KeyEvent.VK_N, Constants.NEWROW));
		menu.add(this.addItem(KeyEvent.VK_D, Constants.DELETEROW));
		
		menuBar.add(menu);

		return menuBar;
    }

    private JMenuItem addItem(int keyevent, String text) {
		JMenuItem item = new JMenuItem(text, keyevent);
		item.setMnemonic(keyevent);
		//item.setAccelerator(KeyStroke.getKeyStroke(keyevent, ActionEvent.ALT_MASK));
		item.setAccelerator(KeyStroke.getKeyStroke(keyevent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(this);
		return item;		
    }

    public void actionPerformed(ActionEvent e) {
		System.out.println("Action performed: " + e.getActionCommand());

		if (e.getActionCommand().equals(Constants.NEWROW)) {
			tablemodel.add();
		} else if (e.getActionCommand().equals(Constants.SAVEDATA)) {
			try {
				if (filename != null) {
					this.storeData(filename);
				} else {
					JOptionPane.showMessageDialog(null, "File needs a name, use save as...");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getActionCommand().equals(Constants.SAVEDATAAS)) {
			try {
				//FileFilter filter = new FileNameExtensionFilter(".bin", "bin");
				fc = new JFileChooser();
				// fc.addChoosableFileFilter(filter);
				int reply = fc.showSaveDialog(this);

				if (reply == JFileChooser.APPROVE_OPTION) {
					if (fc.getSelectedFile().isFile()) {
						JOptionPane.showMessageDialog(null, "File already exists.");
					} else {
						filename = fc.getSelectedFile().getAbsolutePath();
						this.storeData(filename);
					}
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Failure when opening chosen file: " + e1.getMessage());
			}
		} else if (e.getActionCommand().equals(Constants.OPENDATA)) {
			File file = null;
			try {
				FileFilter filter = new FileNameExtensionFilter(".bin", "bin");
				fc = new JFileChooser();
				fc.addChoosableFileFilter(filter);
				int reply = fc.showOpenDialog(this);

				if (reply == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					System.out.println("Reply=" + reply + " File=" + file.getAbsolutePath());
					this.restoreData(file.getAbsolutePath());
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Failure when opening chosen file: " + e1.getMessage());
			}
		} else if (e.getActionCommand().equals(Constants.DELETEROW)) {
			if (selectedRowid < 0) {
				JOptionPane.showMessageDialog(null, Constants.CHOOSEROW);
			} else {
				if (JOptionPane.showConfirmDialog(null, "Vil du slette række " + (selectedRowid + 1), "Slet række.",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					tablemodel.remove(selectedRowid);
				}
			}
		}
	}

    public void storeData(String serialisedFilename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException {
    	Cipher cipher = getCipher(password, Cipher.ENCRYPT_MODE);
		SealedObject so = new SealedObject(tablemodel.getTableData(), cipher);

		FileOutputStream os = new FileOutputStream(serialisedFilename);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(so);
		oos.close();
    }
    
    public void restoreData(String serialisedFilename) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		if (Files.isReadable(Paths.get(serialisedFilename))) {
			Cipher cipher = getCipher(password, Cipher.DECRYPT_MODE);

			FileInputStream is = new FileInputStream(serialisedFilename);
			ObjectInputStream ois = new ObjectInputStream(is);
			SealedObject so = (SealedObject) ois.readObject();
			ois.close();

			tablemodel.setTableData(so.getObject(cipher));
		} else {
			JOptionPane.showMessageDialog(null, "Password file (" + serialisedFilename + ") not found.");
		}
    }

    /* This is the first suggestion, using Blowfish. 
     * Not used.
     */
	private Cipher getCipher_v1(int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		Cipher ciph = Cipher.getInstance("Blowfish");
		SecretKey key = new SecretKeySpec( new byte[] { 0x09, 0x04, 0x06, 0x02, 0x03, 0x08, 0x06, 0x07 }, "Blowfish" );
		ciph.init(mode, key);
		
		return ciph;
	}

	/* This is my own suggestion.
	 * I'm not sure how to handle salt in this case.
	 * Also, how do I handle IV - is that handled by SealedObject?
	 */
	private Cipher getCipher(char[] password, int mode) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException {
		//SecureRandom random = new SecureRandom();
	    byte salt[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15 };
	    //random.nextBytes(salt);
	    
	    /* Derive the key, given password and salt. */
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 655536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

		Cipher ciph = Cipher.getInstance("AES");
		ciph.init(mode, key);
		
		return ciph;
	}
    

/*    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JSplitPane splitPane = new JSplitPane();
        frame.getContentPane().add(splitPane);

        scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(100, 2));
        splitPane.setLeftComponent(scrollPane);

        tableModel = new DefaultTableModel(new Object[]{"Stuff"},0);
        table = new JTable(tableModel);
        scrollPane.setViewportView(table);
        table.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.INSERT) {
                    int last = table.getModel().getRowCount() - 1;
                    Rectangle r = table.getCellRect(last, 0, true);
                    table.scrollRectToVisible(r);
                }
            }
        });

        JButton btnAddRow = new JButton("Add Row");
        btnAddRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableModel.addRow(new Object[]{"new row"});
            }
        });
        splitPane.setRightComponent(btnAddRow);
    }
*/

}