module Src.Haskell.Util.Colors(suc, norm, err, fine)where

black   = "\x11b[30m"
red     = "\x1b[31m"
green   = "\x1b[32m"
yellow  = "\x1b[33m"
blue    = "\x1b[34m"
magenta = "\x1b[35m"
cyan    = "\x1b[36m"
white   = "\x1b[37m"
reset   = "\x1b[0m"


suc cs   = green ++ cs ++ reset
norm cs  = blue ++ cs ++ reset
err cs   = red ++ cs ++ reset
fine cs  = yellow ++ cs ++ reset


main = do
  let hoge = fine "fuga"
  putStrLn hoge

{-
runghc src/haskell/src/Colors.hs
-}