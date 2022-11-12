const t = require("../util/tester.js")("Sniper Test"), fs = require("fs"), u = require("util");

function reader(path) { return fs.readFileSync(path, "utf-8");}
function writer(path, msg){ fs.writeFileSync(path, msg, "utf-8");}

class Sniper {
  constructor(items){
    let [nm, it,sp,lp,lb,st] = items.split(' '); this.name = nm; this.item = it; this.state = st.trim();
    this.stopPrice = parseInt(sp); this.lastPrice = parseInt(lp); this.lastBid = parseInt(lb);
    this.msg = "toAuctionMsssage is None";
  }
  close(){ this.state = this.state == "WINNING" ? "WON": "LOST";}
  price(price, increment, bidder){
    if(this.name === bidder) { this.lastPrice = price; this.state = "WINNING"; return;}
    let bid = price + increment; if(bid > this.stopPrice){ this.lastPrice = price; this.state = "LOSING"; return;}
    this.lastPrice = price; this.lastBid = bid; this.state = "BIDDING";
    this.msg = u.format("SOLVersion: 1.1; Command: BID; Price: %d;", bid);
  }
  fail(msg){ this.msg = "failed: " + msg;  this.lastPrice = 0; this.lastBid = 0; this.state = "FAILED";}
  res(){ return `${this.msg}\n${this.item} ${this.lastPrice} ${this.lastBid} ${this.state}`;}
} exports.Sniper = Sniper;

class Translator {
  constructor(msg, sniper){ this.msg = msg; this.sniper = sniper; this.events = {};}
  mkEvent(){
    let events = this.msg.split(';'); events.pop();
    events.forEach(pair => { let [k,v] = pair.split(':'); this.events[k.trim()] = v.trim();});
  }
  get(key){
    let val = this.events[key]; if (val == null || val === ""){ throw new Error("missing field: " + key);}
    return val;
  }
  event(){ return this.get("Event")} getInt(key){ return parseInt(this.get(key));} bidder() { return this.get("Bidder");}
  price(){ return this.getInt("CurrentPrice");} increment(){ return this.getInt("Increment");}
  translate(){
    try {
      this.mkEvent(); let type = this.event();
      if(type === "CLOSE"){ this.sniper.close(); return;}
      if(type === "PRICE"){ this.sniper.price( this.price(), this.increment(), this.bidder()); return;}
    }catch(e){ this.sniper.fail(`msg is [${this.msg}] ${e}`);}
  }
} exports.Translator = Translator;

function domain(items, msg){
  let sniper = new Sniper(items), trans = new Translator(msg, sniper);
  trans.translate(); return sniper.res();
}

function test(){
  let res = domain("sniper item-123 1300 1098 1098 WINNING", "SOLVersion: 1.1; Event: CLOSE;");
  console.log(res);
}

if(require.main === module){
  test()
  // refactor()
  // develop()
  // product()
}

/*
node src/js/src/sniper.js
 */