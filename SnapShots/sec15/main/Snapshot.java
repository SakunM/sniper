package main;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Snapshot {
  public final String itemId; public final int lastPrice; public final int lastBid; public final State state;
  public Snapshot(String itemId, int lastPrice, int lastBid, State state){
    this.itemId = itemId; this.lastPrice = lastPrice; this.lastBid = lastBid; this.state = state;
  }
  public Snapshot bidding(int price, int bid){ return new Snapshot(itemId, price, bid, State.BIDDING);}
  public Snapshot winning(int price){ return new Snapshot(itemId, price, lastBid, State.WINNING);}
  public Snapshot closed(){ return new Snapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());}
  private boolean isSame(Snapshot s){
    return itemId.equals(s.itemId) && lastPrice == s.lastPrice && lastBid == s.lastBid && state == s.state;
  }
	@Override public boolean equals(final Object obj) { if(obj instanceof Snapshot) { return isSame((Snapshot) obj);} return false;}
  @Override public String toString() { return itemId + " " + String.valueOf(lastPrice) + " " + String.valueOf(lastBid) + " " + state;}
  @Override public int hashCode() { return HashCodeBuilder.reflectionHashCode(this);}
  public static Snapshot joining(String itemId){ return new Snapshot(itemId, 0, 0, State.JOINING);}
}