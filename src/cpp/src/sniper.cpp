#include <map>
using std::map;

#include "../utils/about_util.cpp"
#include "../utils/tester.cpp"

const string joining = "sniper item-123 1300 0 0 JOINING";
const string bidding = "sniper item-123 1300 1000 1098 BIDDING";
const string winning = "sniper item-123 1300 1098 1098 WINNING";
const string close_msg = "SOLVersion: 1.1; Event: CLOSE;";
const string buy_price_msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: other bidder;";
const string price_me = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: sniper;";
const string hi_price = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1280; Increment: 21; Bidder: some one;";
const string not_e_msg = "SOLVersion: 1.1; Event:; CurrentPrice: 1098; Increment: 24; Bidder: other bidder;";
const string not_i_msg = "SOLVersion: 1.1; Event:PRICE; CurrentPrice: 1098; Bidder: other bidder;";

struct Sniper{
  string name, item, state, msg; int stopPrice, lastPrice, lastBid;
  Sniper (string sniper){
    Ss ss = split(sniper, ' ');
    name = ss.at(0); item = ss.at(1); stopPrice = std::stoi(ss.at(2)); lastPrice = std::stoi(ss.at(3));
    lastBid = std::stoi(ss.at(4)); state = ss.at(5); msg = "toAuctionMessage is None";
  }
  string show(){
    return msg + "\n" + item + " " + to_string(lastPrice) + " " + to_string(lastBid) + " " + state;
  }
  void close(){ state = state == "WINNING" ? "WON" : "LOST";}
  void price(int price, int increment, string bidder){
    if(bidder == name){ lastPrice = price; state = "WINNING"; return;}
    int bid = price + increment; if (bid > stopPrice) { lastPrice = price; state = "LOSING"; return;}
    lastPrice = price; lastBid = bid; state = "BIDDING";
    msg = "SOLVersion: 1.1; Command: BID; Price: " + to_string(bid) + ";";
  }
  void failed(string fail_msg){ lastPrice = 0; lastBid = 0; state = "FAILED"; msg = "failed: " + fail_msg;}
};
void test_s(){ Sniper s = Sniper(joining); cout << s.show() << endl;}

struct Translator{
  string msg, fail_msg, bidder; map<string, string> fields; int price, increment; bool fail = false;
  Translator(const string &message) { msg = message; fail_msg = "[" + msg + "]";}
  void makeFields(){
    Ss fs = split(msg, ';');
    for(int i= 0; i<(int)fs.size(); i++){
      Ss pair = split(fs.at(i), ':');
      string key = trim(pair.at(0)); string val = trim(pair.at(1));
      fields[key] = val;
    }
  }  
  void translate(){ try{ makeFields();} catch (std::out_of_range e) { fail = true;}}
  string get(string key){
    string val = fields[key]; if (val == "") { throw " Missing value for field is [" + key + "]";}
    return val;
  }
  void set_price(){ try {price = stoi(get("CurrentPrice"));} catch(string e){fail = true; fail_msg += e;}}
  void set_increment(){ try {increment = stoi(get("Increment"));} catch(string e){fail = true; fail_msg += e;} }
  void set_bidder(){ try {bidder = get("Bidder");} catch(string e){fail = true; fail_msg += e;}}
  string event(){
    string res;
    try{ res = get("Event");} catch(string e) { fail = true; fail_msg += e;}
    if(res == "CLOSE") { return res;}
    if(res == "PRICE") { set_price(); set_increment(); set_bidder();}
    if (fail) { return "FAILED";}
    return res;
  }
};
void test_t() { Translator t = Translator(price_me); t.translate(); cout << t.event() << endl;}

string domain(string items, string msg){
  Sniper s = Sniper(items); Translator t = Translator(msg); t.translate();
  string event = t.event();
  if(event == "CLOSE") { s.close();}
  if(event == "PRICE") { s.price(t.price, t.increment, t.bidder);}
  if(event == "FAILED") { s.failed(t.fail_msg);}
  return s.show();
}
void test() { string res = domain(bidding, price_me); cout << res << endl;}

string retToSp(const string &str){ Ss res = split(str, '\n'); return trim(res.at(0)) + " " + trim(res.at(1));}

void refactor(){
  string arg = read_file("a:/pj/sniper/rr/req1.txt"), exp = read_file("a:/pj/sniper/rr/res2.txt");
  Ss args = split(arg, '\n'); string act = domain(args.at(0), args.at(1));
  zz_act("Sniper Test", retToSp(act), retToSp(exp), "ここでは失敗メッセージをチェックするよ");
}

void developer(string arg, string exp, string msg){
  Ss args = split(arg, '\n'); string act = domain(args.at(0), args.at(1));
  zz_act("Sniper Test", retToSp(act), retToSp(exp), msg);
}
void develop(){
  string req1 = read_file("a:/pj/sniper/rr/req1.txt"), res1 = read_file("a:/pj/sniper/rr/res1.txt");
  string req2 = read_file("a:/pj/sniper/rr/req2.txt"), res2 = read_file("a:/pj/sniper/rr/res2.txt");
  string req3 = read_file("a:/pj/sniper/rr/req3.txt"), res3 = read_file("a:/pj/sniper/rr/res3.txt");
  string req4 = read_file("a:/pj/sniper/rr/req4.txt"), res4 = read_file("a:/pj/sniper/rr/res4.txt");
  string req5 = read_file("a:/pj/sniper/rr/req5.txt"), res5 = read_file("a:/pj/sniper/rr/res5.txt");
  string req6 = read_file("a:/pj/sniper/rr/req6.txt"), res6 = read_file("a:/pj/sniper/rr/res11.txt");
  developer(req1, res1, "参加要請中に限度額以下の価格情報が来ると入札中になって買い注文をだすよ");
  developer(req2, res2, "参加要請中にオークションが閉まると落札失敗で買い注文は無し");
  developer(req3, res3, "入札中にオークションが閉まると落札失敗で注文はしない");
  developer(req4, res4, "一位入札中にオークションが閉まると落札成功で注文はしない");
  developer(req5, res5, "一位入札中でも限度額以上の価格情報が来ると脱落中で注文はしない");
  developer(req6, res6, "ヘンテコなメッセージが来れば金額は０にしてエラー発生だ");
}

void product(){
  string arg = read_file("toSniper.txt"); Ss args = split(arg, '\n');
  string ans = domain(args.at(0), args.at(1));
  write_file("fromSniper.txt", ans);
}

int main(){
  // test();
  // refactor();
  // develop();
  product();
}

/*
g++ -Wall -Wextra -fexec-charset=cp932 src/cpp/src/sniper.cpp
.\a.exe
*/