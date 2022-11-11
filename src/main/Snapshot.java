package main;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Snapshot {
  public final Item item; public final int lastPrice; public final int lastBid; public final State state;
  public Snapshot(Item item, int lastPrice, int lastBid, State state){
    this.item = item; this.lastPrice = lastPrice; this.lastBid = lastBid; this.state = state;
  }
  public Snapshot bidding(int price, int bid){ return new Snapshot(item, price, bid, State.BIDDING);}
  public Snapshot winning(int price){ return new Snapshot(item, price, lastBid, State.WINNING);}
  public Snapshot losing(int price){ return new Snapshot(item, price, lastBid, State.LOSING);}
  public Snapshot closed(){ return new Snapshot(item, lastPrice, lastBid, state.whenAuctionClosed());}
  public Snapshot failed(){ return new Snapshot(item, 0, 0, State.FAILED);}
  public boolean isForSameItemAs(final Snapshot ss){ return this.item.equals(ss.item);}
  private  boolean isSame(Snapshot s){
    return item.equals(s.item) && lastPrice == s.lastPrice && lastBid == s.lastBid && state == s.state;
  }
	@Override public boolean equals(final Object obj) { if(obj instanceof Snapshot) { return isSame((Snapshot) obj);} return false;}
  @Override public String toString() { return item + " " + String.valueOf(lastPrice) + " " + String.valueOf(lastBid) + " " + state;}
  @Override public int hashCode() { return HashCodeBuilder.reflectionHashCode(this);}
  public static Snapshot joining(Item item){ return new Snapshot(item, 0, 0, State.JOINING);}
  public static Snapshot create(Item item, String l){
    String[] ls = l.split(" ");
    return new Snapshot(item,Integer.parseInt(ls[1]), Integer.parseInt(ls[2]), State.create(ls[3]));
  }
}