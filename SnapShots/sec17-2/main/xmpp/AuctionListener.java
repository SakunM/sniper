package main.xmpp;

public interface AuctionListener {
  enum PriceSource { FromSniper, FromOtherBidder;};
  void auctionClosed();
  void currentPrice(int price, int increment, PriceSource source);
}