package main.xmpp;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  
  private final Chat chat;
  public XMPPAuction(Chat chat){ this.chat = chat;}
  private void sendMessage(final String msg){ try { chat.sendMessage(msg);} catch (XMPPException e) { e.printStackTrace();}}
  @Override public void join(){ sendMessage(JOIN_COMMAND_FORMAT);}
  @Override public void bid(int amount){ sendMessage(format(BID_COMMAND_FORMAT, amount));}
}