class Point:
  def __init__(self, x, y): self.x, self.y = x, y
  def __repr__(self): return "Point: " + str( self.__dict__)

def limit(p):
  if(p.x < 0 or p.y < 0): return Point(p.x if p.x > 0 else 0, p.y if p.y > 0 else 0)
  return p
  
def wrapper(fun):
  def checker(a,b):
    a, b = limit(a), limit(b)
    res = fun(a,b); res = limit(res); return  res
  return checker

@wrapper
def add(a, b): return Point(a.x + b.x, a.y + b.y)
@wrapper
def sub(a, b): return Point(a.x - b.x, a.y - b.y)


def deco():
  one, two, three = Point(100, 200), Point(300, 200), Point(-100, -100)
  res1, res2 = sub(one, two), add(one, three)
  print(res1, res2)
  

def logger(fun):
  def inner(*args, **kwargs):
    print(f"Arguments were: {args} {kwargs}")
    return fun(*args, **kwargs)
  return inner

@logger
def foo1(x,y): return x * y
@logger
def foo2(x, y=1): return x * y
@logger
def foo3(): return 34

def use_logger():
  print(foo1(5,4))
  print(foo2(5))
  print(foo3())
  print(foo1(x=10, y=32))
  

if __name__ == "__main__": use_logger()

'''
python src/python/src/deco.py
'''