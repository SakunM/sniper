module Src.Haskell.Src.Sniper(sniper, translator, job)where
import Data.List
import qualified Data.Map as M
import Text.Regex
import Data.Char (isSpace)

import Src.Haskell.Util.Tester(zz_act)


main = do
  ans <- release
  -- ans <- develop
  -- ans <- refactor
  -- let ans = test
  putStrLn ans

{-
runghc src/haskell/src/Sniper.hs
:l src/haskell/src/Sniper.hs
cd src/haskell
cd ../..
runghc src/Sniper.hs
-}