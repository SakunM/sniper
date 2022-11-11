package test.xmpp;

import static java.lang.String.format;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import test.auction.FakeAuction;
import test.LogDriver;
import main.xmpp.Translator;
import main.xmpp.AuctionListener;
import main.xmpp.FailureReporter;

@RunWith(MockitoJUnitRunner.class) public class TranslatorTest {
  @Mock AuctionListener listener;
  @Mock FailureReporter reporter;
  @InjectMocks Translator translator;
  private final Message msg = new Message(); private final String sniperId = FakeAuction.sniperId;
  @Before public void create(){ translator = new Translator(sniperId, reporter); translator.addListener(listener);}

  // @Ignore
  @Test public void おかしなメッセージを受信したらエラー発生を報告(){
    String badMsg = "a bat message"; msg.setBody(badMsg);
    translator.processMessage(null, msg);
    verify(listener, atLeast(1)).auctionFailed();
    verify(reporter, atLeast(1)).cannotTranslateMessage(anyString(), anyString(), any());
  }
  // @Ignore
  @Test public void 少しおかしなメッセージを受信してもエラー発生を報告(){
    msg.setBody("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: onther biddar;");
    translator.processMessage(null, msg);
    verify(listener, atLeast(1)).auctionFailed();
    verify(reporter, atLeast(1)).cannotTranslateMessage(anyString(), anyString(), any());
  }
  // @Ignore
  @Test public void 自分がビッダーの価格情報を受けたらFromSniperを報告(){
    msg.setBody(format(FakeAuction.PRICE_EVENT_FORMAT, 234, 5, sniperId));
    translator.processMessage(null, msg);
    verify(listener).currentPrice(234, 5, FromSniper);
  }
  // @Ignore
  @Test public void 他人がビッダーの価格情報を受けたらFromOtherBiddarを報告(){
    msg.setBody(format(FakeAuction.PRICE_EVENT_FORMAT, 234, 5, "Somone else"));
    translator.processMessage(null, msg);
    verify(listener).currentPrice(234, 5, FromOtherBidder);
  }
  // @Ignore
  @Test public void 終了メッセージをうけたらリスナーに伝えるよ() {
    msg.setBody(FakeAuction.CLOSE_EVENT_FORMAT);
    translator.processMessage(null, msg);
    verify(listener, atLeast(1)).auctionClosed();
  }
} 

/**
ant TranslatorTest
*/
