package main.xmpp;

import static java.lang.String.format;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  
  private final Chat chat; private final Translator translator;
  public XMPPAuction(XMPPConnection connection, String itemId){
    translator = new Translator(connection.getUser());
    chat = connection.getChatManager().createChat(format(AUCTION_ID, itemId), translator);
  }
  private void sendMessage(final String msg){ try { chat.sendMessage(msg);} catch (XMPPException e) { e.printStackTrace();}}
  @Override public void join(){ sendMessage(JOIN_COMMAND_FORMAT);}
  @Override public void bid(int amount){ sendMessage(format(BID_COMMAND_FORMAT, amount));}
  @Override public void addListener(AuctionListener listener){ this.translator.addListener(listener);}
}