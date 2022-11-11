#encoding: UTF-8

@black   = "\u001b[30m"
@red     = "\u001b[31m"
@green   = "\u001b[32m"
@yellow  = "\u001b[33m"
@blue    = "\u001b[34m"
@magenta = "\u001b[35m"
@cyan    = "\u001b[36m"
@white   = "\u001b[37m"
@reset   = "\u001b[0m"

module Colors
  def suc(text)   @green + text + @reset end;     module_function :suc
  def norm(text)  @blue + text + @reset  end;     module_function :norm
  def err(msg)    @red + msg + @reset    end;     module_function :err
  def fine(text)  @yellow + text + @reset end;    module_function :fine
end

include Colors
if __FILE__ == $0 then
  g = suc("hoge")
  puts g
end