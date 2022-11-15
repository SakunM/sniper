import Data.List
import qualified Data.Map as M
import Text.Regex
import Data.Char (isSpace)

trim :: String -> String; trim = f . f where f = reverse . dropWhile isSpace
split :: String -> String -> [String]; split cs dmt = splitRegex(mkRegex dmt) cs

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

main = do
  arg <- readFile "toSniper.txt"
  let ans = domain (lines arg)
  writeFile "fromSniper.txt" ans

{-
ghc src/haskell/src/SniperZ.hs
-}