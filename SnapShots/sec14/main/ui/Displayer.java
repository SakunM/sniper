package main.ui;

import javax.swing.SwingUtilities;

public class Displayer implements SniperListener {
  private final MainWindow ui;
  public Displayer(MainWindow ui){ this.ui = ui;}
  private void showStatus(final String status){
    SwingUtilities.invokeLater(new Runnable(){ @Override public void run(){ ui.showStatus(status);}});
  }
  @Override public void sniperLost(){ showStatus("落札失敗");}
  @Override public void sniperBidding(){ showStatus("入札中");}
  @Override public void sniperWinning(){ showStatus("一位入札中");}
  @Override public void sniperWon(){ showStatus("落札成功");}
}