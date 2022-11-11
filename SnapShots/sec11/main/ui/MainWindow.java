package main.ui;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {
  private static JLabel createLable (String state){
    JLabel lbl = new JLabel(state);
    lbl.setName("label");
    lbl.setBorder(new LineBorder(Color.BLACK));
    return lbl;
  }
  private final JLabel sniperStatus = createLable("参加要請中");
  public MainWindow(){
    super ("Auction Sniper");
    setName("MainWindow");
    add(sniperStatus); pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
  public void showStatus(String status){ sniperStatus.setText(status);}
}