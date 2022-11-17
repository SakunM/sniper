#pragma once

#include <iostream>
using std::cin; using std::cout; using std::endl;

#include <fstream>
using std::ofstream; using std::ifstream;

#include <vector>
using std::vector;

#include <string>
using std::string; using std::to_string;

#include <sstream>
using std::stringstream;

using Ns    = vector<int>;
using Nss   = vector<vector<int>>;
using Ss    = vector<string>;
using Sss   = vector<vector<string>>;
using Cs    = vector<char>;
using Css   = vector<vector<char>>;

void write_file(string path, string text){
  ofstream ofs(path);
  ofs << text << endl;
}

string read_file(string path){
  string res; ifstream ifs(path); string buf;
  while (!ifs.eof()){ std::getline(ifs,buf); res += buf + "\n";}
  return res;
}

Ss split(const string &str, char sep){
  Ss v; stringstream ss(str); string buffer;
  while(getline(ss, buffer, sep)){ v.push_back(buffer);}
  return v;
}

string trim(const string &str){
  const char *sps = " \t\v\r\n";
  int left = str.find_first_not_of(sps);
  int right = str.find_last_not_of(sps);
  return str.substr(left, right - left + 1);
}

string sSs(const Ss &ss){
  int i;
  string res = "["; for(i= 0; i<(int)ss.size()-1; i++){ res += ss[i]; res += ",";}
  res += ss[i]; res += "]"; return res;
}

// int main(){ tests();}

/*
g++ -Wall -Wextra -fexec-charset=cp932 src/cpp/utils/about_util.cpp
.\a.exe
*/