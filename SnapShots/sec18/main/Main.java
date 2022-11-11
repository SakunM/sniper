package main;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import main.ui.MainWindow;
import main.ui.TableModel;
import main.ui.Displayer;
import main.ui.UserListener;
import main.xmpp.AuctionListener;
import main.xmpp.Translator;
import main.xmpp.Auction;
import main.xmpp.XMPPAuction;
import main.xmpp.AuctionHouse;
import main.xmpp.XMPPAuctionHouse;


public class Main {
  private MainWindow ui; private final Portfolio pf = new Portfolio();

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow(pf);}});
  }
  
  private void disconnectWhenUICloses(AuctionHouse house) {
    ui.addWindowListener(new WindowAdapter(){
      @Override public void windowClosed(WindowEvent e){ house.disconnect();}
    });
  }
  
  private void addUserListenerFor(AuctionHouse house){ ui.addListener(new Launcher(house, pf));}

  public static void main(String... args) throws Exception{
    Main main = new Main(); AuctionHouse house = new XMPPAuctionHouse("localhost", "sniper", "sniper");
    main.disconnectWhenUICloses(house); main.addUserListenerFor(house);
  }
}