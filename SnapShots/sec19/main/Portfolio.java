package main;

import java.util.List;
import java.util.ArrayList;

import main.ui.Collector;

public class Portfolio implements Collector {
  private List<Sniper> snipers = new ArrayList<>(); private  PortfolioListener listener;
  public void addListener(PortfolioListener listener) { this.listener = listener;}
  @Override public void addSniper(Sniper sniper){ snipers.add(sniper); listener.sniperAdded(sniper);}
  public interface PortfolioListener { void sniperAdded(Sniper sniper);}
}