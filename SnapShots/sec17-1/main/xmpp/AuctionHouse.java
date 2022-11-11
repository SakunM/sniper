package main.xmpp;

public interface AuctionHouse {
  Auction auctionFor(String itemId);
  void disconnect();
}