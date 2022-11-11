package main.xmpp;

import java.util.Map;
import java.util.HashMap;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.Chat;

public class Translator implements MessageListener {
  private final AuctionListener listener;
  public Translator(AuctionListener listener) { this.listener = listener;}

  private Map<String,String> unpackEventFrom(Message msg){
    Map<String,String> event = new HashMap<>();
    for(String elem: msg.getBody().split(";")){
      String[] pair = elem.split(":");
      event.put(pair[0].trim(), pair[1].trim());
    }
    return event;
  }

  @Override public void processMessage(Chat chat, Message msg){
    Map<String,String> event = unpackEventFrom(msg);
    String type = event.get("Event");
    if("CLOSE".equals(type)) { listener.auctionClosed();}
    if("PRICE".equals(type)) {
      int price = Integer.parseInt(event.get("CurrentPrice")); 
      int increment = Integer.parseInt(event.get("Increment")); 
      listener.currentPrice(price, increment);
    }
  }
}

/**
ant TranslatorTest
*/