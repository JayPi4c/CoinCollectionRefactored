package com.JayPi4c.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.JayPi4c.utils.Util;

/**
 * Diese Klasse erm&oumlglicht die Tabellen, welche in rot oder gr&uumln
 * anzeigen, ob die M&uumlnze im Besitz ist oder nicht.
 * 
 * @see javax.swing.JTable
 * @author JayPi4c
 *
 */
public class Table extends JTable {

	private static final long serialVersionUID = -8959754359475426474L;

	private int year;

	/**
	 * Der Konstruktor der Tableklasse. Es ist wichtig, dass rowData[0].length ==
	 * columnNames.length()
	 * 
	 * @param rowData     Ein String-Array, das f&uumlr jede Zeile und Spalte einen
	 *                    Text bereith&aumllt, der in der Tabelle angezeigt wird.
	 * @param columnNames Alle Namen der L&aumlnder, die in dem Jahr im &euro;
	 *                    waren.
	 * @param year        Das Jahr, f&uumlr welches die Tabelle erstellt wird.
	 */
	public Table(String rowData[][], String columnNames[], int year) {
		super(rowData, columnNames);
		this.year = year;

		// https://www.tutorials.de/threads/jtable-zelle-gezielt-einfaerben.195978/
		// Quelle des Codes f&uumlr das &Aumlndern des Hintergrundes einer Zelle
		this.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				try {
					setBackground(getColor(column, row));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return this;
			}
		});

		// https://stackoverflow.com/questions/7350893/click-event-on-jtable-java
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				int row = rowAtPoint(evt.getPoint());
				int col = columnAtPoint(evt.getPoint());
				if (col >= 0 && row >= 0) {
					String val = (String) getValueAt(row, col);
					double value = getValue(val);
					String land = Util.getNameByCountryKey(col);
					boolean holding = false;
					try {
						holding = Util.getStatus(land, value, year);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Util.log.info("The coin is " + (holding ? "" : "not ") + "in your possesion!");
					// JOptionPane.showMessageDialog(null,
					// "Die M\u00fcnze ist " + (holding ? "" : "nicht ") + "in deinem Besitz!");

					int dialogButton = JOptionPane.showConfirmDialog(null,
							"Die M\u00fcnze befindet sich " + (holding ? "" : "nicht ") + "in Ihrem Besitz!" + Util.n
									+ "Soll die M\u00fcnze " + (holding ? "entfernt " : "hinzugef\u00fcgt ") + "werden?"
									+ Util.n + "Land: " + land + "; Wert: " + val + "; Jahr: " + year,
							"M\u00fcnze " + (holding ? "entfernen" : "hinzuf\u00fcgen") + "?",
							JOptionPane.YES_NO_OPTION);
					if (dialogButton == JOptionPane.YES_OPTION) {
						try {
							Util.updateRegistry(land, value, year, !holding);
							JOptionPane.showMessageDialog(null, "Erfolgreich!");

							// "Die M\u00fcnze ist " + (holding ? "" : "nicht ") + "in deinem Besitz!");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}
		});
	}

	public Color getColor(int col, int row) throws IOException {
		File file = new File(Util.getPath(year));
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		for (int i = 0; i < col; i++) {
			line = bufferedReader.readLine();
		}

		String[] parts = line.split(";");
		String[] part = parts[row].split(",");
		bufferedReader.close();
		return (part[2].equals("1") ? Color.GREEN : Color.RED);

	}

	private double getValue(String s) {
		switch (s) {
		case "1ct":
			return 0.01;
		case "2ct":
			return 0.02;
		case "5ct":
			return 0.05;
		case "10ct":
			return 0.10;
		case "20ct":
			return 0.20;
		case "50ct":
			return 0.50;
		case "1\u20AC":
			return 1;
		case "2\u20AC":
			return 2;
		default:
			return 42;
		}

	}
}
