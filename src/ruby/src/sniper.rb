#encoding: UTF-8

require './src/ruby/util/tester.rb'

def reader(path) File.read(path) end
def writer(path, msg) File.write(path, msg) end

class Sniper
  attr_reader :msg, :state;
  def initialize(items)
    @name, @item, sp, lp, lb, @state = items.split(' ')
    @stop_price, @last_price, @last_bid, @msg = sp.to_i, lp.to_i, lb.to_i, "toAuctionMessage is None"
  end
  def to_s() "#@msg\n#@item #@last_price #@last_bid #@state" end
  def close() @state = @state == "WINNING" ? "WON" : "LOST" end
  def price(price, increment, bidder)
    if bidder == @name then @last_price, @state = price, "WINNING"; return end
    bid = price + increment; if bid > @stop_price then @last_price, @state = price, "LOSING"; return end
    bid_msg = "SOLVersion: 1.1; Command: BID; Price: %d;"
    @last_price, @last_bid, @state, @msg = price, bid, "BIDDING", sprintf(bid_msg, bid)
  end
  def fail(msg) @last_price, @last_bid, @state, @msg = 0, 0, "FAILED", "failed: " + msg + "." end
end

class Translator
  def initialize(msg, sniper) @msg, @sniper, @dic = msg, sniper, {} end
  def mk_hash() @msg.split(';').each{|field| k,v = field.split(':'); k.strip!; v.strip!; @dic[k.to_sym] = v } end
  def get(key) res = @dic[key]; if res == nil then raise "missing field [#{key}]" else res end end
  def event() get(:Event) end; def get_int(key) get(key).to_i end
  def price() get_int(:CurrentPrice) end; def increment() get_int(:Increment) end; def bidder() get(:Bidder) end
  def translate()
    mk_hash;
    @sniper.close if event == "CLOSE"
    @sniper.price(price, increment, bidder) if event == "PRICE"
    rescue => e then @sniper.fail("msg is [#@msg]" + ' ' + e.message)
  end
end

def domain(items, msg)
  sniper = Sniper.new(items)
  trans = Translator.new(msg, sniper)
  trans.translate
  sniper.to_s
end
def str_cut(text, n)
  res = ""
  for i in 0..n do res += text[i] end
  res
end
def test()
  args = "sniper item-123 1300 0 0 JOINING\nSOLVersion: 1.1; Event: CLOSE;"
  res = str_cut(args, 30);
  puts res
end

def refactor()
  items, msg = (reader "a:/pj/sniper/rr/req1.txt").split("\n"); exp = reader "a:/pj/sniper/rr/res2.txt"
  act = domain(items, msg); act = act.gsub("\n", ' '); exp = exp.gsub("\n", ' ')
  zz_act "Sniper Test", act, exp, "此処ではテストが通らない時には詳細がほしい"
end

def developer(args, exp, name)
  act = domain(*args.split("\n")); act = act.gsub("\n", ' '); exp = exp.gsub("\n", ' ')
  zz_act("Sniper Test", act, exp, name)
end
def develop()
  arg1 = reader "a:/pj/sniper/rr/req1.txt"; exp1 = reader "a:/pj/sniper/rr/res1.txt"
  arg2 = reader "a:/pj/sniper/rr/req2.txt"; exp2 = reader "a:/pj/sniper/rr/res2.txt"
  arg3 = reader "a:/pj/sniper/rr/req3.txt"; exp3 = reader "a:/pj/sniper/rr/res3.txt"
  arg4 = reader "a:/pj/sniper/rr/req4.txt"; exp4 = reader "a:/pj/sniper/rr/res4.txt"
  arg5 = reader "a:/pj/sniper/rr/req5.txt"; exp5 = reader "a:/pj/sniper/rr/res5.txt"
  arg6 = reader "a:/pj/sniper/rr/req6.txt"; exp6 = reader "a:/pj/sniper/rr/res6.txt"
  developer arg1, exp1, "参加要請中に限度額以下の価格情報がくると入札中になって買い注文を出すよ"
  developer arg2, exp2, "参加要請中にオークションが閉まると落札は失敗で買い注文は無しだ"
  developer arg3, exp3, "入札中にオークションが閉まっても落札は失敗で買い注文は無しだ"
  developer arg4, exp4, "一位入札中にオークションが閉まれば落札は成功で買い注文は無しだ"
  developer arg5, exp5, "一位入札中でも限度額以上の価格情報がくれば脱落中で買い注文は無しだ"
  developer arg6, exp6, "ヘンテコなメッセージが来ると金額はすべて０にしてエラー発生だ"
end

def product()
  writer("fromSniper.txt", domain(*reader("toSniper.txt").split("\n")))
end

if __FILE__ == $0 then
  # test
  # refactor
  # develop
  product
end
__END__
ruby src/ruby/src/sniper.rb