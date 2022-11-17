#include "../utils/about_util.c"
#include "../utils/tester.c"


int next(const char *str, char *buf, int start, char dmt){
  int i, id = 0;
  for(i= start; i<(int)strlen(str); i++){ if(str[i] == dmt){ break;} buf[id++] = str[i];}
  buf[id] = '\0'; return i+1;
}
void lines(char *all, char *befor, char *after){ int i = next(all, befor, 0, '\n'); next(all, after, i, '\n');}

void test_ln(){ char *all = "hoge\nfuga", f[WORD], r[WORD]; lines(all, f, r); ps(f); ps(r);}


void sniper(char *all, char *nm, char *it, int *ps, char *st){
  char buf[WORD];
  int i = next(all, nm, 0, ' '); i = next(all, it, i, ' ');
  i = next(all, buf, i, ' '); ps[0] = atoi(buf); 
  i = next(all, buf, i, ' '); ps[1] = atoi(buf);
  i = next(all, buf, i, ' '); ps[2] = atoi(buf);
  next(all, st, i, ' ');
}

bool dmt_checker(char *msg){
  int col = 0, semicol = 0;
  for(int i= 0; i<(int)strlen(msg); i++){ if(msg[i] == ':'){ col++;} if(msg[i] == ';'){ semicol++;}}
  if (col != semicol){ return true;} return col == 2 || col == 5 ? false : true;
}

void fail(char *ans, char *item, char *msg, char *becouse){
  char buf[LINE*2]; sprintf(buf, "%s%s%s%s%s", "failed: msg is [", msg, "] because < ", becouse, ".>");
  sprintf(ans, "%s\n%s %d %d %s", buf, item, 0, 0, "FAILED");
}

void fail_dmt(char *ans, char *msg, char *item){ fail(ans, item, msg, "delmit error"); }

int translator(const char *msg, char *event, int *bids, char *bidder){
  char buff[WORDS];
  int i = next(msg, buff, 0, ';');
  i = next(msg, buff, i, ':'); trim(buff); if (strcmp(buff,"Event") != 0){ return 1;}
  i = next(msg, event, i, ';'); trim(event); if(strcmp(event,"CLOSE") == 0) { return 0;}
  i = next(msg, buff, i, ':'); trim(buff); if (strcmp(buff,"CurrentPrice") != 0){ return 2;}
  i = next(msg, buff, i, ';'); bids[0] = atoi(buff);
  i = next(msg, buff, i, ':'); trim(buff); if (strcmp(buff,"Increment") != 0){ return 3;}
  i = next(msg, buff, i, ';'); bids[1] = atoi(buff);
  i = next(msg, buff, i, ':'); trim(buff); if (strcmp(buff,"Bidder") != 0){ return 4;}
  i = next(msg, bidder, i, ';'); trim(bidder);
  return 0;
}

void close(char *state){
  char *res = strcmp(state, "WINNING") == 0 ? "WON" : "LOST"; strcpy(state, res);
}
void price(char *name, char *state, int *ps, char *msg, int *bids, char *bidder){
  if(strcmp(bidder, name) == 0) { ps[1] = bids[0]; strcpy(state, "WINNING"); return;}
  int bid = bids[0] + bids[1];
  if(bid > ps[0]){ ps[1] = bids[0], strcpy(state, "LOSING"); return;}
  sprintf(msg, "SOLVersion: 1.1; Command: BID; Price: %d;",bid);
  ps[1] = bids[0]; ps[2] = bid; strcpy(state, "BIDDING");
}

