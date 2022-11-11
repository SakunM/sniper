package main;

import main.ui.UserListener;
import main.ui.Collector;
import main.xmpp.AuctionHouse;
import main.xmpp.Auction2;

public class Launcher2 implements UserListener {
  @Override public void joinAuction(Item item){
    Auction2 auction = house.auction2For(item);
    Sniper2 sniper = auction.getSniper();
    collector.addSniper(sniper);
    auction.join();
  }
  private final AuctionHouse house; private final Collector collector;
  public Launcher2(AuctionHouse house, Collector collector){ this.house = house; this.collector = collector;}
}