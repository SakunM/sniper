package test.xmpp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import main.xmpp.AuctionListener;
import main.xmpp.Auction;
import main.xmpp.AuctionHouse;
import main.xmpp.XMPPAuctionHouse;

import test.auction.FakeAuction;
import test.LogDriver;

public class XMPPAuctionHouseTest {
  private FakeAuction server = new FakeAuction("item-65432");
  private AuctionHouse house;
  @Before public void setup() throws Exception {
    server.入札開始(); house = new XMPPAuctionHouse("localhost", "sniper", "sniper");
  }
  
  @Test public void 参加するとオークションからイベントが来るよ() throws Exception {
    CountDownLatch closed = new CountDownLatch(1);
    Auction auction = house.auctionFor("item-65432");
    auction.addListener(listener(closed));
    auction.join(); server.参加メッセージが届くはず(FakeAuction.sniperId);
    server.終了(); assertThat(closed.await(2, TimeUnit.SECONDS), is(true));
  }
  @After public void stop(){ if(house != null){ house.disconnect(); server.stop();}}

  private AuctionListener listener(CountDownLatch cdl){
    return new AuctionListener(){
      @Override public void auctionClosed(){ cdl.countDown();}
      @Override public void currentPrice(int price, int increment, PriceSource source){}
      @Override public void auctionFailed(){}
    };
  }
}

