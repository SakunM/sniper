package main;

import static java.lang.String.format;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import main.ui.MainWindow;
import main.ui.SniperListener;
import main.ui.Displayer;
import main.ui.TableModel;
import main.xmpp.AuctionListener;
import main.xmpp.Translator;
import main.xmpp.Auction;
import main.xmpp.XMPPAuction;

public class Main {
  private final TableModel snipers = new TableModel();
  private MainWindow ui; private List<Chat> notToBeGCd = new ArrayList<>();

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow(snipers);}});
  }

  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  private static String auctionId(String itemId){ return format(AUCTION_ID, itemId);}
  
  private void disconnectWhenUICloses(final XMPPConnection connection) {
    ui.addWindowListener(new WindowAdapter(){
      @Override public void windowClosed(WindowEvent e){ connection.disconnect();}
    });
  }
  
  private void safelyAddItemToModel(String itemId) throws Exception{
    SwingUtilities.invokeAndWait(new Runnable () {
      @Override public void run(){
        snipers.addSniper(Snapshot.joining(itemId));
      }
    });
  }
  private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
    safelyAddItemToModel(itemId);
    final Chat chat = connection.getChatManager().createChat( auctionId(itemId), null);
    this.notToBeGCd.add(chat);
    Auction auction = new XMPPAuction(chat);
    chat.addMessageListener(new Translator(connection.getUser(), new Sniper(itemId, auction, new Displayer(snipers))));
    auction.join();
  }
  
  private static XMPPConnection connection() throws XMPPException {
    XMPPConnection connection = new XMPPConnection("localhost");
    connection.connect(); connection.login("sniper", "sniper", "Auction");
    return connection;
  }
  public static void main(String... args) throws Exception{
    Main main = new Main(); XMPPConnection connection = connection();
    main.disconnectWhenUICloses(connection);
    for(int i= 0; i<args.length; i++){ main.joinAuction(connection, args[i]);}
  }
}