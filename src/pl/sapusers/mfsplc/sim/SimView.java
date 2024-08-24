package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JTable {
	private class CellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			controller.decorateCell((JComponent) cell, row, column, isSelected);

			return cell;

		}

	}
	public static final Border BORDER_DOWN = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Border BORDER_UP = BorderFactory.createBevelBorder(BevelBorder.RAISED);

	public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();
	private int cellSize;
	private Configurator configurator;
	private SimController controller;

	GridCell[][] cells;

	public SimView(Configurator configurator, SimController controller) {
		super();

		this.configurator = configurator;
		this.controller = controller;

		cellSize = configurator.getCellSize();

		cells = new GridCell[configurator.getGridSizeX()][configurator.getGridSizeY()];

		DefaultTableModel tableModel = new DefaultTableModel(configurator.getGridSizeX(), configurator.getGridSizeY());
		setModel(tableModel);
		resize();

		setShowGrid(true);
		setGridColor(controller.getModel().getBackgroundColor().darker());
		setTableHeader(null);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultRenderer(Object.class, (TableCellRenderer) new CellRenderer());
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Get the point where the mouse was clicked
				Point point = e.getPoint();

				// Determine the row and column of the clicked cell
				int row = rowAtPoint(point);
				int column = columnAtPoint(point);

				// Print the cell that was clicked
				System.out.println("Clicked cell at row " + row + ", column " + column);

				// Optionally, get the value of the clicked cell
				Object value = getValueAt(row, column);
				System.out.println("Value of clicked cell: " + value);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				return;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				return;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				return;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				return;
			}
		});

//		setSelectionModel( (ListSelectionModel) new ListSelectionModel() {
//			public boolean isSelectionEmpty() { return true; }
//			public boolean isSelectedIndex(int index) { return false; }
//			public int getMinSelectionIndex() { return -1; }
//			public int getMaxSelectionIndex() { return -1; }
//			public int getLeadSelectionIndex() { return -1; }
//			public int getAnchorSelectionIndex() { return -1; }
//			public void setSelectionInterval(int index0, int index1) { }
//			public void setLeadSelectionIndex(int index) { }
//			public void setAnchorSelectionIndex(int index) { }
//			public void addSelectionInterval(int index0, int index1) { }
//			public void insertIndexInterval(int index, int length, boolean before) { }
//			public void clearSelection() { }
//			public void removeSelectionInterval(int index0, int index1) { }
//			public void removeIndexInterval(int index0, int index1) { }
//			public void setSelectionMode(int selectionMode) { }
//			public int getSelectionMode() { return SINGLE_SELECTION; }
//			public void addListSelectionListener(ListSelectionListener lsl) { }
//			public void removeListSelectionListener(ListSelectionListener lsl) { }
//			public void setValueIsAdjusting(boolean valueIsAdjusting) { }
//			public boolean getValueIsAdjusting() { return false; }
//		});

		setColumnSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

	}

	public void changeBackgroundColor() {
		controller.setBackgroundColor(
				JColorChooser.showDialog(this.getParent(), "Set Background Color", controller.getBackgroundColor()));

		repaint();
	}

	public GridCell[][] getCells() {
		return cells;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(getColumnCount() * getRowHeight(), getRowCount() * getRowHeight());
	}

	public void setCellText(String text, int x, int y) {
		getModel().setValueAt(text, y, x);
	}

	public void zoomIn() {
		cellSize = getRowHeight() + configurator.getZoomStep();
		resize();
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		Point point = e.getPoint();
		
		int x = columnAtPoint(point);
		int y = rowAtPoint(point);
		
		return Integer.valueOf(x) + " " + Integer.valueOf(y);
	}
	
	public void zoomOut() {
		cellSize = (cellSize > configurator.getZoomStep() ? cellSize -= configurator.getZoomStep() : 1);
		cellSize = (cellSize > configurator.getZoomStep() ? cellSize -= configurator.getZoomStep() : 1);
		resize();
		zoomIn();
	}

	private void resize() {

		for (int x = 0; x < getModel().getColumnCount(); x++) {
			getColumnModel().getColumn(x).setPreferredWidth(cellSize);
			getColumnModel().getColumn(x).setMaxWidth(cellSize);
			getColumnModel().getColumn(x).setMinWidth(cellSize);
			getColumnModel().getColumn(x).setWidth(cellSize);
		}

		setRowHeight(cellSize);
	}
}
