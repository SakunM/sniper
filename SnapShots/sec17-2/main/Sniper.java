package main;

import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.ui.SniperListener;

public class Sniper implements AuctionListener {
  private SniperListener listener; private final Auction auction; private Snapshot ss;
  
  public Sniper(String itemId, Auction auction){ this.auction = auction; this.ss = Snapshot.joining(itemId);}
  private void notifyChange(){ listener.stateChanged(ss);}
  @Override public void auctionClosed(){ ss = ss.closed(); notifyChange();}
  @Override public void currentPrice(int price, int increment, PriceSource source){
    switch (source){
      case FromSniper: ss = ss.winning(price); break;
      case FromOtherBidder:
        int bid = price + increment;
        auction.bid(bid); ss = ss.bidding(price, bid); break;
    }
    notifyChange();
  }
  public void addListener(SniperListener listener){ this.listener = listener;}
  public Snapshot getSnapshot(){ return ss;}
}

/**
ant SniperTest
*/