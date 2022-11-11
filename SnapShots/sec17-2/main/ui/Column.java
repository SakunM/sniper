package main.ui;

import main.Snapshot;
import main.State;

public enum Column {
  ITEM_IDENTIFER("商品名") { @Override public Object valueIn(Snapshot ss){ return ss.itemId;}}, 
  LAST_PRICE("最新価格"){  @Override public Object valueIn(Snapshot ss){ return ss.lastPrice;}}, 
  LAST_BID("当社入札額"){  @Override public Object valueIn(Snapshot ss){ return ss.lastBid;}},
  STATE("入札状態"){  @Override public Object valueIn(Snapshot ss){ return ss.state.name;}};
  public final String name;
  public static Column at(int offset){ return values()[offset];}
  private Column(String name) { this.name = name;}
  abstract public Object valueIn(Snapshot ss);
}