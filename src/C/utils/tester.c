#pragma once

#include "about_util.c"

void zz_act1(char *title, const char *act, char *exp, char *disp){
  char tds[WORDS], res[LINE*4], buf1[WORD], buf2[WORD], buf3[WORD], buf4[WORD]; 
  sprintf(tds, "%s %s %20s", norm("@ ",buf1), title, disp);
  if (strcmp(act,exp) == 0 ){ sprintf(res, "%20s actual : %-20s => %s passed", suc("Succes!",buf2), below_i(exp, 35), suc("OK!",buf3));}
  else{ sprintf(res, "%10s ☆ -- %s \n %20s -- Expect : %s \n %22s bat was : %s", " ",err("Failid!",buf4), " ", exp, " ", act);}
  printf("%s %s\n", tds, res);
}

static int g_i = 1;

char *str_cut(char *str, int n){
  if((int)strlen(str) < n) { return str;}
  str[n] = '\0'; return str;
}

void zz_act(char *name, char *act, char *exp, char *disp){
  char title[WORDS], buf1[WORD], buf2[WORD], buf3[WORD], buf4[WORD], buf5[WORD];
  sprintf(title, "%s %s-%d ", norm("@", buf1), name, g_i++);
  if(strcmp(act, exp) == 0){
    printf("%s %s < %s ..>  %s -->  %s: %s\n", 
      title, suc("Succes!", buf2), str_cut(exp, 30), suc("OK!!",buf3),fine("disp is ", buf4), disp);
  }else{
    char line_1[LINE]; sprintf(line_1, "< %s >  %s => %s\n", disp, fine("☆", buf2), err("failid!!", buf3));
    char line_2[LINE]; sprintf(line_2, "         %s : %s\n", suc("--Expect", buf4), act);
    char line_3[LINE]; sprintf(line_3, "          %s : %s", err("bat was", buf5), exp);
    printf("%s %s %s %s\n", title, line_1, line_2, line_3);
  }

}

