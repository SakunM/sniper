package test.xmpp;

import static java.lang.String.format;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import test.auction.FakeAuction;
import main.xmpp.Translator;
import main.xmpp.AuctionListener;

@RunWith(MockitoJUnitRunner.class) public class TranslatorTest {
  @Mock AuctionListener listener;
  @InjectMocks Translator translator;
  private final Message msg = new Message();
  @Test public void 価格情報を受けたら詳細を報告(){
    msg.setBody(format(FakeAuction.PRICE_EVENT_FORMAT, 234, 5, "Somone else"));
    translator.processMessage(null, msg);
    verify(listener).currentPrice(234, 5);
  }
  @Test public void 終了メッセージをうけたらリスナーに伝えるよ() {
    msg.setBody(FakeAuction.CLOSE_EVENT_FORMAT);
    translator.processMessage(null, msg);
    verify(listener, atLeast(1)).auctionClosed();
  }
} 

/**
ant TranslatorTest
*/
