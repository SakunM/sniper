package main;

import main.util.Defect;

public enum State {
  JOINING("参加要請中"){ @Override public State whenAuctionClosed(){ return LOST;}}, 
  BIDDING("入札参加中"){ @Override public State whenAuctionClosed(){ return LOST;}}, 
  WINNING("一位入札中"){ @Override public State whenAuctionClosed(){ return WON;}}, 
  LOSING("入札脱落中") { @Override public State whenAuctionClosed(){ return LOST;}},
  FAILED("エラー発生") { @Override public State whenAuctionClosed(){ return this;}},
  LOST("落札失敗"){}, 
  WON("落札成功"){};
  private  State(String name) { this.name = name;}
  public final String name;
  public State whenAuctionClosed() { throw new Defect("オークションはとっくに閉まってるよ！！");}
}