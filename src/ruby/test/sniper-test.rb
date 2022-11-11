#encoding: UTF-8

require 'test/unit'
require 'flexmock/test_unit'
require './src/ruby/src/sniper.rb'

class SniperTest < Test::Unit::TestCase
  @skip = false
  @@joining = "sniper item-123 1300 0 0 JOINING"
  @@bidding = "sniper item-123 1300 1000 1098 BIDDING"
  @@winning = "sniper item-123 1300 1098 1098 WINNING"
  @@close_msg = "SOLVersion: 1.1; Event: CLOSE;"
  @@price_msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"
  @@broken_msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice:; Increment: 98; Bidder: hoge;"
  @@bid_msg = "SOLVersion: 1.1; Command: BID; Price: %d;"
  @@no_msg = "toAuctionMessage is None"

  def test_SniperAndTranslator少しヘンテコなメッセージが来てもエラー発生
    omit_if @skip,""
    sniper = Sniper.new(@@winning); trans = Translator.new(@@broken_msg, sniper); trans.translate
    assert sniper.to_s.include? @@broken_msg; assert sniper.to_s.include? "failed:"
  end

  def test_SniperAndTranslatorヘンテコなメッセージを受け取るとエラー発生
    omit_if @skip,""
    sniper = Sniper.new(@@bidding); trans = Translator.new("a broken message", sniper); trans.translate
    msg = "failed: msg is [a broken message] undefined method `strip!' for nil:NilClass."
    assert_equal(sniper.to_s, msg + "\nitem-123 0 0 FAILED")
  end

  def test_SniperAndTranslator一位入札中でも限度額以上の価格情報が来ると脱落中になるよ
    omit_if @skip,""; sniper = Sniper.new(@@winning)
    trans = Translator.new(sprintf(@@price_msg, 1250, 51, "some one"), sniper); trans.translate
    assert_equal(sniper.to_s, @@no_msg + "\nitem-123 1250 1098 LOSING", "価格と状態が変わる")
  end

  def test_SniperAndTranslator入札中に自分名義の価格情報が来ると一位入札中になって注文はしない  
    omit_if @skip,""; sniper = Sniper.new(@@bidding);
    trans = Translator.new(sprintf(@@price_msg, 1098, 35, "sniper"), sniper); trans.translate
    assert_equal(sniper.to_s, @@no_msg + "\nitem-123 1098 1098 WINNING", "価格と状態が変わる")
  end

  def test_SniperAndTranslator要請中に限度額以下の価格情報が来ると買い注文を出して入札中
    omit_if @skip,""; sniper = Sniper.new(@@joining);
    trans = Translator.new(sprintf(@@price_msg, 1000, 98, "a bidder"), sniper); trans.translate
    assert_equal(sniper.to_s, sprintf(@@bid_msg, 1098) + "\nitem-123 1000 1098 BIDDING", "価格と入札額と状態が変わる")
  end

  def test_SniperAndTranslator一位入札中でオークションが閉まると落札成功
    omit_if @skip,""
    sniper = Sniper.new(@@winning); trans = Translator.new(@@close_msg, sniper); trans.translate
    assert_equal(sniper.to_s, @@no_msg + "\nitem-123 1098 1098 WON")
  end

  def test_SniperAndTranslator参加要請中や入札中にオークションが閉まると落札失敗
    omit_if @skip,""
    sniper = Sniper.new(@@joining); trans = Translator.new(@@close_msg, sniper); trans.translate
    assert_equal(sniper.to_s, @@no_msg + "\nitem-123 0 0 LOST")
    sniper = Sniper.new(@@bidding); trans = Translator.new(@@close_msg, sniper); trans.translate
    assert_equal(sniper.to_s, @@no_msg + "\nitem-123 1000 1098 LOST")
  end

  def test_Translator少し壊れたメッセージを受け取るとFailを呼ぶよ
    omit_if @skip,""
    sniper = flexmock("sniper"); sniper.should_receive(:fail)
    trans = Translator.new(@@broken_msg, sniper); trans.translate
  end

  def test_Translator価格情報が来るとSniperのPriceを呼ぶよ
    omit_if @skip,""
    sniper = flexmock("sniper"); sniper.should_receive(:price).with(1000, 98, "other")
    trans = Translator.new(sprintf(@@price_msg, 1000, 98, "other"), sniper); trans.translate
  end

  def test_Translatorオークションが閉まるとSnierのCloseを呼ぶよ
    omit_if @skip,""
    sniper = flexmock("sniper"); sniper.should_receive(:close)
    trans = Translator.new(@@close_msg, sniper); trans.translate
  end

  def test_Sniperヘンテコなメッセージを受け取るとエラー発生
    omit_if @skip,""
    s = Sniper.new(@@winning); s.fail("a broken message")
    assert_equal(s.state, "FAILED"); assert(s.msg.include? "a broken message");
  end

  def test_Sniper一位入札中でも限度額以上の価格情報が来れば脱落中になるよ
    omit_if @skip,""
    s = Sniper.new(@@winning); s.price(1250, 51, "forth party")
    assert_equal(s.state, "LOSING"); assert_equal(s.msg, @@no_msg);
  end

  def test_Sniper入札中に自分名義の価格情報が来ると一位入札中になって注文はしないよ
    omit_if @skip,""
    s = Sniper.new(@@bidding); s.price(1098, 24, "sniper")
    assert_equal(s.state, "WINNING"); assert_equal(s.msg, @@no_msg);
  end

  def test_Sniper参加要請中に限度額以下の価格情報が来ると参加して買い注文をだすよ
    omit_if @skip,""
    s = Sniper.new(@@joining); s.price(1000, 98, "other")
    assert_equal(s.state, "BIDDING"); assert_equal(s.msg, sprintf(@@bid_msg, 1098));
  end

  def test_Sniper一位入札中にオークションが閉まると落札成功
    omit_if @skip,""
    s = Sniper.new(@@winning); s.close(); assert_equal(s.state, "WON");
  end

  def test_Sniper参加要請中や入札中にオークションが閉まると落札失敗
    omit_if @skip,""
    s1 = Sniper.new(@@joining); s1.close(); assert_equal(s1.state, "LOST");
    s1 = Sniper.new(@@bidding); s1.close(); assert_equal(s1.state, "LOST");
  end
end

__END__
ruby src/ruby/test/sniper-test.rb