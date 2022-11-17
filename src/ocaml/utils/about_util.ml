#use "./src/ocaml/utils/print_util.ml"
module S = Str

let split str dmt = S.split(S.regexp_string dmt) str
let to_i i = int_of_string i
let to_s s = string_of_int s

let rec sJoin dmt = function [] -> "" | [s] -> s | s :: ss -> s ^ dmt ^ " " ^ sJoin dmt ss
let sSs ss = "[" ^ sJoin ";" ss ^ "]"
let test_sSs() = ps (sSs ["sniper";"item-123"; "1300"; "0";"0"])

let read_to_file path = let chan = open_in path and buf = Buffer.create 4006 in
  let rec loop () = let line = input_line chan in Buffer.add_string buf (line ^ "\n"); loop () in
  try loop () with End_of_file -> Buffer.contents buf
let write_to_file path text = let chan = open_out path in output_string chan text; close_out chan

let tests() =
  test_sSs()

(* let() = tests() *)