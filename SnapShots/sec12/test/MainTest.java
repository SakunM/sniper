package test;

import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;

import test.auction.FakeAuction;
import test.ui.SwingTestDriver;

import main.Main;

public class MainTest {
  private final FakeAuction auction = new FakeAuction("item-54321");
  private final String sniperId = FakeAuction.sniperId;
  private SwingTestDriver driver;

  @BeforeClass public static void  setupKeyBoardLayout(){ System.setProperty("com.objogate.wl.keyboard", "US");}

  @Test public void 入札中にオークションが閉まっても落札失敗() throws Exception {
    auction.入札開始();
    入札参加(auction);
    auction.参加メッセージが届くはず(sniperId);
    auction.価格情報(1000, 98, "other bidder");
    入札中を表示するはず();
    auction.買い注文が届くはず(1098, sniperId);
    auction.終了();
    落札失敗を表示するはず();
  }

  // @Ignore
  @Test public void 参加中にオークションが終了すると落札失敗() throws Exception {
    auction.入札開始();
    入札参加(auction);
    auction.参加メッセージが届くはず(sniperId);
    auction.終了();
    落札失敗を表示するはず();
  }

  @After public void stopProcess(){ auction.stop(); stop();}

  private void 入札参加(final FakeAuction auction) {
    Thread thread = new Thread("Test Application") {
      @Override public void run(){ try { Main.main(auction.getItemId());} catch (Exception e) { e.printStackTrace();}}
    };
    thread.setDaemon(true); thread.start(); driver = new SwingTestDriver(1000); driver.状態("参加要請中");
  }
  private void 入札中を表示するはず(){ driver.状態("入札中");}
  private void 落札失敗を表示するはず(){ driver.状態("落札失敗");}
  private void stop(){ if(driver != null) {driver.dispose();}}
}