package main;

import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.ui.SniperListener;

public class Sniper implements AuctionListener {
  private final SniperListener listener; private final Auction auction; private boolean isWinning = false;
  public Sniper(Auction auction, SniperListener listener){ this.listener = listener; this.auction = auction;}
  @Override public void auctionClosed(){
    if (isWinning) { listener.sniperWon();} else { listener.sniperLost();}
  }
  @Override public void currentPrice(int price, int increment, PriceSource source){
    isWinning = source == FromSniper;
    if (isWinning) { listener.sniperWinning();}
    else { auction.bid( price + increment); listener.sniperBidding();} 
  }
}

/**
ant SniperTest
*/