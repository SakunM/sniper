#include <stdio.h>

void func(int *i) { *i = 2;}

int main(void){
  int i = 1;
  func(&i);
  printf("i = %d\n",i);
}