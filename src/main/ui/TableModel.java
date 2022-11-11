package main.ui;

import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import javax.swing.SwingUtilities;

import main.Sniper;
import main.Snapshot;
import main.Portfolio.PortfolioListener;
import main.util.Defect;

public class TableModel extends AbstractTableModel implements PortfolioListener {
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
  public void addSnapshot(Snapshot ss){ this.sss.add(ss); int row = sss.size()-1; fireTableRowsInserted(row,row);}

  private class SwingThreadListener implements SniperListener {
    private final TableModel model;
    public SwingThreadListener(TableModel model){ this.model = model;}
    @Override public void stateChanged(final Snapshot ss){
      SwingUtilities.invokeLater(new Runnable(){
        @Override public void run(){ model.stateChanged(ss);}
      });
    }
  }
  @Override public void sniperAdded(ForGUI sniper){
    addSnapshot(sniper.getSnapshot()); sniper.addListener(new SwingThreadListener(this));
  }
}