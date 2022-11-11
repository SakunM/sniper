package main.auction;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class AuctionServer implements ChatManagerListener {
  private final String itemId; private final SingleMessageListner listener; private Chat currentChat;
  private final XMPPConnection connection;

  private static class SingleMessageListner implements MessageListener {
    private final ArrayBlockingQueue<Message> msgs = new ArrayBlockingQueue<>(1);
    @Override public void processMessage(Chat chat, Message msg){ msgs.add(msg);}
    private Message getMessage() throws InterruptedException { return msgs.poll(7, TimeUnit.SECONDS);}
  }  

  public AuctionServer(String itemId){
    this.itemId = itemId; this.connection = new XMPPConnection("localhost");
    this.listener = new SingleMessageListner();
  }
  @Override public void chatCreated(
    Chat chat, boolean bln){ this.currentChat = chat;
    chat.addMessageListener(listener);
  }
  public void startSellingItem() throws XMPPException {
    connection.connect();
    String auctionId = "auction-" + itemId;
    connection.login(auctionId, "auction", "Auction");
    connection.getChatManager().addChatListener(this);
  }
  public String getItemId(){ return this.itemId;}
  public Message getMessage() throws InterruptedException { return listener.getMessage();}
  public void sendMessage(String msg) throws XMPPException {
    if(currentChat == null){ System.out.println("null"); return;}
    currentChat.sendMessage(msg);
  }
  public String getUserName(){ return connection.getUser();}
  public void stop(){ connection.disconnect();}
}