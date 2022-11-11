package test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import main.Sniper;
import main.ui.SniperListener;
import main.xmpp.Auction;

@RunWith(MockitoJUnitRunner.class) public class SniperTest {
  @Mock SniperListener listener;
  @Mock Auction auction;
  @InjectMocks Sniper sniper;
  @Before public void create(){ sniper = new Sniper(auction, listener);}
  @Test public void 価格情報が届いたら買い注文と入札中を報告(){
    int price = 1001, increment = 25;
    sniper.currentPrice(price, increment);
    verify(auction).bid(price + increment);
    verify(listener, atLeast(1)).sniperBidding();
  }

  @Test public void オークションが閉まったら落札失敗だ(){
    sniper.auctionClosed();
    verify(listener).sniperLost();
  }
}
