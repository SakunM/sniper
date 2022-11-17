open Printf

let black   = "\x1b[30m"
let red     = "\x1b[31m"
let green   = "\x1b[32m"
let yellow  = "\x1b[33m"
let blue    = "\x1b[34m"
let magenta = "\x1b[35m"
let cyan    = "\x1b[36m"
let white   = "\x1b[37m"
let reset   = "\x1b[0m"

let suc cs   = green ^ cs ^ reset
let norm cs  = blue ^ cs ^ reset
let err cs   = red ^ cs ^ reset
let fine cs  = yellow ^ cs ^ reset

(* 改行付き表示系メソッド*)
let ps str  = printf "%s\n" str
let pi i    = printf "%d\n" i
let pb b    = printf "%b\n" b
let pf f    = printf "%f\n" f
let pc c    = printf "%c\n" c

(* 改行無し表示系メソッド*)
let pps str  = printf "%s" str
let ppi i    = printf "%d" i
let ppb b    = printf "%b" b
let ppf f    = printf "%f" f
let ppc c    = printf "%c" c

