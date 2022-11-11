# coding: utf-8

from colors import suc, norm, err, fine

def zz_act(title,act,exp,disp = ""):
  at = norm("@")
  if act == exp :
    act = act[:26]; green = suc("Succes!"); ok = suc("OK!"); passed = fine("passed")
    print(f"{at} {title} {green} actual : => {ok} {passed} <{act} ..>  {disp} ")
  else :
    print(f"{at} {title} {disp:>30}")
    fail = err("☆-Fielid")
    print(f"  {fail} -- Expect   : {exp:>10}\n               bat was : {act:>10}")

if __name__ == "__main__":
  zz_act("Tester Test", 10, 10, "test-1")