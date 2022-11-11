package main.ui;

import javax.swing.SwingUtilities;

import main.Snapshot;

public class Displayer implements SniperListener {
  private final TableModel model;
  public Displayer(TableModel model){ this.model = model;}
  
  @Override public void stateChanged(Snapshot ss){
    SwingUtilities.invokeLater(new Runnable(){
      @Override public void run(){model.stateChanged(ss);}
    });
  }
}