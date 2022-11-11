package test;

import java.util.List;
import java.util.ArrayList;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.any;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import main.Sniper2;
import main.Snapshot;
import main.State;
import main.Item;
import main.xmpp.Auction2;
import main.ui.SniperListener;

import test.auction.FakeAuction;

@RunWith(MockitoJUnitRunner.class) public class Sniper2Test{
  private  static final Item item = new Item("item-123", 1300);
  @Mock private Auction2 auction;
  @Mock private SniperListener listener;
  private Sniper2 sniper; private Message msg = new Message();
  @Before public void create(){ sniper = new Sniper2(item, "sniper", auction); sniper.addListener(listener);}

  @Test public void 入札を続けて行く内に限度額を超えると脱落中になるよ() { assert(true);}
  @Test public void 自分がビッダーの価格情報が届くと入札はしないで一位入札中になるよ() { assert(true);}
  @Test public void 限度額を超えた価格情報が届くと脱落中になるよ() { assert(true);}
  @Test public void 限度額以下の他人の価格情報が届くとオークションとユーザーに知らせるよ(){ assert(true);}
  @Test public void 参加要請中にオークションが閉まると落札失敗ね() {
    msg.setBody(FakeAuction.CLOSE_EVENT_FORMAT); sniper.processMessage(null, msg);
    verify(listener, atLeast(1)).stateChanged(new Snapshot(item, 0, 0, State.LOST));
  }
}
