package main.ui;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import main.Snapshot;

public class MainWindow extends JFrame {
  private void fillContentPane(JTable table, JPanel controls){
    final Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    pane.add(controls, BorderLayout.NORTH);
    pane.add(new JScrollPane(table), BorderLayout.CENTER);
  }
  private JTable makeSnipersTable(TableModel snipers){
    final JTable table = new JTable(snipers);
    table.setName("table");
    return table;
  }
  private  JTextField itemIdField(){
    JTextField field = new JTextField();
    field.setColumns(10); field.setName("field");
    return field;
  }
  private UserListener listener;
  private  JTextField itemIdField = itemIdField();
  private JPanel makecontrols(){
    JPanel pane = new JPanel(new FlowLayout());
    pane.add(new JLabel("商品名")); pane.add(itemIdField);
    JButton btn = new JButton("入札に参加"); btn.setName("button");
    btn.addActionListener(new ActionListener(){
      @Override public void actionPerformed(ActionEvent e){ listener.joinAuction(itemIdField.getText());}
    });
    pane.add(btn); return pane;
  }
  
  public void addListener(UserListener listener){ this.listener = listener;}

  public MainWindow(TableModel model){
    super ("Auction Sniper");
    setName("MainWindow");
    fillContentPane(makeSnipersTable(model), makecontrols()); pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}