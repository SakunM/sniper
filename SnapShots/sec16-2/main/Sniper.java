package main;

import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.ui.SniperListener;

public class Sniper implements AuctionListener {
  private final SniperListener listener; private final Auction auction; private Snapshot ss;
  
  public Sniper(String itemId, Auction auction, SniperListener listener){
    this.listener = listener; this.auction = auction; this.ss = Snapshot.joining(itemId);
  }
  private void notifyChange(){ listener.stateChanged(ss);}
  @Override public void auctionClosed(){ ss = ss.closed(); notifyChange();}
  @Override public void currentPrice(int price, int increment, PriceSource source){
    switch (source){
      case FromSniper: ss = ss.winning(price); break;
      case FromOtherBidder:
        int bid = price + increment;
        auction.bid(bid);
        ss = ss.bidding(price, bid);
        break;
    }
    notifyChange();
  }
}

/**
ant SniperTest
*/