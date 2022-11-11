package main.ui;

import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import main.Snapshot;
import main.util.Defect;

public class TableModel extends AbstractTableModel {
  private static final long serialVarsionUID = 2L;
  private List<Snapshot> sss = new ArrayList<>();

  @Override public int getColumnCount(){ return Column.values().length; }
  @Override public int getRowCount(){ return sss.size();}
  @Override public Object getValueAt(int row, int col){ return Column.at(col).valueIn(sss.get(row));}
  @Override public String getColumnName(int col){ return Column.at(col).name;}
  private int rowMatching(Snapshot ss){
    for(int i= 0; i<sss.size(); i++){ if( ss.isForSameItemAs(sss.get(i))) {return i;}}
    throw new Defect("Cannot find match for " + ss);
  }
  public void stateChanged(Snapshot ss){
    int row = rowMatching(ss); this.sss.set(row, ss); fireTableRowsUpdated(row, row);
  }
  public void addSniper(Snapshot ss){ this.sss.add(ss); int row = sss.size()-1; fireTableRowsInserted(row,row);}
}