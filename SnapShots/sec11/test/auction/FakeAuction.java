package test.auction;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import main.auction.AuctionServer;

public class FakeAuction {
  private final AuctionServer server;
  public FakeAuction(final String itemId){ server = new AuctionServer(itemId);}
  public void 入札開始() throws XMPPException { server.startSellingItem();}
  public void 参加メッセージが届くはず() throws InterruptedException {
    Message actual = server.getMessage(); assertThat(actual, is(notNullValue()));
  }
  public void 終了() throws XMPPException { server.sendMessage("");}
  public String getItemId(){ return server.getItemId();}
  public void stop(){ server.stop();}
}