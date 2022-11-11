package main;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Item {
  public final String itemId; public final int stopPrice;
  public Item(String itemId, int stopPrice){ this.itemId = itemId; this.stopPrice = stopPrice;}
  private boolean isSame(Item i) { return itemId.equals(i.itemId) && stopPrice == i.stopPrice;}
  @Override public boolean equals(Object obj){ if(obj instanceof Item) { return isSame((Item) obj);} return false;}
  @Override public int hashCode(){ return HashCodeBuilder.reflectionHashCode(this);}
  @Override public String toString(){ return itemId + " " + String.valueOf(stopPrice);}
  public boolean allowBid(int bid){ return bid <= stopPrice;}
}