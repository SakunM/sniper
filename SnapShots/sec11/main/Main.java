package main;

import static java.lang.String.format;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.MessageListener;


import main.ui.MainWindow;

public class Main {
  private MainWindow ui; private Chat notToBeGCd;

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow();}});
  }

  public static final String AUCTION_ID = "auction-%s@localhost/Auction";
  private static String auctionId(String itemId){ return format(AUCTION_ID, itemId);}
  
  private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    final Chat chat = connection.getChatManager().createChat( auctionId(itemId), new MessageListener (){
      @Override public void processMessage(Chat achat, Message msg){
        SwingUtilities.invokeLater( new Runnable (){
          @Override public void run(){ ui.showStatus("落札失敗");}
        });
      }
    });
    this.notToBeGCd = chat; chat.sendMessage(new Message());
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