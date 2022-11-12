const sp = require("../src/sniper"), assert = require("assert"), sinon = require("sinon"), expect = require("chai").expect,
  u = require("util");

class Sniper{ constructor(){} close(){} price(price, increment,bidder){} fail(msg){}}

describe("スナイパーとトランスレーターのテストだよ", () => {
  const item = "sniper item-123 3000 ", winning = item + "1098, 1098 WINNING", noMsg = "toAuctionMsssage is None",
    joining = item + "0 0 JOINING", bidding = item + "1000 1098 BIDDING";
  describe("とりあえず二つとも生成してみるよ", () => {
    it("スナイパーからね",() => {
      let s = new sp.Sniper(winning); assert.equal(s.name, "sniper"); assert.equal(s.item, "item-123");
      assert.equal(s.stopPrice, 3000); assert.equal(s.lastPrice, 1098); assert.equal(s.lastBid, 1098);
      assert.equal(s.state, "WINNING"); assert.equal(s.msg, noMsg);
    });
    it("トランスレーターもね！！", () => {
      let t = new sp.Translator("hoge", null);
      assert.equal(t.msg, "hoge"); assert.equal(t.sniper, null);
    });
  });
  describe("それぞれの終了メッセージへの対応をテストするよ",() => {
    const close_msg = "SOLVersion: 1.1; Event: CLOSE;";
    it("Sniper:参加要請中や入札中にオークションが閉まると落札失敗", () => {
      let sniper = new sp.Sniper(joining); sniper.close();
      assert.equal(sniper.msg, noMsg);assert.equal(sniper.state, "LOST");
      sniper = new sp.Sniper(bidding); sniper.close();
      assert.equal(sniper.msg, noMsg);assert.equal(sniper.state, "LOST");
    });
    it("Sniper:一位入札中にオークションが閉まると落札成功", () => {
      let sniper = new sp.Sniper(winning); sniper.close();
      assert.equal(sniper.msg, noMsg);assert.equal(sniper.state, "WON");
    });
    it("Translator:オークションが閉まるとSniperのCloseを呼ぶよ", () => {
      let sniper = new Sniper(), spy = sinon.spy(sniper, "close");
      let trans = new sp.Translator(close_msg, sniper); trans.translate();
      expect(spy.calledOnes);
    });
    it("SniperAndTranslator:参加要請中や入札中にオークションが閉まると落札失敗", () => {
      let sniper = new sp.Sniper(joining);
      let trans = new sp.Translator(close_msg, sniper); trans.translate();
      let [msg, state] = sniper.res().split('\n');
      assert.equal(msg, noMsg);assert.equal(state, "item-123 0 0 LOST");
      sniper = new sp.Sniper(bidding);
      trans = new sp.Translator(close_msg, sniper); trans.translate();
      [msg, state] = sniper.res().split('\n');
      assert.equal(msg, noMsg);assert.equal(state, "item-123 1000 1098 LOST");
    });
    it("SniperAndTranslator:一位入札中にオークションが閉まると落札成功", () => {
      let sniper = new sp.Sniper(winning);
      let trans = new sp.Translator(close_msg, sniper); trans.translate();
      let [msg, state] = sniper.res().split('\n');
      assert.equal(msg, noMsg);assert.equal(state, "item-123 1098 1098 WON");
    });
  });
  describe("今度は価格情報への対応をテストだ", () => {
    const price_msg = "Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;",
      bid_msg = "SOLVersion: 1.1; Command: BID; Price: %d;";
    it("Sniper:限度額以下の価格情報が来ると買い注文をだして入札中になるよ", () => {
      let sniper = new sp.Sniper(joining); sniper.price(1000, 98, "other bidder");
      assert.equal(sniper.msg, u.format(bid_msg, 1098)); assert.equal(sniper.state, "BIDDING");
    });
    it("Sniper:自分名義の価格情報が来ると一位入札中になって注文はしないよ", () => {
      let sniper = new sp.Sniper(bidding); sniper.price(1098, 98, "sniper");
      assert.equal(sniper.msg, noMsg); assert.equal(sniper.state, "WINNING");
    });
    it("Sniper:限度額を超えたら買い注文は出さないし脱落中になるよ", () => {
      let sniper = new sp.Sniper(winning); sniper.price(2950, 51, "other bidder");
      assert.equal(sniper.msg, noMsg); assert.equal(sniper.state, "LOSING");
    });
    it("Translator:価格情報が来るとSniperのPriceを引数付きで呼ぶよ", () => {
      let sniper = new Sniper(); spy = sinon.spy(sniper, "price");
      let trans = new sp.Translator(u.format(price_msg, 1000, 98, "bidder"), sniper); trans.translate();
      expect(spy.getCall(0).args).to.deep.equal([1000, 98, "bidder"]);
    });
    it("SniperAndTranslator:限度額以下の価格情報が来ると買い注文をだして入札中になるよ", () => {
      let sniper = new sp.Sniper(joining);
      let trans = new sp.Translator(u.format(price_msg, 1000, 98, "other bidder"), sniper);
      trans.translate(); let [msg, state] = sniper.res().split('\n');
      assert.equal(sniper.msg, u.format(bid_msg, 1098)); assert.equal(state, "item-123 1000 1098 BIDDING");
    });
    it("SniperAndTranslator:自分名義の価格情報が来ると一位入札中になって注文はしないよ", () => {
      let sniper = new sp.Sniper(bidding);
      let trans = new sp.Translator(u.format(price_msg, 1098, 28, "sniper"), sniper);
      trans.translate(); let [msg, state] = sniper.res().split('\n');
      assert.equal(sniper.msg, noMsg); assert.equal(state, "item-123 1098 1098 WINNING");
    });
    it("SniperAndTranslator:限度額を超えたら買い注文は出さないし脱落中になるよ", () => {
      let sniper = new sp.Sniper(winning);
      let trans = new sp.Translator(u.format(price_msg, 2800, 201, "other one"), sniper);
      trans.translate(); let [msg, state] = sniper.res().split('\n');
      assert.equal(sniper.msg, noMsg); assert.equal(state, "item-123 2800 1098 LOSING");
    });
  });
  describe("最後は異常系を調べるよ",() => {
    it("Sniper:ヘンテコなメッセージを受け取るとエラー発生ね", () => {
      let sniper = new sp.Sniper(winning); sniper.fail("a broken message");
      assert.equal(sniper.msg.includes("a broken message"), true); assert.equal(sniper.state, "FAILED");
    });
    it("SniperAndTranslator:少しヘンテコなメッセージを受け取ってもエラー発生だ", () => {
      let sniper = new sp.Sniper(winning);
      let trans  = new sp.Translator("Event: PRICE; Increment: 24; Bidder: Same one;", sniper);
      trans.translate(); let [msg, state] = sniper.res().split('\n');
      console.log(msg);
      assert.equal(msg.includes("failed: "), true); assert.equal(state, "item-123 0 0 FAILED");
    });
  });
});

/*
mocha src/js/test/sniper-test.js
*/