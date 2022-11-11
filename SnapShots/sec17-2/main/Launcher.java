package main;

import java.util.List;
import java.util.ArrayList;

import main.xmpp.Auction;
import main.xmpp.AuctionHouse;
import main.ui.UserListener;
import main.ui.Collector;



public class Launcher implements UserListener {
  private final AuctionHouse house; private final Collector collector;

  @Override public void joinAuction(String itemId){
    Auction auction = house.auctionFor(itemId);
    Sniper sniper = new Sniper(itemId, auction);
    auction.addListener(sniper);
    collector.addSniper(sniper);
    auction.join();
  }
  
  public Launcher(AuctionHouse house, Collector collector){ this.house = house; this.collector = collector;}
}