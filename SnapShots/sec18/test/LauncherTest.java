package test;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.hamcrest.Matcher;
import org.hamcrest.FeatureMatcher;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import main.Launcher;
import main.Sniper;
import main.Item;
import main.ui.Collector;
import main.xmpp.Auction;
import main.xmpp.AuctionHouse;

@RunWith(MockitoJUnitRunner.class) public class LauncherTest {
  @Mock private Collector collector;
  @Mock private AuctionHouse house;
  @Mock private Auction auction;
  @InjectMocks private Launcher launcher;

  @Test public void 入札に参加するとスナイパーが作られて保管されオークションに参加するよ(){
    final Item item = new Item("item 123", 789); final InOrder inOrder = inOrder(auction, collector);
    when(house.auctionFor(item.itemId)).thenReturn(auction);
    launcher.joinAuction(item);
    inOrder.verify(auction).addListener(argThat(is(sniperForItem(item))));
    inOrder.verify(collector).addSniper(argThat(is(sniperForItem(item))));
    inOrder.verify(auction).join();
  }

  private Matcher<Sniper> sniperForItem(final Item item){
    return new FeatureMatcher<Sniper, Item>(equalTo(item), "a sniper that is ", "was") {
      @Override protected Item featureValueOf(final Sniper actual){ return actual.getSnapshot().item;}
    };
  }
}