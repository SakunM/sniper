package main.xmpp;

public class XMPPAuctionException extends Exception {
  private static final long serialVersionUID = 4L;
  public XMPPAuctionException(final String msg, final Exception e) { super(msg, e);}
  public XMPPAuctionException(final Exception e) { super(e);}
}