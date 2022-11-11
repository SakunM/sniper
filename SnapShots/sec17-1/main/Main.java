package main;

import static java.lang.String.format;
import java.util.List;
import java.util.ArrayList;

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
  private final TableModel snipers = new TableModel();
  private MainWindow ui; private List<Auction> notToBeGCd = new ArrayList<>();

  public Main() throws Exception {
    SwingUtilities.invokeAndWait( new Runnable(){ @Override public void run(){ ui = new MainWindow(snipers);}});
  }
  
  private void disconnectWhenUICloses(AuctionHouse house) {
    ui.addWindowListener(new WindowAdapter(){
      @Override public void windowClosed(WindowEvent e){ house.disconnect();}
    });
  }
  
  private void addUserListenerFor(AuctionHouse house){
    ui.addListener(new UserListener(){
      @Override public void joinAuction(String itemId){
        snipers.addSniper(Snapshot.joining(itemId));
        Auction auction = house.auctionFor(itemId);
        notToBeGCd.add(auction);
        auction.addListener(new Sniper(itemId, auction, new Displayer(snipers)));
        auction.join();
      }
    });
  }
  public static void main(String... args) throws Exception{
    Main main = new Main(); AuctionHouse house = new XMPPAuctionHouse("localhost", "sniper", "sniper");
    main.disconnectWhenUICloses(house); main.addUserListenerFor(house);
  }
}