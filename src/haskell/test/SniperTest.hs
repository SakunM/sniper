import Test.HUnit
import System.IO
import qualified Data.Map as M
import Text.Regex
import Src.Haskell.Src.Sniper(sniper, translator, job)

sol = "SOLVersion: 1.1; "
close_msg = sol ++ "Event: CLOSE;"
no_msg = "toAuctionMessage is None"
price_other = sol ++ "Event: PRICE; CurrentPrice: 1000; Increment: 98; Bidder: someone else;"
price_me = sol ++ "Event: PRICE; CurrentPrice: 1098; Increment: 32; Bidder: sniper;"
price_over = sol ++ "Event: PRICE; CurrentPrice: 2800; Increment: 201; Bidder: a bidder;"
missing_event = sol ++ "Event: ;"
missing_bidder = sol ++ "Event: PRICE; CurrentPrice: 197; Increment: 98; : some one;"
bid_msg = sol ++ "Command: BID; Price: 1098;"
joining = "sniper item-123 3000 0 0 JOINING"
bidding = "sniper item-123 3000 1000 1098 BIDDING"
winning = "sniper item-123 3000 1098 1098 WINNING"

translatorTest = do
  let close = translator close_msg
      price = translator price_other
      broken = translator "a broken message"
      missingEvent = translator missing_event
      missingBidder = translator missing_bidder
      tests = TestList
        [
          "translatorTest 1" ~: "Close" ~=? show close,
          "translatorTest 2" ~: "Price 1000 98 \"someone else\"" ~=? show price,
          "translatorTest 3" ~: "Failed \"failed: msg " ~=? take 20 (show broken),
          "translatorTest 4" ~: "missing field [Event]" ~=? take 21 (drop 51 (show missingEvent)),
          "translatorTest 5" ~: "missing field [Bidder]" ~=? take 22 (drop 102 (show missingBidder))
        ]
  runTestText(putTextToHandle stderr False) tests

sniperTest = do
  let j = sniper (words joining)
      b = sniper (words bidding)
      w = sniper (words winning)
      tests = TestList
        [
          "sniperJoiningTest" ~: no_msg ++ "\n" ++ "item-123 0 0 JOINING" ~=? show j,
          "sniperBiddingTest" ~: no_msg ++ "\n" ++ "item-123 1000 1098 BIDDING" ~=? show b,
          "sniperWinningTest" ~: no_msg ++ "\n" ++ "item-123 1098 1098 WINNING" ~=? show w
        ]
  runTestText(putTextToHandle stderr False) tests

closeWhenJoiningJobTest = do
  let s = sniper (words joining)
      t = translator close_msg
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenJoiningMsg" ~: no_msg ~=? msg,
          "closeWhenJoiningState" ~: "item-123 0 0 LOST" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

closeWhenBiddingJobTest = do
  let s = sniper (words bidding)
      t = translator close_msg
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenBiddingMsg" ~: no_msg ~=? msg,
          "closeWhenBiddingState" ~: "item-123 1000 1098 LOST" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

closeWhenWinningJobTest = do
  let s = sniper (words winning)
      t = translator close_msg
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenWinningMsg" ~: no_msg ~=? msg,
          "closeWhenWinningState" ~: "item-123 1098 1098 WON" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

sniperBiddingTest = do
  let s = sniper (words joining)
      t = translator price_other
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenWinningMsg" ~: bid_msg ~=? msg,
          "closeWhenWinningState" ~: "item-123 1000 1098 BIDDING" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

sniperWinningTest = do
  let s = sniper (words bidding)
      t = translator price_me
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenWinningMsg" ~: no_msg ~=? msg,
          "closeWhenWinningState" ~: "item-123 1098 1098 WINNING" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

sniperLosingTest = do
  let s = sniper (words winning)
      t = translator price_over
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenWinningMsg" ~: no_msg ~=? msg,
          "closeWhenWinningState" ~: "item-123 2800 1098 LOSING" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

sniperFailedTest = do
  let s = sniper (words winning)
      t = translator "a broken message"
      (msg:state:_) = lines (job s t)
      tests = TestList
        [
          "closeWhenWinningMsg" ~: "failed: msg is [a broken message] missing field [Event]."~=? msg,
          "closeWhenWinningState" ~: "item-123 0 0 FAILED" ~=? state
        ]
  runTestText(putTextToHandle stderr False) tests

main = do
  translatorTest
  sniperTest
  closeWhenJoiningJobTest
  closeWhenBiddingJobTest
  closeWhenWinningJobTest
  sniperBiddingTest
  sniperWinningTest
  sniperLosingTest
  sniperFailedTest


{-
runghc src/haskell/test/SniperTest.hs
-}