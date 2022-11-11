package main.xmpp;

public interface AuctionListener {
  void auctionClosed();
  void currentPrice(int price, int increment);
}