module Sec1 where
import System.Environment

use_args:: IO(); use_args = do
  args<- getArgs
  putStrLn("Hello, " ++ args !! 0 ++ " and " ++ args !! 1)

use_calc:: IO(); use_calc = do
  args <- getArgs
  let a = read (args !! 0)
  let b = read (args !! 1)
  let ans = a + b
  putStrLn (show ans)

use_getLine:: IO(); use_getLine = do
  line <- getLine
  putStrLn $"Hello " ++ line
main = do
  -- use_args use_calc
  use_getLine
  

{-
runghc src/haskell/src/Sec1.hs
-}