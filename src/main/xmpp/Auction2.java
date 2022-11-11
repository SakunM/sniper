package main.xmpp;

import main.Sniper2;
import main.ui.SniperListener;

public interface Auction2 {
  void toAuction(String msg);
  void addListener(SniperListener listener);
  Sniper2 getSniper();
  void failed(String msg);
  void join();
}