class Tester{
  constructor(title){ this.title = title; this.id = 1; this.colors = require('./colors')();}
  test(act,exp,disp="disp is none"){
    const c = this.colors, sp = ' ', title = (`${c.norm('@')} ${this.title}-${this.id++}  ${(this.center(c.fine(disp),20)).padStart(22)}`);
    if(act.toString() === exp.toString()) {
      const suc = c.suc("Sucess!"), ok = c.suc("OK!"); 
      console.log( title + `${sp.padStart(3)} ${suc}   actual : ${act.toString().padEnd(20)}  => ${ok} passed!!`);
    }
    else { const fail = c.err("Failid!"), bat = c.err("bat"); console.log(title + `${sp.padStart(5)} *** ${fail}`);
      console.log(`${sp.padEnd(10)} Expect : ${exp.toString()}`);
      console.log(`${sp.padEnd(9)} ${bat} was : ${act.toString()}`);
    }
  }
  center(text,num){
    const len = text.length, pad = (num - len) / 2; let res = '';
    for(let i=0; i<pad; i++){ res += ' ';} res += text; for(let i=0; i<pad; i++){ res += ' ';} return res;
  }
}
function create(arg){ return new Tester(arg);}
module.exports = create;
if(require.main === module){
  const t = new Tester("tester test");
  t.test(10,5,"test in");
  t.test(5,5,"test in");
}