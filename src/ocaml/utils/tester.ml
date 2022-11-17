#use "./src/ocaml/utils/print_util.ml"
#use "./src/ocaml/utils/about_util.ml"

let str_cut n str = if String.length str > n then String.sub str 0 n else str

let zz_act ?(dsp = "now printing!!") title act exp = let at = (norm "@") in
  if act = exp
    then let l1 = at ^ title ^ "  " ^ (suc "Succes!") ^ "  < " ^ (str_cut 30 act) 
      and l2 = " ..>  =>  " ^ (suc "OK!") ^ " " ^ (fine "passed") ^ "   " ^ dsp in ps (l1 ^ l2)
    else let l1 = at ^ title ^ "   <" ^ (fine dsp) ^ ">  *** " ^ (err "fiiled!") ^ "\n"
      and l2 = "         " ^ (suc "-- Expect") ^ " : " ^ exp ^ "\n" and l3 = "            " ^ (err "bad") ^ " was: " ^ act
      in ps (l1 ^ l2 ^ l3)

let sI n = string_of_int n
let tester_test title = 
  zz_act ~dsp:title "Tester Test" (sI 10) (sI 10);
  zz_act ~dsp:title "Tester Test" (sI 10) (sI 5)


(* let () = tester_test "Teter test" *)

(*
ocaml src/ocaml/test/tester.ml
*)