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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** 
 * TableDemo is just like SimpleTableDemo, except that it
 * uses a custom TableModel.
 */
public class PasswordTable extends JPanel implements ActionListener {
	private static final long serialVersionUID = -5470726773076828825L;
	private JTable table;
	private PasswordTableModel tablemodel;
	private final static String SAVEDATA = "Save data";
	private final static String NEWROW = "New row";
	private final static String DELETEROW = "Delete row";
	private int selectedRowid = -1;
	
    public PasswordTable() {
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
    
    public JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");

		//a group of JMenuItems		
		JMenuItem menuItem1 = new JMenuItem(SAVEDATA, KeyEvent.VK_S);
		menuItem1.setMnemonic(KeyEvent.VK_S);
		//menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		KeyStroke strokeSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		strokeSave = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK);
		menuItem1.setAccelerator(strokeSave);
		menuItem1.getAccessibleContext().setAccessibleDescription("Save current data");
		menuItem1.addActionListener(this);
		menu.add(menuItem1);		

		JMenuItem menuItem2 = new JMenuItem(NEWROW, KeyEvent.VK_N);
		menuItem2.setMnemonic(KeyEvent.VK_N);
		KeyStroke strokeNewRow = KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		strokeNewRow = KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK);
		menuItem2.setAccelerator(strokeNewRow);
		menuItem2.getAccessibleContext().setAccessibleDescription("Add new row");
		menuItem2.addActionListener(this);
		menu.add(menuItem2);

		JMenuItem menuItem3 = new JMenuItem(DELETEROW, KeyEvent.VK_D);
		menuItem3.setMnemonic(KeyEvent.VK_D);
		KeyStroke strokeDeleteRow = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		strokeDeleteRow = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK);
		menuItem3.setAccelerator(strokeDeleteRow);
		menuItem3.getAccessibleContext().setAccessibleDescription("Delete row");
		menuItem3.addActionListener(this);
		menu.add(menuItem3);

		menuBar.add(menu);

		return menuBar;
    }

    public void actionPerformed(ActionEvent e) {
        //...Get information from the action event...
        //...Display it in the text area...
    	System.out.println("Action performed: " + e.getActionCommand());
    	
    	if (e.getActionCommand().equals(NEWROW)) {
    		tablemodel.add();
    	} else if (e.getActionCommand().equals(SAVEDATA)) {
    		String serialised = tablemodel.getData();
    		System.out.println(serialised);
    	} else if (e.getActionCommand().equals(DELETEROW)) {
    		if (selectedRowid < 0) {
    			JOptionPane.showMessageDialog(null, "Vælg venligst en række at slette.");
    		} else {
    			if (JOptionPane.showConfirmDialog(null, "Vil du slette række " + (selectedRowid+1), "Slet række.",
    		        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
    				tablemodel.remove(selectedRowid);
    			}
    		}
    	}
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