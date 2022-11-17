#pragma once
#include <iostream>
using std::cin; using std::cout; using std::endl;
#include <string>
using std::string; using std::to_string;
#include <boost/format.hpp>
using boost::format;

#include "colors.cpp"


// zz_act関数は説明付きテスト 成功時は一行で表し失敗した時はどこが違うか教えるよ
static int g_i = 1;

string str_cut (int n, const string &str){ if((int)str.size() > n){ return str.substr(0,n);} return str;}

void zz_act(string name, string act, string exp, string disp="disp is None."){
  string title = norm("@") + name + "-" + to_string(g_i++) + " ";
  if (act == exp){
    string suces = suc("Succes!") + "  " + str_cut(35, act) + "..  " + fine("=> ") + suc("OK!") + "  " + disp;
    cout << title << suces << endl;
  } else {
    string line1 = title + "  < " + fine(disp) + " >   ***" + err("Failed!!"); cout << line1 << endl;
    string line2 = "       " + suc("--Expect ") + ":  " + exp; cout << line2 << endl;
    string line3 = "       " + err("but was ") + " :  " + act; cout << line3 << endl;
  }
}

void tester_test() {
  zz_act("Tester Test", "mamo", "mamo","should ture");
}
// int main(){ tester_test();}