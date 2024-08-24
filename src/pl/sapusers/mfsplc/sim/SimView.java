package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JTable {
	public static final Border BORDER_UP = BorderFactory.createBevelBorder(BevelBorder.RAISED);
	public static final Border BORDER_DOWN = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

	private Configurator configurator;
	private SimController controller;
	private Color cellColor;
	private int cellSize;
	GridCell[][] cells;

	public SimView(Configurator configurator, SimController controller) {
		super();

		this.configurator = configurator;
		this.controller = controller;

		cellColor = controller.getModel().getBackgroundColor();
		cellSize = configurator.getCellSize();

// 		setLayout(new GridBagLayout());
//		setBackground(cellColor.darker());

		cells = new GridCell[configurator.getGridSizeX()][configurator.getGridSizeY()];

//		GridBagConstraints gbc = new GridBagConstraints();
//		for (int x = 0; x < cells.length; x++) {
//			for (int y = 0; y < cells[x].length; y++) {
//				gbc.gridx = x;
//				gbc.gridy = y;
//				gbc.insets = new Insets(1, 1, 1, 1);
//
//				GridCell cell = new GridCell(x, y, configurator.getCellSize(), cellColor);
//				cells[x][y] = cell;
//
//				cell.addMouseListener(controller);
//				add(cell, gbc);
//			}
//		}

		DefaultTableModel tableModel = new DefaultTableModel(configurator.getGridSizeX(), configurator.getGridSizeY());
		setModel(tableModel);
		setRowMargin(0);
		resize();

		setShowGrid(true);
		setGridColor(controller.getModel().getBackgroundColor().darker());
		setTableHeader(null);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

	public void zoomIn() {
		cellSize = getRowHeight() + configurator.getZoomStep();
		resize();
	}

	public void zoomOut() {
		cellSize = (cellSize > configurator.getZoomStep() ? cellSize -= configurator.getZoomStep() : 1);
		cellSize = (cellSize > configurator.getZoomStep() ? cellSize -= configurator.getZoomStep() : 1);
		resize();
		zoomIn();
	}

	private void resize() {
		System.out.println(cellSize);
		
		for (int x = 0; x < getModel().getColumnCount(); x++) {
			getColumnModel().getColumn(x).setPreferredWidth(cellSize);
			getColumnModel().getColumn(x).setMaxWidth(cellSize);
			getColumnModel().getColumn(x).setMinWidth(cellSize);
			getColumnModel().getColumn(x).setWidth(cellSize);
		}
		
		setRowHeight(cellSize);
	}

	public void changeBackgroundColor() {
		Color newColor = JColorChooser.showDialog(this.getParent(), "Set Background Color", cellColor);

		if (newColor == null)
			return;
		else
			cellColor = newColor;

		controller.paintBackground(cellColor);
		setBackground(cellColor.darker());
	}

	public GridCell getCell(int x, int y) {
		return cells[x][y];
	}

	public GridCell getCell(Position pos) {
		return getCell(pos.x, pos.y);
	}

	public GridCell[][] getCells() {
		return cells;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(getColumnCount() * getRowHeight(), getRowCount() * getRowHeight());
	}
}
