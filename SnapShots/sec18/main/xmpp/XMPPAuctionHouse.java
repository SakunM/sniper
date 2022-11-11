package main.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
  private XMPPConnection connection;
  public XMPPAuctionHouse(String host, String user, String pw){
    try {
      connection = new XMPPConnection(host);
      connection.connect(); connection.login(user, pw, "Auction");
    }catch (XMPPException e) { e.printStackTrace();}
  }
  @Override public Auction auctionFor(String itemId){ return new XMPPAuction(connection, itemId);}
  @Override public void disconnect(){ connection.disconnect();}
}