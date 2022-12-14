module Src.Haskell.Src.Sniper(sniper, translator, job)where
import Data.List
import qualified Data.Map as M

import Src.Haskell.Util.Tester(zz_act)
import Src.Haskell.Util.Util(trim, split)

bid_msg = "SOLVersion: 1.1; Command: BID; Price: "

data Sniper = Sniper {
  name :: String, item :: String, stopPrice :: Int, lastPrice :: Int, lastBid :: Int, state :: String, aucMsg :: String
}

instance Show Sniper where
  show s = (aucMsg s) ++ "\n" ++ (item s) ++ " " ++ (show(lastPrice s)) ++ " " ++ (show(lastBid s)) ++ " " ++ (state s)

sniper:: [String] -> Sniper; sniper(n:i:s:p:b:t:_) = Sniper n i (read s) (read p) (read b) t "toAuctionMessage is None"

mapping :: String -> M.Map String String; mapping msg = M.fromList(map(pair) (init(split msg ";"))) where
  pair f = tpls(split f ":"); tpls (k:v:_) = if k=="" || v=="" then("","") else (trim k, trim v)

checker :: Maybe String -> String; checker ms = maybe "Failed" id ms

data Translator = Close | Price Int Int String | Failed String deriving Show

missingField :: String -> String -> Translator
missingField msg field = aboutFailed $"msg is [" ++ msg ++ "] missing field [" ++ field ++ "]"

aboutPrice :: M.Map String String -> String -> Translator
aboutPrice maps msg = 
  let price =  checker (M.lookup "CurrentPrice" maps) in if price ==  "Failed"  then missingField msg "CurrentPrice" else
  let inc =    checker (M.lookup "Increment" maps)    in if inc ==    "Failed"  then missingField msg "Increment"    else
  let bidder = checker (M.lookup "Bidder" maps)       in if bidder == "Failed"  then missingField msg "Bidder"       else
  Price (read price) (read inc) bidder

aboutFailed :: String -> Translator; aboutFailed msg = Failed ("failed: " ++ msg)

translator :: String -> Translator
translator msg = let maps = mapping msg in case checker (M.lookup "Event" maps) of
  "CLOSE" -> Close; "PRICE" -> aboutPrice maps msg; other   -> missingField msg "Event"

close :: Sniper -> Sniper; close s | (state s) == "WINNING" = s {state = "WON"} | otherwise = s {state = "LOST"}

price :: Sniper -> Int -> Int -> String -> Sniper
price s p i b = if b == (name s) then s {lastPrice = p, state = "WINNING"} else
  let bid = p + i in if bid > (stopPrice s) then s {lastPrice = p, state = "LOSING"} else
    s { lastPrice = p, lastBid = bid, state = "BIDDING", aucMsg = bid_msg ++ (show bid) ++ ";"}

failed :: Sniper -> String -> Sniper; failed s m = s {lastPrice = 0, lastBid = 0, state = "FAILED", aucMsg = m}

job :: Sniper -> Translator -> String
job s t = case t of Close -> show (close s); (Price p i b) -> show $ price s p i b; (Failed m) -> show $ failed s m

domain :: [String] -> String; domain (items:msg:_) =
  let s = sniper (words items)
      t = translator msg
  in job s t

test = let bidding = "sniper item-123 3000 1000 1098 BIDDING"
           price_other = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1000; Increment: 98; Bidder: someone else;"
       in domain [bidding, price_other]

retToSp :: String -> String; retToSp s = let (a:b:_) = lines s in a ++ " " ++ b
refactor = do
  args <- readFile "a:/pj/sniper/rr/req1.txt"; exp <- readFile "a:/pj/sniper/rr/res2.txt"
  let act = domain(lines args)
  return (zz_act "Sniper Test" (retToSp act) (retToSp exp) "it makes snse to fail by Refactor")

developer args exp name = do
  let act = domain(lines args)
  return (zz_act "Sniper" (retToSp act) (retToSp exp) name)
develop = do
  req1 <- readFile "a:/pj/sniper/rr/req1.txt"; res1 <- readFile "a:/pj/sniper/rr/res1.txt"
  req2 <- readFile "a:/pj/sniper/rr/req2.txt"; res2 <- readFile "a:/pj/sniper/rr/res2.txt"
  req3 <- readFile "a:/pj/sniper/rr/req3.txt"; res3 <- readFile "a:/pj/sniper/rr/res3.txt"
  req4 <- readFile "a:/pj/sniper/rr/req4.txt"; res4 <- readFile "a:/pj/sniper/rr/res4.txt"
  req5 <- readFile "a:/pj/sniper/rr/req5.txt"; res5 <- readFile "a:/pj/sniper/rr/res5.txt"
  req6 <- readFile "a:/pj/sniper/rr/req6.txt"; res6 <- readFile "a:/pj/sniper/rr/res9.txt"
  ans1 <- developer req1 res1 "WnenReceivedBuiableInfoThenBuyAndJoining"
  ans2 <- developer req2 res2 "WnenJoiningIfReceivedCloseInfoWhileNoMsgAndLost"
  ans3 <- developer req3 res3 "WnenBiddingIfReceivedCloseInfoWhileNoMsgAndLost"
  ans4 <- developer req4 res4 "WnenWinningIfReceivedCloseInfoWhileNoMsgAndWon"
  ans5 <- developer req5 res5 "WnenWinningIfReceivedThooHiPriceInfoWhileNoMsgAndLosing"
  ans6 <- developer req6 res6 "WnenWinningIfReceivedFunnyMsgWhileFailMsgAndFailed"
  return ( ans1 ++ "\n" ++ ans2 ++ "\n" ++ ans3 ++ "\n" ++ ans4 ++ "\n" ++ ans5 ++ "\n" ++ ans6)

release :: IO (); release = do
  arg <- readFile "toSniper.txt"
  let ans = domain (lines arg)
  writeFile "fromSniper.txt" ans
  return ()

main = do
  -- let ans = test
  -- ans <- refactor
  -- ans <- develop
  release
  -- putStrLn ans

{-
runghc src/haskell/src/Sniper.hs
:l src/haskell/src/Sniper.hs
cd src/haskell
cd ../..
runghc src/Sniper.hs
-}