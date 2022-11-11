package main;

import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.ui.SniperListener;

public class Sniper implements AuctionListener {
  private SniperListener listener; private final Auction auction; private final Item item;
  private Snapshot ss;
  
  public Sniper(Item item, Auction auction){
    this.item = item; this.auction = auction; this.ss = Snapshot.joining(item);
  }
  private void notifyChange(){ listener.stateChanged(ss);}
  @Override public void currentPrice(int price, int increment, PriceSource source){
    switch (source){
      case FromSniper: ss = ss.winning(price); break;
      case FromOtherBidder:
        int bid = price + increment;
        if(item.allowBid(bid)) { auction.bid(bid); ss = ss.bidding(price, bid);}
        else { ss = ss.losing(price);} break;
    }
    notifyChange();
  }
  @Override public void auctionClosed(){ ss = ss.closed(); notifyChange();}
  @Override public void auctionFailed(){ ss = ss.failed(); auction.failed(); notifyChange();}
  public void addListener(SniperListener listener){ this.listener = listener;}
  public Snapshot getSnapshot(){ return ss;}
}

/**
ant SniperTest
*/