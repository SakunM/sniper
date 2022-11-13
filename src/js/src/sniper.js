const t = require("../util/tester.js")("Sniper Test"), fs = require("fs"), u = require("util");

function reader(path) { return fs.readFileSync(path, "utf-8");}
function writer(path, msg){ fs.writeFileSync(path, msg, "utf-8");}

class Sniper {
  constructor(items){
    let [nm, it,sp,lp,lb,st] = items.split(' '); this.name = nm; this.item = it; this.state = st.trim();
    this.stopPrice = parseInt(sp); this.lastPrice = parseInt(lp); this.lastBid = parseInt(lb);
    this.msg = "toAuctionMessage is None";
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

function refactor(){
  let [items, msg ] = reader("a:/pj/sniper/rr/req1.txt").split('\n');
  let res = domain(items, msg), exp = reader("a:/pj/sniper/rr/res2.txt");
  res = res.replace("\n", " "); exp = exp.replace("\r\n", " ");
  t.test(res.trim(), exp.trim(), "今回リファクターではあえて間違いテストをチェックしたい");
}

function developer(arg, exp, disp){
  let [items, msg] = arg.split('\n'), ans = domain(items, msg);
  t.test(ans.replace("\n", " "), exp.replace("\r\n", " "), disp);
}
function develop(){
  const arg1 = reader("a:/pj/sniper/rr/req1.txt"), exp1 = reader("a:/pj/sniper/rr/res1.txt");
  const arg2 = reader("a:/pj/sniper/rr/req2.txt"), exp2 = reader("a:/pj/sniper/rr/res2.txt");
  const arg3 = reader("a:/pj/sniper/rr/req3.txt"), exp3 = reader("a:/pj/sniper/rr/res3.txt");
  const arg4 = reader("a:/pj/sniper/rr/req4.txt"), exp4 = reader("a:/pj/sniper/rr/res4.txt");
  const arg5 = reader("a:/pj/sniper/rr/req5.txt"), exp5 = reader("a:/pj/sniper/rr/res5.txt");
  const arg6 = reader("a:/pj/sniper/rr/req6.txt"), exp6 = reader("a:/pj/sniper/rr/res8.txt");
  developer(arg1,exp1, "参加要請中に限度額以下の価格情報がくると入札中になって買い注文を出すよ");
  developer(arg2,exp2, "参加要請中にオークションが閉まると落札は失敗で買い注文は無しだ");
  developer(arg3,exp3, "入札中にオークションが閉まっても落札は失敗で買い注文は無しだ");
  developer(arg4,exp4, "一位入札中にオークションが閉まれば落札は成功で買い注文は無しだ");
  developer(arg5,exp5, "一位入札中でも限度額以上の価格情報がくれば脱落中で買い注文は無しだ");
  developer(arg6,exp6, "ヘンテコなメッセージが来ると金額はすべて０にしてエラー発生だ");
}

function product(){
  let [items, msg] = reader("toSniper.txt").split('\n');
  writer("fromSniper.txt", domain(items, msg));
}

if(require.main === module){
  // test()
  // refactor()
  // develop()
  product()
}

/*
node src/js/src/sniper.js
 */