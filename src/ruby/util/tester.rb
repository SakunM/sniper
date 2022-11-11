#encoding: UTF-8

require 'a:/pj/sc/langs/ruby/utils/colors.rb'

module Tester
  def zz_act(title,act,exp,disp = "")
    at = norm("@")
    if act.to_s == exp.to_s
      green = suc("Succes!"); ok = suc("OK!"); passed = fine("passed"); act30 = act[0..30]
      puts("#{at} #{title} #{green}  [#{act30} ..] =>  #{ok} #{passed}  #{disp}")
    else
      puts("#{at} #{title} #{disp}")
      fail = err("☆-Fielid")
      puts("  #{fail} -- Expect    : #{exp}\n               bat was  : #{act}")
    end
  end
  module_function :zz_act
end

include Tester
if __FILE__ == $0 then
  zz_act("Tester Test", 10, 5, "test-1")
  zz_act("Tester Test", 10, 10, "test-2")
end
  