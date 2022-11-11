# coding: utf-8

black   = '\u001b[30m'
red     = '\u001b[31m'
green   = '\u001b[32m'
yellow  = '\u001b[33m'
blue    =  '\u001b[34m'
magenta = '\u001b[35m'
cyan    = '\u001b[36m'
white   =  '\u001b[37m'
reset   = '\u001b[0m'

def suc(text):  return green + text + reset
def norm(text): return blue + text + reset
def err(msg):   return red + msg + reset
def fine(text): return yellow + text + reset


if __name__ == "__main__":
  text = suc("green")
  print(text)