module Src.Haskell.Util.Tester (zz_act)where
import Src.Haskell.Util.Colors (suc,err,fine,norm)

zz_act :: String -> String -> String -> String -> String
zz_act title act exp disp = let at = norm "@" in
  if act == exp
    then at ++ title ++ "  " ++ suc("Succes!") ++ "  < " ++ (take 35 act) ++ " ..>  " ++ (fine "disp is") ++ "  " ++  disp
    else 
      let l1 = at ++ title ++ "  " ++ err("Failed!") ++ "  disp is " ++ (fine disp) ++ "\n" 
          l2 = "       " ++ (suc "-- Expect") ++ ":  " ++ exp ++ "\n"
          l3 = "        " ++ (err "bat was") ++ " :  " ++ act
      in l1 ++ l2 ++ l3

main = do
  putStrLn$zz_act "B008 Test" (show 15)    (show 10) "test 1"
  putStrLn$zz_act "B008 Test" (show 15)    (show 15) "test 2"

{-
runghc src/haskell/src/Tester.hs
runghc Tester.hs
-}