void domain(char *arg, char *ans){
  char items[LINE], msg[LINE], auc_msg[LINE]; sprintf(auc_msg, "toAuctionMessage is None"); lines(arg, items, msg);
  char name[WORDS], item[WORD], state[WORDS]; int ps[3]; sniper(items, name, item, ps, state);

  if(dmt_checker(msg)){fail_dmt(ans, msg, item); return;}

  char event[WORD], bidder[WORDS]; int bids[2];
  int err = translator(msg, event, bids, bidder);

  if(err != 0){
    switch(err){
      case 1:  fail(ans, item, msg, "misssing field Event"); return;
      case 2:  fail(ans, item, msg, "misssing field CurrentPrice"); return;
      case 3:  fail(ans, item, msg, "misssing field Increment"); return;
      case 4:  fail(ans, item, msg, "misssing field Bidder"); return;
    }
  }
  if(strcmp(event,"CLOSE") != 0 && strcmp(event,"PRICE") != 0){ fail(ans, item, msg, "misssing field value Event"); return;}
  if(strcmp(event,"CLOSE") == 0){ close(state);}
  if(strcmp(event,"PRICE") == 0){
    if(bids[0] == 0){ fail(ans, item, msg, "misssing field value CurrentPrice"); return;}
    if(bids[1] == 0){ fail(ans, item, msg, "misssing field value Increment"); return;}
    if(strlen(bidder) == 0){ fail(ans, item, msg, "misssing field value Bidder"); return;}
    price(name, state, ps, auc_msg, bids, bidder);
  }

  sprintf(ans, "%s\n%s %d %d %s", auc_msg, item, ps[1], ps[2], state);
}

const char *joining = "sniper item-123 1300 0 0 JOINING";
const char *bidding = "sniper item-123 1300 1000 1098 BIDDING";
const char *winning = "sniper item-123 1300 1098 1098 WINNING";
const char *close_msg   = "SOLVersion: 1.1; Event: CLOSE;";
const char *price_other   = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: other new;";
const char *price_me   = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 18; Bidder: sniper;";
const char *price_over   = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1280; Increment: 21; Bidder: some one;";
const char *funny_msg   = "SOLVersion: 1.1; Event: mamo; CurrentPrice: 1000; Increment:34; Bidder: hoge;";

void test(){
  char arg[LINE*2], ans[LINE*2]; sprintf(arg, "%s\n%s", winning, funny_msg);
  domain(arg, ans); ps(ans);
}

char *ret_to_sp(char *str){ for(int i= 0; i<(int)strlen(str); i++){ if(str[i] == '\n') {str[i] = ' ';}} return str;}

void refactor(){
  char arg[LINE*3], exp[LINE*2], act[LINE*3];
  read_file("a:/pj/sniper/rr/req1.txt", arg); read_file("a:/pj/sniper/rr/res1.txt", exp);
  domain(arg, act); zz_act("Sniper Test", ret_to_sp(act), ret_to_sp(exp),"あえて通らないテストの表示を確認するよ");
}

void developer(char *arg_path, char *exp_path, char *disp){
  char arg[LINE*3], exp[LINE*3], act[LINE*3];
  read_file(arg_path, arg); read_file(exp_path, exp); domain(arg, act);
  zz_act("Sniper Test", ret_to_sp(act), ret_to_sp(exp), disp);
}
void develop(){
  char *arg1 = "a:/pj/sniper/rr/req1.txt", *exp1 = "a:/pj/sniper/rr/res1.txt";
  char *arg2 = "a:/pj/sniper/rr/req2.txt", *exp2 = "a:/pj/sniper/rr/res2.txt";
  char *arg3 = "a:/pj/sniper/rr/req2.txt", *exp3 = "a:/pj/sniper/rr/res2.txt";
  char *arg4 = "a:/pj/sniper/rr/req4.txt", *exp4 = "a:/pj/sniper/rr/res4.txt";
  char *arg5 = "a:/pj/sniper/rr/req5.txt", *exp5 = "a:/pj/sniper/rr/res5.txt";
  char *arg6 = "a:/pj/sniper/rr/req6.txt", *exp6 = "a:/pj/sniper/rr/res12.txt";
  developer(arg1, exp1, "入札可能な価格情報が来ると入札して入札中になるよ");
  developer(arg2, exp2, "参加要請中にオークションが閉まると落札失敗");
  developer(arg3, exp3, "入札中にオークションが閉まっても落札失敗");
  developer(arg4, exp4, "一位入札中にオークションが閉まると落札成功");
  developer(arg5, exp5, "一位入札中でも限度額以上の価格情報が来ると脱落中だ");
  developer(arg6, exp6, "ヘンテコなメッセージが来ると金額は前部０でエラー発生");
}

void product(){
  char arg[LINE*3], act[LINE*3];
  read_file("toSniper.txt", arg);
  domain(arg,act); write_file("fromSniper.txt", act);
}

int main(void){
  // test();
  // refactor();
  // develop();
  product();
}

/*
gcc -Wall -Wextra -fexec-charset=cp932 src/c/src/sniper.c
*/