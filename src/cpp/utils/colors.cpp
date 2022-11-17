#pragma once

#include <iostream>
using std::cin; using std::cout; using std::endl;
#include <string>
using std::string; using std::to_string;

string black_cs   = "\x1b[30m";
string red_cs     = "\x1b[31m";
string green_cs   = "\x1b[32m";
string yellow_cs  = "\x1b[33m";
string blue_cs    = "\x1b[34m";
string magenta_cs = "\x1b[35m";
string cyan_cs    = "\x1b[36m";
string white_cs   = "\x1b[37m";
string reset_cs   = "\x1b[0m";

string suc(string t) { return green_cs + t + reset_cs;}
string err(string t) { return red_cs + t + reset_cs;}
string norm(string t) { return blue_cs + t + reset_cs;}
string fine(string t) { return yellow_cs + t + reset_cs;}

void colors_test() {
  cout << suc(string("suc")) << endl;
  cout << err(string("err")) << endl;
  cout << norm(string("norm")) << endl;
  cout << fine(string("fine")) << endl;
}
// int main(){ colors_test();}