#load "str.cma"

#use "./src/ocaml/utils/tester.ml"
#use "./src/ocaml/utils/print_util.ml"
#use "./src/ocaml/utils/about_util.ml"

module S = Str module L = List module St = String

let joining = "sniper item-123 1300 0 0 JOINING"
let bidding = "sniper item-123 1300 1000 1098 BIDDING"
let winning = "sniper item-123 1300 1098 1098 WINNING"
let close_msg = "SOLVersion: 1.1; Event: CLOSE;"
let price_msg = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: other bidder;"
let price_me = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1098; Increment: 24; Bidder: sniper;"
let price_over = "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 1280; Increment: 24; Bidder: some one;"
let price_not_val = "SOLVersion: 1.1; Event: PRICE; CurrentPrice:; Increment: 24; Bidder: other bidder;"
let price_not_CP = "SOLVersion: 1.1; Event: PRICE; Increment: 24; Bidder: other bidder;"
let price_not_EV = "SOLVersion: 1.1; CurrentPrice: 1098; Increment: 24; Bidder: other bidder;"
let no_msg = "toAuctionMessage is None"
let bid_msg = "SOLVersion: 1.1; Command: BID; Price: "

let lines str = let ss = split str "\n" in (L.nth ss 0, L.nth ss 1)

let words str = let ss = split str " " in (L.nth ss 0, L.nth ss 1, L.nth ss 2, L.nth ss 3, L.nth ss 4, L.nth ss 5)

let ret_to_sp ss = let (a,b) = lines ss in (St.trim a ) ^ " " ^ (St.trim b)

let test_lw() = let (a,b) = lines (joining ^ "\n" ^ close_msg) and (c,d,e,f,g,h) = words joining
  in ps (a^b^c^d^e^f^g^h)

type sniper = 
  {name : string; item : string; stop_price : int; last_price : int; last_bid : int; state : string; auc_msg : string}

let str_sniper {item = i; last_price = lp; last_bid = lb; state = st; auc_msg = am} = 
  am ^ "\n" ^ i ^ " " ^ to_s lp ^ " " ^ to_s lb ^ " " ^ st 

let mk_sniper (a,b,c,d,e,f) =
  {name = a; item = b; stop_price = to_i c; last_price = to_i d; last_bid = to_i e; state = f; auc_msg = no_msg}

let test_s() = let j = mk_sniper(words joining) in ps (str_sniper j)


type translator = Price of int * int * string | Failed of string | Close

let str_trans = function Price (a,b,c) -> to_s a ^ " " ^ to_s b ^ " " ^ c | Failed s -> "failed: " ^ s | close -> "close"

let test_st() = let res = str_trans (Price (25, 43, "bidder")) in ps res

let lack = Failure "not enough args" and empty s = Failure ("empty field val is " ^ s)
  and missing s = Failure ("missing field is " ^ s)

let semi_colons str = split str ";" and colons str = split str ":"

let safe_colons = function ([] | _::[]) -> raise lack | a::b::_ -> (St.trim a, St.trim b)

let pairs msg =
  let rec inner = function [] -> [] | x::xs -> safe_colons(colons x) :: inner xs in inner (semi_colons msg)

let test_ps() = let res = pairs price_msg in L.iter(fun (a,b) -> ps (a ^ " " ^ b)) res

let is_empty k v = if v = "" then raise (empty k) else v

let rec find_msg key = function [] -> raise (missing key)
  | (k,v) :: xs -> if k = key then is_empty k v else find_msg key xs

let event msg = let msgs = pairs msg in let ev = find_msg "Event" msgs in match ev with
  | "CLOSE" -> Close
  | "PRICE" -> Price (to_i(find_msg "CurrentPrice" msgs), to_i(find_msg "Increment" msgs), (find_msg "Bidder" msgs))
  | _       -> Failed "Event field is notFound"

let test_ev() = let res = event close_msg in ps (str_trans res)

let translate msg = try event msg with Failure e -> Failed ("<" ^ msg ^ ">  [" ^ e ^ "]")

let test_t() = let res = translate price_not_EV in ps (str_trans res)


let close s = if s.state = "WINNING" then {s with state = "WON"} else {s with state = "LOST"}

let price s p i b = if b = s.name then {s with last_price = p; state = "WINNING"} else
  let bid = p + i in if bid > s.stop_price then {s with last_price = p; state = "LOSING"} else
  {s with last_price = p; last_bid = bid; state = "BIDDING"; auc_msg = bid_msg ^ to_s bid ^ ";"}

let failed s m = {s with last_price = 0; last_bid = 0; state = "FAILED"; auc_msg = "failed: " ^ m}

let job s = function Close -> close s | Price (a,b,c) -> price s a b c | Failed m -> failed s m

let domain (items, msg) =
  let s = mk_sniper (words items) and t = translate msg in let res = job s t in str_sniper res

let test() = let res = domain (bidding, price_me) in ps res

let refactor() = let arg = read_to_file "a:/pj/sniper/rr/req1.txt" and exp = read_to_file "a:/pj/sniper/rr/res2.txt" in
  let res = domain (lines arg) in
    zz_act ~dsp: "WhenJoinningIfReceivedCloseInfoWhileNoMsgAndLost by Refactor" "Sniper Test" (ret_to_sp res) (ret_to_sp exp)

let developer arg exp name = zz_act ~dsp:name "Sniper Test" (ret_to_sp(domain(lines arg))) (ret_to_sp exp)
let develop() =
  let req1 = read_to_file "a:/pj/sniper/rr/req1.txt" and res1 = read_to_file "a:/pj/sniper/rr/res1.txt" in
  let req2 = read_to_file "a:/pj/sniper/rr/req2.txt" and res2 = read_to_file "a:/pj/sniper/rr/res2.txt" in
  let req3 = read_to_file "a:/pj/sniper/rr/req3.txt" and res3 = read_to_file "a:/pj/sniper/rr/res3.txt" in
  let req4 = read_to_file "a:/pj/sniper/rr/req4.txt" and res4 = read_to_file "a:/pj/sniper/rr/res4.txt" in
  let req5 = read_to_file "a:/pj/sniper/rr/req5.txt" and res5 = read_to_file "a:/pj/sniper/rr/res5.txt" in
  let req6 = read_to_file "a:/pj/sniper/rr/req6.txt" and res6 = read_to_file "a:/pj/sniper/rr/res10.txt" in
  developer req1 res1 "WhenReceivedBuiableInfoThenBuyAndJoining";
  developer req2 res2 "WhenJoinningIfReceivedCloseInfoWhileNoMsgAndLost";
  developer req3 res3 "WhenBiddeingIfReceivedCloseInfoWhileMoMsgAndLost";
  developer req4 res4 "WhenWinningIfReceivedCloseInfoWhileNoMsgAndWon";
  developer req5 res5 "WhenWinningIfReceivedTooHiPriceInfoWhileNoMsgAndLosing";
  developer req6 res6 "WhenWinningIfReceivedFunnyMsgWhileFailMsgAndFailed"

let product() = let arg = read_to_file "toSniper.txt" in write_to_file "fromSniper.txt" (domain(lines arg))


let () = 
  (* test() *)
  (* refactor() *)
  (* develop() *)
  product()

(*
ocaml src/ocaml/src/sniper.ml
*)