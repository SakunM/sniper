package main;

import static java.lang.String.format;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.MessageListener;

import main.ui.MainWindow;
import main.xmpp.AuctionListener;
import main.xmpp.Translator;

public class Main implements AuctionListener {
  private MainWindow ui; private Chat notToBeGCd;

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow();}});
  }

  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  private static String auctionId(String itemId){ return format(AUCTION_ID, itemId);}
  
  private void disconnectWhenUICloses(final XMPPConnection connection) {
    ui.addWindowListener(new WindowAdapter(){
      @Override public void windowClosed(WindowEvent e){ connection.disconnect();}
    });
  }
  @Override public void auctionClosed(){
    SwingUtilities.invokeLater(new Runnable(){
      @Override public void run(){ ui.showStatus("落札失敗");}
    });
  }
  @Override public  void currentPrice(int price, int increment){
    SwingUtilities.invokeLater(new Runnable(){
      @Override public void run(){ ui.showStatus("入札中");}
    });
  }
  private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    disconnectWhenUICloses(connection);
    final Chat chat = connection.getChatManager().createChat( auctionId(itemId), new Translator(this));
    this.notToBeGCd = chat; chat.sendMessage(JOIN_COMMAND_FORMAT);
  }
  
  private static XMPPConnection connection() throws XMPPException {
    XMPPConnection connection = new XMPPConnection("localhost");
    connection.connect(); connection.login("sniper", "sniper", "Auction");
    return connection;
  }
  public static void main(String... args) throws Exception{
    Main main = new Main(); main.joinAuction(connection(), args[0]);
  }
}