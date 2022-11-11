package test.auction;

import static java.lang.String.format;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import main.auction.AuctionServer;
import main.xmpp.XMPPAuction;

public class FakeAuction {
  public static final String auctionId = "auction-%s@localhost/Auction";
  public static final String sniperId = "sniper@localhost/Auction";
  public static final String VERSION = "SOLVersion: 1.1; ";
  public static final String CLOSE_EVENT_FORMAT = VERSION + "Event: CLOSE;";
  public static final String PRICE_EVENT_FORMAT = VERSION + "Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;";

  private final AuctionServer server;
  public FakeAuction(final String itemId){ server = new AuctionServer(itemId);}
  public void 入札開始() throws XMPPException { server.startSellingItem();}
  public void 価格情報(int price, int increment, String bidder) throws XMPPException {
    server.sendMessage(format(PRICE_EVENT_FORMAT, price, increment, bidder));
  }

  private void msgChecker(String sniper, String expect) throws InterruptedException {
    Message actual = server.getMessage();
    assertThat(actual.getFrom(), is(sniper));
    assertThat(actual.getBody(), is(expect));
  }
  public void 参加メッセージが届くはず(String name) throws InterruptedException {
    msgChecker(name, XMPPAuction.JOIN_COMMAND_FORMAT);
  }
  public void 買い注文が届くはず(int bid, String name) throws InterruptedException {
    msgChecker(name, format(XMPPAuction.BID_COMMAND_FORMAT, bid));
  }

  public void 終了() throws XMPPException { server.sendMessage(CLOSE_EVENT_FORMAT);}
  public String getItemId(){ return server.getItemId();}
  public void stop(){ server.stop();}
}