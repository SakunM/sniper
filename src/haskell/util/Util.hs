module Src.Haskell.Util.Util (trim, split) where

import Text.Regex
import Data.Char (isSpace)

trim :: String -> String; trim = f . f where f = reverse . dropWhile isSpace
split :: String -> String -> [String]; split cs dmt = splitRegex(mkRegex dmt) cs