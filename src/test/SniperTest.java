package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.hamcrest.Matcher;
import org.hamcrest.FeatureMatcher;

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
import main.Item;
import main.ui.SniperListener;
import main.xmpp.Auction;

@RunWith(MockitoJUnitRunner.class) public class SniperTest {
  @Mock SniperListener listener;
  @Mock Auction auction;
  @InjectMocks Sniper sniper;
  private final Item item = new Item("itme-123", 1234);
  @Before public void create(){ sniper = new Sniper(item, auction); sniper.addListener(listener);}

  @Test public void 参加中にオークションが壊れたらエラー発生(){
    final InOrder inOrder = inOrder(listener, auction);
    sniper.currentPrice(123, 45, FromOtherBidder); sniper.auctionFailed();
    inOrder.verify(auction).bid(168);
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.BIDDING))));
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.FAILED))));
    inOrder.verify(auction, never()).bid(anyInt());
  }
  @Test public void 脱落中にオークションが閉まれは落札失敗だ(){
    final InOrder inOrder = inOrder(listener);
    sniper.currentPrice(1230, 5, FromOtherBidder); sniper.auctionClosed();
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.LOSING))));
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.LOST))));
  }
  @Test public void 脱落中になったら見守るだけだ(){
    final InOrder inOrder = inOrder(listener, auction); int price = 1233, increment = 25;
    sniper.currentPrice(123,45,FromOtherBidder);
    sniper.currentPrice(168,45,FromSniper);
    sniper.currentPrice(price,increment,FromOtherBidder);
    inOrder.verify(auction).bid(168);
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.BIDDING))));
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.WINNING))));
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(is(aSniperThatIs(State.LOSING))));
    inOrder.verify(auction, never()).bid(anyInt());
  }
  @Test public void 限度額を超えたらもう入札はしないよ(){
    final int price = 1233, increment = 25; sniper.currentPrice(price, increment, FromOtherBidder);
    verify(listener, atLeast(1)).stateChanged( new Snapshot(item, price, 0, State.LOSING));
    verify(auction, never()).bid(anyInt());
  }
  @Test public void 限度額を超えたら脱落中を報告(){
    final InOrder inOrder = inOrder(listener); final int bid = 123 + 45;
    sniper.currentPrice(123,45,FromOtherBidder);
    sniper.currentPrice(2345,25,FromOtherBidder);
    inOrder.verify(listener, atLeast(1)).stateChanged(argThat(aSniperThatIs(State.BIDDING)));
    inOrder.verify(listener).stateChanged(new Snapshot(item, 2345, bid, State.LOSING));
  }
  @Test public void 一位入札中にオークションが閉まったら落札成功(){
    sniper.currentPrice(123,45, FromSniper);
    sniper.auctionClosed();
    verify(listener, atLeast(1)).stateChanged(any());
  }
  @Test public void 自分がビッダーの価格情報が届いたら一位入札中を報告(){
    final InOrder inOrder = inOrder(listener);
    sniper.currentPrice(123,12, FromOtherBidder);
    sniper.currentPrice(135,45, FromSniper);
    inOrder.verify(listener, atLeast(1)).stateChanged(new Snapshot(item, 123, 135, State.BIDDING));
    inOrder.verify(listener, atLeast(1)).stateChanged(new Snapshot(item, 135, 135, State.WINNING));
  }
  @Test public void 他人がビッダーで限度額以下の価格情報が届いたら買い注文と入札中を報告(){
    int price = 1001, increment = 25, bid = price + increment;
    sniper.currentPrice(price, increment, FromOtherBidder);
    verify(auction).bid(bid);
    verify(listener, atLeast(1)).stateChanged( new Snapshot(item, price, bid, State.BIDDING));
  }
  @Test public void なにもしないうちにオークションが閉まったら落札失敗だ(){
    sniper.auctionClosed();
    verify(listener).stateChanged(any());
  }

  private Matcher<Snapshot> aSniperThatIs(final State state){
    return new FeatureMatcher<Snapshot, State>(equalTo(state), "sniper state is ", "was") {
      @Override protected State featureValueOf(final Snapshot actual){ return actual.state;}
    };
  }
}
