#![allow(unused)]

use std::fmt;
use std::collections::HashMap;

pub fn int(str: &str) -> u32 { str.parse().unwrap()}
pub fn str(str: &str) -> String { str.to_owned()}

const JOINING: &str = "sniper item-123 1300 0 0 JOINING";
const BIDDING: &str = "sniper item-123 1300 1000 1098 BIDDING";
const WINNING: &str = "sniper item-123 1300 1098 1098 WINNING";
const CLOSE_MSG: &str = "SOLVersion: 1.1; Event: CLOSE;";
const PRICE_MSG: &str = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: other bidder;";
const PRICE_ME: &str = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: sniper;";
const PRICE_OVER: &str = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1280; Increment: 24; Bidder: some one;";
const LOST_INCREMENT: &str = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1280; Increment:; Bidder: some one;";

struct Sniper{name: String, item: String, stop_price: u32, last_price: u32, last_bid: u32, state: String, msg: String}
impl Sniper{
  fn new(args: String) -> Sniper{
    let s:Vec<&str> = args.split_whitespace().collect();
    let (name, item, state, msg) = (str(s[0]), str(s[1]), str(s[5]), str("toAuctionMessage is None"));
    let (stop_price, last_price, last_bid) = (int(s[2]),int(s[3]), int(s[4]));
    Sniper { name, item, stop_price, last_price, last_bid, state, msg}
  }
  fn price(self, price: u32, increment: u32, bidder: &str) -> Self {
    if bidder == self.name { return Self { last_price: price, state: str("WINNING"), ..self};}
    let bid = price + increment; if bid > self.stop_price { return Self { last_price: price, state: str("LOSING"), ..self};}
    let bid_msg = format!("SOLVersion: 1.1; Command: BID; Price: {};",bid);
    Self { last_price: price, last_bid: bid, state: str("BIDDING"), msg: bid_msg, ..self}
  }
  fn close(self) -> Self {
    if self.state == "WINNING" { return Self {state: str("WON"), ..self};} Self { state: str("LOST"), ..self}
  }
  fn failed(self, msg: &str) -> Self {
    let msg = format!("failed: {}", msg);
    Self { last_price: 0, last_bid: 0, state: str("FAILED"), msg, ..self}
  }
}
impl fmt::Display for Sniper{
  fn fmt(&self, f:&mut fmt::Formatter) -> fmt::Result{
    write!(f, "{}\n{} {} {} {}", self.msg, self.item, self.last_price, self.last_bid, self.state)
  }
}


#[derive(Debug, PartialEq)] enum Trans {Close, Price(u32, u32, String), Failed(String)}

fn about_fail(msg: &str, err: &str) -> Trans { Trans::Failed(format!("<{}> [{}]",err, msg))}

fn dmt_check(msg: &str) -> bool {
  let (mut col, mut sem_col) = (0,0);
  for c in msg.chars(){ if c == ':' {col += 1} if c == ';' { sem_col += 1}}
  if col != sem_col { return true;} if col == 2 || col == 5 {return false;}
  true
}
fn make_map(msg: &str) -> HashMap<&str, &str> {
  msg.split(';').filter(|v| *v != "").map(|pair| {
    let mut p = pair.split(':'); (p.next().unwrap().trim(), p.next().unwrap().trim())
  }).collect()
}
fn missing(map: &HashMap<&str, &str>, key: &str) -> bool { match map.get(key) { Some(_) => false, None => true}}

fn parser(able: &str) -> bool { match able.parse::<u32>() { Ok(_) => false, Err(_) => true}}

fn about_price(msg: &str, map: HashMap<&str, &str>) -> Trans {
  if missing(&map, "CurrentPrice") { return about_fail(msg, "missing filed CurrentPrice");}
  if missing(&map, "Increment") { return about_fail(msg, "missing filed Increment");}
  if missing(&map, "Bidder") { return about_fail(msg, "missing filed Bidder");}
  let (price, inc, bidder) = (map.get("CurrentPrice").unwrap(), map.get("Increment").unwrap(), map.get("Bidder").unwrap());
  if parser(price) { return about_fail(msg, "parse err CurrentPrice");}
  if parser(inc) { return about_fail(msg, "parse err Increment");}
  Trans::Price(price.parse().unwrap(), inc.parse().unwrap(), str(bidder))
}
fn translate(msg: &str) -> Trans {
  if dmt_check(msg){ return about_fail(msg, "delemit erroe");}
  let res = make_map(msg);
  match res.get("Event") {
    Some(&"CLOSE") => Trans::Close,
    Some(&"PRICE") => about_price(msg, res),
    None => about_fail(msg, "Event field missing"),
    _ => panic!("other")
  }
}

