package main.xmpp;

public interface Auction {
  void bid(int amont);
  void join();
  void addListener(AuctionListener listener);
}