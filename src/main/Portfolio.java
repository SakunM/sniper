package main;

import java.util.List;
import java.util.ArrayList;

import main.ui.Collector;
import main.ui.ForGUI;

public class Portfolio implements Collector {
  private List<ForGUI> snipers = new ArrayList<>(); private  PortfolioListener listener;
  public void addListener(PortfolioListener listener) { this.listener = listener;}
  @Override public void addSniper(ForGUI sniper){ snipers.add(sniper); listener.sniperAdded(sniper);}
  public interface PortfolioListener { void sniperAdded(ForGUI sniper);}
}