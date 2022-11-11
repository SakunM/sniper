package main.xmpp;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import main.Item;

public class XMPPAuctionHouse implements AuctionHouse {
  private XMPPConnection connection; private final FailureReporter reporter;
  private String getFullPath(final String logFile){ return new File(logFile).getAbsolutePath();}
  private FileHandler simpleFileHander() throws XMPPAuctionException {
    try {
      FileHandler handler = new FileHandler("auction.log");
      handler.setFormatter(new SimpleFormatter());
      return handler;
    } catch(Exception e){
      throw new XMPPAuctionException("Could not create logger FileHandler " + getFullPath("auction.log"), e);
    }
  }
  private Logger makeLogger() throws XMPPAuctionException {
    Logger logger = Logger.getLogger("Auction Logger");
    logger.setUseParentHandlers(false);
    logger.addHandler(simpleFileHander());
    return logger;
  }

  public XMPPAuctionHouse(String host, String user, String pw) throws XMPPAuctionException {
    this.reporter = new XMPPFailureReporter(makeLogger());
    try {
      connection = new XMPPConnection(host);
      connection.connect(); connection.login(user, pw, "Auction");
    }catch (XMPPException e) { e.printStackTrace();}
  }
  @Override public Auction auctionFor(String itemId){ return new XMPPAuction(connection, itemId, reporter);}
  @Override public Auction2 auction2For(Item item){ return new XMPPAuction2(connection, item, reporter);}
  @Override public void disconnect(){ connection.disconnect();}
}