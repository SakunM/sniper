package main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import main.ui.SniperListener;
import main.ui.ForGUI;
import main.xmpp.Auction2;
import main.util.Defect;

public class Sniper2 implements MessageListener, ForGUI {
  private final Item item; private final String name; private final Auction2 auction;
  private SniperListener listener; private Snapshot ss;

  private void writePart(String msg){
    String message = name + " " + ss + "\n" + msg;
    try(FileWriter fw = new FileWriter(new File("toSniper.txt"))) { fw.write(message); fw.flush();}
    catch(IOException e) { e.printStackTrace();}
  }
  private void exesPart(){
    String exes = "node src/js/src/sniper.js";
    // String exes = "ruby src/ruby/src/sniper.rb";
    // String exes = "python src/python/src/sniper.py";
    try{ Process p = Runtime.getRuntime().exec(exes); p.waitFor(); p.destroy();
    } catch (IOException | InterruptedException e) { e.printStackTrace();}
  }
  private String readPart(){
    File file = new File("fromSniper.txt"); String text = "";
    try (FileReader fr = new FileReader(file)){
      int i = fr.read(); while(i != -1) { char c = (char)i; text += c; i = fr.read();}
    } catch (IOException e) { e.printStackTrace();}
    return text;
  }

  private void domain(String res){
    String[] lines = res.split("\n");
    String msg = lines[0].trim(); ss = Snapshot.create(item, lines[1].trim());
    if(!msg.equals("toAuctionMessage is None")){
      if(msg.startsWith("failed:")){auction.failed(msg);} else {auction.toAuction(msg);}
    }
    listener.stateChanged(ss);
  }

  @Override public void processMessage(Chat chat, Message msg){
    writePart(msg.getBody()); exesPart(); domain(readPart());
  }

  @Override public void addListener(SniperListener listener){ this.listener = listener;}
  @Override public Snapshot getSnapshot(){ return this.ss;}

  public Sniper2(Item item, String name, Auction2 auction){
    this.item = item; this.name = name; this.auction = auction; this.ss = Snapshot.joining(item);
  }
}