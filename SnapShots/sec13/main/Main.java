package main;

import static java.lang.String.format;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import main.ui.MainWindow;
import main.ui.SniperListener;
import main.ui.Displayer;
import main.xmpp.AuctionListener;
import main.xmpp.Translator;
import main.xmpp.Auction;
import main.xmpp.XMPPAuction;

public class Main {
  private MainWindow ui; private Chat notToBeGCd;

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow();}});
  }

  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  private static String auctionId(String itemId){ return format(AUCTION_ID, itemId);}
  
  private void disconnectWhenUICloses(final XMPPConnection connection) {
    ui.addWindowListener(new WindowAdapter(){
      @Override public void windowClosed(WindowEvent e){ connection.disconnect();}
    });
  }
  
  private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    disconnectWhenUICloses(connection);
    final Chat chat = connection.getChatManager().createChat( auctionId(itemId), null);
    this.notToBeGCd = chat;
    Auction auction = new XMPPAuction(chat);
    chat.addMessageListener(new Translator(new Sniper(auction, new Displayer(ui))));
    auction.join();
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