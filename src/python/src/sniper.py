# coding: utf-8
from util.tester import zz_act

def reader(path):
  with open(path) as f: return f.read()
def writer(path, msg):
  with open(path, mode='w') as f: f.write(msg)

class Sniper:
  def __init__(self, items):
    self.name, self.item, sp, lp, lb, self.state = items.split(' ')
    self.stop_price, self.last_price, self.last_bid = int(sp), int(lp), int(lb)
    self.msg = "toAuctionMessage is None"
  def __str__(self): return  f"{self.msg}\n{self.item} {self.last_price} {self.last_bid} {self.state}"
  def close(self): self.state = "WON" if self.state == "WINNING" else "LOST"
  def price(self, price, increment, bidder):
    if bidder == self.name: self.last_price, self.state = price, "WINNING"; return
    bid = price + increment
    if bid > self.stop_price: self.last_price, self.state = price, "LOSING"; return
    self.last_price, self.last_bid, self.state, self.msg = price, bid, "BIDDING", f"SOLVersion: 1.1; Command: BID; Price: {bid};"
  def failed(self, msg):
    self.last_price, self.last_bid, self.state, self.msg = 0, 0, "FAILED", f"failed: {msg}"

class Translator:
  def __init__(self, msg, sniper): self.msg = msg; self.sniper = sniper
  def translate(self):
    dic = {}; fields = self.msg.split(';'); fields.pop()
    for f in fields:
      pair = f.split(':'); dic[pair[0].strip()] = pair[1].strip()
    try:
      event = dic["Event"]
      if (event == "CLOSE"): self.sniper.close(); return
      if (event == "PRICE"): self.sniper.price(int(dic["CurrentPrice"]), int(dic["Increment"]), dic["Bidder"]); return
    except KeyError as msg: self.sniper.failed(f"msg is [{self.msg}] Missing value field is [{str(msg)}]")

def domain(items, msg):
  sniper = Sniper(items); trans = Translator(msg, sniper); trans.translate()
  return str(sniper)
     
def test():
  text = "昔々有るところにおじいさんとおばあさんがおりました。"
  res = ""
  for i in range(10): res += text[i]
  print(res)

def refactor():
  issue = reader("a:/pj/sniper/rr/req1.txt"); exp = reader("a:/pj/sniper/rr/res2.txt")
  items, msg = issue.split('\n'); ans = domain(items, msg); exp = exp.replace('\n', ' '); ans = ans.replace('\n', ' ')
  zz_act("Snier Test", ans, exp, "今回の場合は失敗のメッセージは詳細に表したい")

def developer(args, exp, name):
  items, msg = args.split('\n'); act = domain(items, msg); exp = exp.replace('\n',' '); act = act.replace('\n', ' ')
  zz_act("Sniper Test", act, exp, name)
def develop():
  arg1 = reader("a:/pj/sniper/rr/req1.txt"); exp1 = reader("a:/pj/sniper/rr/res1.txt")
  arg2 = reader("a:/pj/sniper/rr/req2.txt"); exp2 = reader("a:/pj/sniper/rr/res2.txt")
  arg3 = reader("a:/pj/sniper/rr/req3.txt"); exp3 = reader("a:/pj/sniper/rr/res3.txt")
  arg4 = reader("a:/pj/sniper/rr/req4.txt"); exp4 = reader("a:/pj/sniper/rr/res4.txt")
  arg5 = reader("a:/pj/sniper/rr/req5.txt"); exp5 = reader("a:/pj/sniper/rr/res5.txt")
  arg6 = reader("a:/pj/sniper/rr/req6.txt"); exp6 = reader("a:/pj/sniper/rr/res7.txt")
  developer(arg1, exp1, "参加要請中に限度額以下の価格情報がくると入札中になって買い注文を出すよ")
  developer(arg2, exp2, "参加要請中にオークションが閉まると落札は失敗で買い注文は無しだ")
  developer(arg3, exp3, "入札中にオークションが閉まっても落札は失敗で買い注文は無しだ")
  developer(arg4, exp4, "一位入札中にオークションが閉まれば落札は成功で買い注文は無しだ")
  developer(arg5, exp5, "一位入札中でも限度額以上の価格情報がくれば脱落中で買い注文は無しだ")
  developer(arg6, exp6, "ヘンテコなメッセージが来ると金額はすべて０にしてエラー発生だ")

def product():
  items, msg = reader("toSniper.txt").split('\n')
  writer("fromSniper.txt", domain(items, msg))

if __name__ == "__main__":
  # test()
  # refactor()
  develop()
  # product()

'''
python src/python/src/sniper.py
'''