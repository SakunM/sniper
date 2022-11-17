#pragma once

#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include <ctype.h>

#define WORD 32
#define WORDS 64
#define LINE 128

char *black   = "\x1b[30m";
char *red     = "\x1b[31m";
char *green   = "\x1b[32m";
char *yellow  = "\x1b[33m";
char *blue    = "\x1b[34m";
char *magenta = "\x1b[35m";
char *cyan    = "\x1b[36m";
char *white   = "\x1b[37m";
char *reset   = "\x1b[0m";

char *strcachers(char *str, char *color, char *buf){
  char *res = strcpy(buf,color); res = strcat(res, str); res = strcat(res,reset);
  return buf;
}
char *suc(char *str, char *buf){ return strcachers(str,green,buf);}
char *norm(char *str, char *buf){ return strcachers(str,blue,buf);}
char *err(char *str, char *buf){ return strcachers(str,red,buf);}
char *fine(char *str, char *buf){ return strcachers(str,yellow,buf);}

void pi(int arg){printf("%d\n",arg);}
void pc(char arg){printf("%c\n",arg);}
void ps(char *arg){printf("%s\n",arg);}
void pf(double arg){printf("%f\n", arg);}
void pp(void *arg){printf("%p\n", arg);}
void pb(bool b){printf("%s\n", b ? "true" : "false");}

void write_file(char *path, char *str){ FILE *fp; fp = fopen(path,"w"); fprintf(fp, str); fclose(fp);}
void read_file(char *path, char *buff){
  FILE *fp; fp = fopen(path, "r"); char ch; int id = 0;
  while((ch = fgetc(fp)) != EOF){ buff[id++] = ch;} 
  buff[id] = '\0'; fclose(fp);
}

char *rtrim(char *s){ int len = strlen(s); while (len && isspace(s[len-1])){ --len;} s[len] = '\0'; return s;}
char *ltrim(char *s){ int len = strlen(s), pos = strspn(s, " \n\r\t\v"); memmove(s, s+pos, len -= pos); s[len] = '\0'; return s;}
char *trim(char *s){ return ltrim(rtrim(s));}

char *below_i(char *str, int n){ if(n < (int)strlen(str)){ str[n] = '\0';} return  str;}
