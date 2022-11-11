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
  private final String sniperId; private final AuctionListener listener;
  public Translator(String sniperId, AuctionListener listener) { this.sniperId = sniperId; this.listener = listener;}

  private static class Event {
    private final Map<String, String> fields = new HashMap<>();
    private void addField(String field){ String[] pair = field.split(":"); fields.put(pair[0].trim(), pair[1].trim());}
    private String get(String key){ return fields.get(key);}
    private int getInt(String key){ return Integer.parseInt(get(key));}
    private String bidder(){ return get("Bidder");}

    public String type() { return fields.get("Event");}
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

  @Override public void processMessage(Chat chat, Message msg){
    Event event = Event.from(msg.getBody());
    String type = event.type();
    if("CLOSE".equals(type)) { listener.auctionClosed();}
    if("PRICE".equals(type)) { listener.currentPrice(event.price(), event.increment(), event.isFrom(sniperId));}
  }
}

/**
ant TranslatorTest
*/