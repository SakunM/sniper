package main.ui;

import javax.swing.table.AbstractTableModel;

import main.Snapshot;

public class TableModel extends AbstractTableModel {
  private static final long serialVarsionUID = 2L;
  private Snapshot ss = Snapshot.joining("");
  private String status = "参加要請中";

  @Override public int getColumnCount(){ return Column.values().length; }
  @Override public int getRowCount(){ return 1;}
  @Override public Object getValueAt(int row, int col){ return Column.at(col).valueIn(ss);}
  @Override public String getColumnName(int col){ return Column.at(col).name;}
  public void setStatus(String status){ this.status = status;}
  public void stateChanged(Snapshot ss){
    this.ss = ss;
    fireTableRowsUpdated(0,0);
  }
}