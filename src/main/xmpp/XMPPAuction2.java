package main.xmpp;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;

import main.Sniper2;
import main.Item;
import main.ui.SniperListener;

public class XMPPAuction2 implements Auction2 {
  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  private final Chat chat; private final Sniper2 sniper; private final Item item; private final FailureReporter reporter;

  private void sendMessage(String msg){ try{chat.sendMessage(msg);} catch (Exception e){e.printStackTrace();}}
  @Override public void toAuction(String msg){sendMessage(msg);}
  @Override public void join(){ sendMessage(JOIN_COMMAND_FORMAT);}
  @Override public void addListener(SniperListener listener){this.sniper.addListener(listener);}
  @Override public Sniper2 getSniper(){ return sniper;}
  @Override public void failed(String msg){
    reporter.cannotTranslateMessage2(item.itemId, msg); chat.removeMessageListener(sniper);
  }

  public XMPPAuction2(XMPPConnection connection, Item item, FailureReporter reporter){
    this.chat = connection.getChatManager().createChat(format(AUCTION_ID, item.itemId), null);
    this.item = item; this.reporter = reporter; this.sniper = new Sniper2(item, connection.getUser(), this);
    this.chat.addMessageListener(this.sniper);
  }
}