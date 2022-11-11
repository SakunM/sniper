# coding: utf-8

import unittest
from unittest import mock
import sys
sys.path.append("src/python"); sys.path.append("src/python/src")

from src.sniper import Sniper, Translator

class SniperTest(unittest.TestCase):
  skip = False
  joining = "sniper item-123 1300 0 0 JOINING"
  bidding = "sniper item-123 1300 1000 1098 BIDDING"
  winning = "sniper item-123 1300 1098 1098 WINNING"
  close_msg = "SOLVersion: 1.1; Event: CLOSE;"
  price_msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"
  bid_msg =  "SOLVersion: 1.1; Command: BID; Price: %d;"
  no_msg = "toAuctionMessage is None"
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator少しヘンテコなメッセージが来てもエラー発生(self):
    msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1250; Bidder: other bidder;"
    sniper = Sniper(self.bidding); trans = Translator(msg, sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertTrue("Missing value field is ['Increment']" in msg); self.assertEqual(state, "item-123 0 0 FAILED")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslatorヘンテコのメッセージが来るとエラー発生(self):
    sniper = Sniper(self.winning); trans = Translator("a broken message", sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertTrue("a broken message" in msg); self.assertEqual(state, "item-123 0 0 FAILED")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator一位入札中でも限度額以上の価格情報が来ると脱落中になるよ(self):
    sniper = Sniper(self.winning); trans = Translator(self.price_msg % (1250, 51, "some one"), sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertEqual(msg, self.no_msg); self.assertEqual(state, "item-123 1250 1098 LOSING")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator参加要請中に限度額以下の価格情報が来ると参加して買い注文を出すよ(self):
    sniper = Sniper(self.joining); trans = Translator(self.price_msg % (1000, 98, "a biider"), sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertEqual(msg, self.bid_msg % 1098); self.assertEqual(state, "item-123 1000 1098 BIDDING")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator一位入札中ならオークションが閉まれば落札成功(self):
    sniper = Sniper(self.winning); trans = Translator(self.close_msg, sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertEqual(msg, self.no_msg); self.assertEqual(state, "item-123 1098 1098 WON")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator入札中でもオークションが閉まれば落札失敗(self):
    sniper = Sniper(self.bidding); trans = Translator(self.close_msg, sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertEqual(msg, self.no_msg); self.assertEqual(state, "item-123 1000 1098 LOST")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_SniperAndTranslator参加要請中にオークションが閉まれと落札失敗(self):
    sniper = Sniper(self.joining); trans = Translator(self.close_msg, sniper); trans.translate()
    msg, state = str(sniper).split('\n')
    self.assertEqual(msg, self.no_msg); self.assertEqual(state, "item-123 0 0 LOST")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Translatorヘンテコメッセージを受け取るとスナイパーのFailedを呼ぶよ(self):
    sniper = mock.Mock(); trans = Translator("a broken message", sniper); trans.translate()
    self.assertTrue(sniper.failed.colled)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Translator価格メッセージを受け取るとスナイパーのPriceを呼ぶよ(self):
    sniper = mock.Mock(); trans = Translator(self.price_msg % (1000, 98, "bidder"), sniper); trans.translate()
    self.assertEqual(str(sniper.price.call_args_list[0]), "call(1000, 98, 'bidder')")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Translator終了メッセージを受け取るとスナイパーのCloseを呼ぶよ(self):
    sniper = mock.Mock(); trans = Translator(self.close_msg, sniper); trans.translate()
    self.assertTrue(sniper.close.colled)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper一位入札中でもエラーになればゼロに戻してエラー発生だ(self):
    sniper = Sniper(self.winning); sniper.failed("hoge")
    self.assertEqual(sniper.state, "FAILED")
    self.assertEqual(sniper.last_price, 0)
    self.assertEqual(sniper.last_bid, 0)
    self.assertEqual("failed:" in sniper.msg, True)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper一位入札中でも限度額以上の価格情報が届くと脱落中になって買い注文は出せないよ(self):
    sniper = Sniper(self.winning); sniper.price(1250, 51, "other budder")
    self.assertEqual(sniper.state, "LOSING")
    self.assertEqual(sniper.last_price, 1250)
    self.assertEqual(sniper.msg, self.no_msg)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper入札中に自分名義の価格情報が届くと一位入札中になって買い注文は出さないよ(self):
    sniper = Sniper(self.bidding); sniper.price(1098, 28, "sniper")
    self.assertEqual(sniper.state, "WINNING")
    self.assertEqual(sniper.last_price, 1098)
    self.assertEqual(sniper.msg, self.no_msg)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper参加要請中に限度額以下の価格情報が届くと入札中になって買い注文を出すよ(self):
    sniper = Sniper(self.joining); sniper.price(1000, 98, "a other")
    self.assertEqual(sniper.state, "BIDDING")
    self.assertEqual(sniper.last_bid, 1098)
    self.assertEqual(sniper.msg, self.bid_msg % 1098)
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper一位入札中ならオークションが閉まると落札成功(self):
    sniper = Sniper(self.winning); sniper.close(); self.assertEqual(sniper.state, "WON")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper入札中でもオークションが閉まると落札失敗(self):
    sniper = Sniper(self.bidding); sniper.close(); self.assertEqual(sniper.state, "LOST")
  
  @unittest.skipIf(skip,"新しいテストが通るまでね")
  def test_Sniper参加要請中にオークションが閉まると落札失敗(self):
    sniper = Sniper(self.joining); sniper.close(); self.assertEqual(sniper.state, "LOST")

if __name__ == "__main__": unittest.main()

'''
python src/python/test/sniper_test.py
'''
  