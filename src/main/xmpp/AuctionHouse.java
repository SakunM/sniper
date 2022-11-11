package main.xmpp;

import main.Item;

public interface AuctionHouse {
  Auction auctionFor(String itemId);
  Auction2 auction2For(Item item);
  void disconnect();
}