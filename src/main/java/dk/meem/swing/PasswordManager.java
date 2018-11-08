package dk.meem.swing;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import java.nio.file.Files;
import java.nio.file.Paths;

public class PasswordManager extends JPanel implements ActionListener {
	private static final long serialVersionUID = -5470726773076828825L;
	private JTable table;
	private JFileChooser fc;
	private PasswordTableModel tablemodel;
	private String serialisedfilename = null;
	private final char[] password = "MegetHemmeligtKodeord".toCharArray();  // Should come from user input.
	private Serialiser serialiser = new Serialiser();
	private JFrame frame;
	private int selectedRowid = -1;
	
    public PasswordManager(JFrame frame) {
        super(new GridLayout(1,0));
        this.frame = frame;
        
        tablemodel = new PasswordTableModel();
        table = new JTable(tablemodel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 300));
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
    			int r = table.rowAtPoint(e.getPoint());
    			int c = table.columnAtPoint(e.getPoint());
    			System.out.println("Mouse " + e.getButton() + " at (" + r + "," + c + ")");

    			if (e.getButton() == 1) {
    				if (e.getClickCount() == 2 && r > -1) {
    					doEdit(r);
    				}
    			} else if (e.getButton() == 3) {
        			if (r > -1 && r < table.getRowCount()) {
        				table.setRowSelectionInterval(r, r);
        				table.setColumnSelectionInterval(c,c);
            		}
            	}
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
		menu.add(this.addItem(KeyEvent.VK_E, Constants.EDITROW));
		menu.add(this.addItem(KeyEvent.VK_N, Constants.NEWROW));
		menu.add(this.addItem(KeyEvent.VK_D, Constants.DELETEROW));

		menuBar.add(menu);

		return menuBar;
    }

    private JMenuItem addItem(int keyevent, String text) {
		JMenuItem item = new JMenuItem(text, keyevent);
		item.setMnemonic(keyevent);
		item.setAccelerator(KeyStroke.getKeyStroke(keyevent, ActionEvent.ALT_MASK));
		//item.setAccelerator(KeyStroke.getKeyStroke(keyevent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(this);
		return item;		
    }

    public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals(Constants.NEWROW)) {
			RowData newrow = this.newDataRow();
			if (newrow != null) {
				tablemodel.add(newrow);
			}
		} else if (e.getActionCommand().equals(Constants.EDITROW) && selectedRowid > -1) {
			doEdit(selectedRowid);
		} else if (e.getActionCommand().equals(Constants.SAVEDATA) && serialisedfilename != null) {
			try {
				serialiser.storeData(password, serialisedfilename, tablemodel.getTableData());
			} catch (Exception e0) {
				JOptionPane.showMessageDialog(null, Constants.ERRORSAVING + e0.getMessage());
				System.err.println("Exception: " + e0.getMessage());
			}
		} else if (e.getActionCommand().equals(Constants.SAVEDATA) || e.getActionCommand().equals(Constants.SAVEDATAAS)) {
			fc = new JFileChooser();
			int reply1 = fc.showSaveDialog(this);
			int overwrite = JOptionPane.NO_OPTION;
			
			if (reply1 == JFileChooser.APPROVE_OPTION ) {
				if (Files.exists(Paths.get(fc.getSelectedFile().getAbsolutePath()))) {
					overwrite = JOptionPane.showConfirmDialog(null, Constants.FILEEXISTS, Constants.OVERWRITE, JOptionPane.YES_NO_OPTION);
				}
				
				if (overwrite ==  JOptionPane.YES_OPTION || ! Files.exists(Paths.get(fc.getSelectedFile().getAbsolutePath()))) {
					try {
						serialiser.storeData(password, fc.getSelectedFile().getAbsolutePath(), tablemodel.getTableData());
						serialisedfilename = fc.getSelectedFile().getAbsolutePath();
					} catch (Exception e0) {
						JOptionPane.showMessageDialog(null, Constants.ERRORSAVINGAS + e0.getMessage());
						System.out.println("Exception: " + e0.getMessage());
					}
				}
			}
		} else if (e.getActionCommand().equals(Constants.OPENDATA)) {
			File file = null;
			try {
				FileFilter filter = new FileNameExtensionFilter(".bin", "bin");
				fc = new JFileChooser();
				fc.addChoosableFileFilter(filter);
				int reply2 = fc.showOpenDialog(this);

				if (reply2 == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					this.serialisedfilename = file.getAbsolutePath(); 
					TableData tmp = serialiser.restoreData(password, serialisedfilename);
					tablemodel.setTableData(tmp);
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, Constants.ERROROPENINGFILE + e1.getMessage());
			}
		} else if (e.getActionCommand().equals(Constants.DELETEROW)) {
			if (selectedRowid < 0) {
				JOptionPane.showMessageDialog(null, Constants.CHOOSEROW);
			} else {
				if (JOptionPane.showConfirmDialog(null, Constants.DELETINGROW + (selectedRowid + 1), Constants.DELETEIT,
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					int myrowid = selectedRowid;
					table.clearSelection();
					tablemodel.remove(myrowid);
				}
			}
		}

		if (serialisedfilename != null) {
			frame.setTitle("PasswordManager [" + serialisedfilename + "]");
		}

    }

    private void doEdit(int rowid) {
		RowData row = this.newDataRow(tablemodel.getRow(rowid));
		if (row != null) {
			tablemodel.setRow(rowid, row);
		}
    }

    private RowData newDataRow() {
    	return this.newDataRow(new RowData());
    }
    
    private RowData newDataRow(RowData row) {
        JTextField f1 = new JTextField(15);
        f1.setText(row.getColumn(0));
        JTextField f2 = new JTextField(15);
        f2.setText(row.getColumn(1));
        JTextField f3 = new JTextField(15);
        f3.setText(row.getColumn(2));
        JPasswordField f4 = new JPasswordField(15);
        f4.setText(row.getColumn(3));

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel(RowData.getColumnName(0)));
        myPanel.add(f1);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel(RowData.getColumnName(1)));
        myPanel.add(f2);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel(RowData.getColumnName(2)));
        myPanel.add(f3);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel(RowData.getColumnName(3)));
        myPanel.add(f4);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Add new row", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	RowData newrow = new RowData(f1.getText(), f2.getText(), f3.getText(), new String(f4.getPassword()));
        	return newrow;
        } else {
        	return null;
        }
     }    
}
