package main.ui;

import java.awt.Container;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import main.Snapshot;

public class MainWindow extends JFrame {
  private void fillContentPane(JTable table){
    final Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    pane.add(new JScrollPane(table), BorderLayout.CENTER);
  }
  private JTable makeSnipersTable(TableModel snipers){
    final JTable table = new JTable(snipers);
    table.setName("table");
    return table;
  }

  public MainWindow(TableModel model){
    super ("Auction Sniper");
    setName("MainWindow");
    fillContentPane(makeSnipersTable(model)); pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}