fn job(s: Sniper, t: Trans) -> String {
  let s = match t {
    Trans::Price(p, i, b) => s.price(p, i, &b),
    Trans::Close => s.close(),
    Trans::Failed(msg) => s.failed(&msg)
  };
  s.to_string()
}
pub fn domain(items: String, msg: String) -> String { let (s,t) = (Sniper::new(items), translate(&msg)); job(s,t)}

#[cfg(test)] mod tests {
  use super::*;
  #[test] fn about_close_1(){ // 参加要請中にオークションが閉まると落札失敗
    let act = domain(str(JOINING), str(CLOSE_MSG));
    let exp = "toAuctionMessage is None\nitem-123 0 0 LOST";
    assert_eq!(act, exp);
  }
  #[test] fn about_close_2(){ // 入札中にオークションが閉まっても落札失敗
    let act = domain(str(BIDDING), str(CLOSE_MSG));
    let exp = "toAuctionMessage is None\nitem-123 1000 1098 LOST";
    assert_eq!(act, exp);
  }
  #[test] fn about_close_3(){ // 一位入札中にオークションが閉まると落札成功
    let act = domain(str(WINNING), str(CLOSE_MSG));
    let exp = "toAuctionMessage is None\nitem-123 1098 1098 WON";
    assert_eq!(act, exp);
  }
  #[test] fn about_price_1(){ // 参加要請中に限度額以下の価格情報が来ると買い注文を出して入札中になる
    let act = domain(str(JOINING), str(PRICE_MSG));
    let exp = "SOLVersion: 1.1; Command: BID; Price: 1122;\nitem-123 1098 1122 BIDDING";
    assert_eq!(act, exp);
  }
  #[test] fn about_price_2(){ // 自分名義の価格情報が来ると一位入札中になって買い注文は出さないよ
    let act = domain(str(BIDDING), str(PRICE_ME));
    let exp = "toAuctionMessage is None\nitem-123 1098 1098 WINNING";
    assert_eq!(act, exp);
  }
  #[test] fn about_price_3(){ // 限度額以上の価格情報が来ると脱落中になって買い注文は出さないよ
    let act = domain(str(WINNING), str(PRICE_OVER));
    let exp = "toAuctionMessage is None\nitem-123 1280 1098 LOSING";
    assert_eq!(act, exp);
  }
  #[test] fn about_failed_1(){ // ヘンテコなメッセージを受け取ると金額をすべて０にしてエラー発生
    let act = domain(str(WINNING), str("a broken message"));
    let exp = "failed: <delemit erroe> [a broken message] \nitem-123 0 0 FAILED";
    assert_eq!(act, exp);
  }
  #[test] fn about_failed_2(){ // 少しおかしなメッセージが来ても金額をすべて０にしてエラー発生
    let act = domain(str(WINNING), str(LOST_INCREMENT));
    let msg = format!("failed: <parse err Increment> [{}]", LOST_INCREMENT);
    let state = "item-123 0 0 FAILED";
    assert_eq!(act, format!("{} \n{}", msg, state));
  }
  #[test] fn trans_price(){
    let res = translate(PRICE_MSG);
    assert_eq!(res, Trans::Price(1098, 24, str("other bidder")));
  }
  #[test] fn trans_clse(){
    let res = translate(CLOSE_MSG);
    assert_eq!(res, Trans::Close);
  }
  #[test] fn about_check(){
    assert_eq! (dmt_check(PRICE_MSG), false); assert_eq! (dmt_check(CLOSE_MSG), false); 
    assert_eq! (dmt_check("a broken message"), true); assert_eq!(dmt_check("a: b;"), true);
  }
  #[test] fn sniper_display(){
    let s = Sniper::new(str(JOINING));
    assert_eq!(s.to_string(), str("toAuctionMessage is None\nitem-123 0 0 JOINING"));
  }
}