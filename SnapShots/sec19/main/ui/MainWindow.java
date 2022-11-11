package main.ui;

import java.text.NumberFormat;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import main.Snapshot;
import main.Portfolio;
import main.Item;

public class MainWindow extends JFrame {
  private void fillContentPane(JTable table, JPanel controls){
    final Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    pane.add(controls, BorderLayout.NORTH);
    pane.add(new JScrollPane(table), BorderLayout.CENTER);
  }
  private JTable makeSnipersTable(Portfolio pf){
    TableModel model = new TableModel(); pf.addListener(model);
    final JTable table = new JTable(model);
    table.setName("table"); return table;
  }
  private  JTextField itemIdField(){
    JTextField field = new JTextField();
    field.setColumns(10); field.setName("text");
    return field;
  }
  private JFormattedTextField stopPriceField(){
    JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
    field.setColumns(10); field.setName("price"); return field;
  }
  private UserListener listener;
  private  JTextField itemIdField = itemIdField();
  private  JFormattedTextField stopPriceField = stopPriceField();
  private JPanel makecontrols(){
    JPanel pane = new JPanel(new FlowLayout());
    pane.add(new JLabel("商品名")); pane.add(itemIdField);
    pane.add(new JLabel("入札限度額")); pane.add(stopPriceField);
    JButton btn = new JButton("入札に参加"); btn.setName("button");
    btn.addActionListener(new ActionListener(){
      private String itemId(){ return itemIdField.getText();}
      private int stopPrice(){ return ((Number)stopPriceField.getValue()).intValue();}
      @Override public void actionPerformed(ActionEvent e){ listener.joinAuction(new Item(itemId(), stopPrice()));}
    });
    pane.add(btn); return pane;
  }
  
  public void addListener(UserListener listener){ this.listener = listener;}

  public MainWindow(Portfolio pf){
    super ("Auction Sniper");
    setName("MainWindow");
    fillContentPane(makeSnipersTable(pf), makecontrols()); pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}