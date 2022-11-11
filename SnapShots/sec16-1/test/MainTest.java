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
  private final FakeAuction auction2 = new FakeAuction("item-65432");
  private final String sniperId = FakeAuction.sniperId;
  private SwingTestDriver driver;

  @BeforeClass public static void  setupKeyBoardLayout(){ System.setProperty("com.objogate.wl.keyboard", "US");}
  @Test public void 複数の商品の入札に挑戦だ() throws Exception {
    auction.入札開始();
    auction2.入札開始();

    入札参加(auction, auction2);
    auction.参加メッセージが届くはず(sniperId);
    auction2.参加メッセージが届くはず(sniperId);

    auction.価格情報(1000, 98, "other bidder");
    auction.買い注文が届くはず(1098, sniperId);

    auction2.価格情報(500, 21, "other bidder");
    auction2.買い注文が届くはず(521, sniperId);

    auction.価格情報(1098, 97, sniperId);
    auction2.価格情報(521, 22, sniperId);

    一位入札中を表示するはず(auction, 1098);
    一位入札中を表示するはず(auction2, 521);

    auction.終了();
    auction2.終了();

    落札成功を表示するはず(auction, 1098);
    落札成功を表示するはず(auction2, 521);
  }
  // @Ignore
  @Test public void 一位入札中にオークションが閉まると落札成功() throws Exception {
    auction.入札開始();
    入札参加(auction);
    auction.参加メッセージが届くはず(sniperId);
    auction.価格情報(1000, 98, "other bidder");
    入札中を表示するはず(1000, 1098);
    auction.買い注文が届くはず(1098, sniperId);
    auction.価格情報(1098, 97, sniperId);
    一位入札中を表示するはず(1098);
    auction.終了();
    落札成功を表示するはず(1098);
  }
  
  // @Ignore
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

  @After public void stopProcess(){ auction.stop(); auction2.stop(); stop();}

  protected static String[] args(FakeAuction... auctions){
    String[] res = new String[auctions.length];
    for(int i= 0; i<auctions.length; i++){ res[i] = auctions[i].getItemId();}
    return res;
  }
  private void 入札参加(final FakeAuction... auctions) {
    Thread thread = new Thread("Test Application") {
      @Override public void run(){ try { Main.main(args(auctions));} catch (Exception e) { e.printStackTrace();}}
    };
    thread.setDaemon(true); thread.start(); driver = new SwingTestDriver(1000);
    driver.hasTitle("Auction Sniper"); driver.カラムのタイトルもチェックするよ();
    for (FakeAuction auction: auctions) { driver.状態(auction.getItemId(), 0, 0, "参加要請中");}
  }

  private void 入札中を表示するはず(){ driver.状態("入札参加中");}
  private void 落札失敗を表示するはず(){ driver.状態("落札失敗");}
  private void 一位入札中を表示するはず(){ driver.状態("一位入札中");}
  private void 落札成功を表示するはず(){ driver.状態("落札成功");}

  private String itemId = "item-54321";
  private void 入札中を表示するはず(int lastPrice, int lastBid){ driver.状態(itemId, lastPrice, lastBid, "入札参加中");}
  private void 一位入札中を表示するはず(int winningBid){ driver.状態(itemId, winningBid, winningBid, "一位入札中");}
  private void 落札成功を表示するはず(int lastPrice){ driver.状態(itemId, lastPrice, lastPrice, "落札成功");}

  private void 一位入札中を表示するはず(FakeAuction auction, int winningBid){
    driver.状態(auction.getItemId(), winningBid, winningBid, "一位入札中");
  }
  private void 落札成功を表示するはず(FakeAuction auction, int lastPrice){
    driver.状態(auction.getItemId(), lastPrice, lastPrice, "落札成功");
  }

  private void stop(){ if(driver != null) {driver.dispose();}}
}