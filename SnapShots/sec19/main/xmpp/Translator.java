package main.xmpp;

import java.util.Map;
import java.util.HashMap;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.Chat;

import static main.xmpp.AuctionListener.PriceSource.FromSniper;
import static main.xmpp.AuctionListener.PriceSource.FromOtherBidder;
import main.xmpp.AuctionListener.PriceSource;

public class Translator implements MessageListener {
  private final String sniperId; private final FailureReporter reporter; private AuctionListener listener;
  public Translator(String sniperId, FailureReporter reporter) { this.sniperId = sniperId; this.reporter = reporter;}
  public void addListener(AuctionListener listener){ this.listener = listener;}

  private static class Event {
    private final Map<String, String> fields = new HashMap<>();
    private void addField(String field){ String[] pair = field.split(":"); fields.put(pair[0].trim(), pair[1].trim());}
    private String get(String key) throws MissingValueException {
      String val = fields.get(key);
      if(val == null) { throw new MissingValueException(key);}
      return val;
    }
    private int getInt(String key){ return Integer.parseInt(get(key));}
    private String bidder(){ return get("Bidder");}

    public String type() { return get("Event");}
    public int price() { return getInt("CurrentPrice");}
    public int increment() { return getInt("Increment");}
    public PriceSource isFrom(String id) { return id.equals(bidder()) ? FromSniper : FromOtherBidder;}

    static String[] fieldsIn(String msg){ return msg.split(";");}
    static Event from(String msg){
      Event event = new Event();
      for(String field: fieldsIn(msg)){ event.addField(field);}
      return event;
    }
  }
  private void translate(String msg){
    Event event = Event.from(msg);
    String type = event.type();
    if("CLOSE".equals(type)) { listener.auctionClosed();}
    if("PRICE".equals(type)) { listener.currentPrice(event.price(), event.increment(), event.isFrom(sniperId));} 
  }
  @Override public void processMessage(Chat chat, Message msg){
    String body = msg.getBody();
    try { translate(body);} catch (Exception e) {
      reporter.cannotTranslateMessage(sniperId, body, e); listener.auctionFailed();
    }
  }
}

/**
ant TranslatorTest
*/