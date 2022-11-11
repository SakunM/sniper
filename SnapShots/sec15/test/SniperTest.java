package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import main.Sniper;
import main.State;
import main.Snapshot;
import main.ui.SniperListener;
import main.xmpp.Auction;

@RunWith(MockitoJUnitRunner.class) public class SniperTest {
  @Mock SniperListener listener;
  @Mock Auction auction;
  @InjectMocks Sniper sniper;
  private final String itemId = "itme-123";
  @Before public void create(){ sniper = new Sniper(itemId, auction, listener);}

  @Test public void 一位入札中にオークションが閉まったら落札成功(){
    sniper.currentPrice(123,45, FromSniper);
    sniper.auctionClosed();
    verify(listener, atLeast(1)).stateChanged(any());
  }
  @Test public void 自分がビッダーの価格情報が届いたら一位入札中を報告(){
    final InOrder inOrder = inOrder(listener);
    sniper.currentPrice(123,12, FromOtherBidder);
    sniper.currentPrice(135,45, FromSniper);
    inOrder.verify(listener, atLeast(1)).stateChanged(new Snapshot(itemId, 123, 135, State.BIDDING));
    inOrder.verify(listener, atLeast(1)).stateChanged(new Snapshot(itemId, 135, 135, State.WINNING));

  }
  @Test public void 他人がビッダーの価格情報が届いたら買い注文と入札中を報告(){
    int price = 1001, increment = 25, bid = price + increment;
    sniper.currentPrice(price, increment, FromOtherBidder);
    verify(auction).bid(bid);
    verify(listener, atLeast(1)).stateChanged( new Snapshot(itemId, price, bid, State.BIDDING));
  }

  @Test public void なにもしないうちにオークションが閉まったら落札失敗だ(){
    sniper.auctionClosed();
    verify(listener).stateChanged(any());
  }
}
