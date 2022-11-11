package main;

import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.ui.SniperListener;

public class Sniper implements AuctionListener {
  private final SniperListener listener; private final Auction auction;
  public Sniper(Auction auction, SniperListener listener){ this.listener = listener; this.auction = auction;}
  @Override public void auctionClosed(){ listener.sniperLost();}
  @Override public void currentPrice(int price, int increment){
    auction.bid(price + increment);
    listener.sniperBidding();
  }
}

/**
ant SniperTest
*/