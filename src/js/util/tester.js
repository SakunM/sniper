class Tester{
  constructor(title){ this.title = title; this.id = 1; this.colors = require('./colors')();}

  test(act,exp,disp="disp is none"){
    const c = this.colors, title = `${c.norm('@')} ${this.title}-${this.id++}`;
    if(act == exp) {
      const suc = c.suc("Sucess!"), ok = c.suc("OK!"), act20 = act.toString().slice(0, 20);
      console.log( title + `  ${suc}  <${c.fine(act20)} ..>  => ${ok} passed!!  ${disp}`);
    }
    else {
      const fail = c.err("Failid!"), bat = c.err("bat");
      console.log(`${title}  ${fail}  ${c.fine(disp)}`);
      console.log(`     Expect : ${exp.toString()}`);
      console.log(`    ${bat} was : ${act.toString()}`);
    }
  }
}
function create(arg){ return new Tester(arg);}
module.exports = create;
if(require.main === module){
  const t = new Tester("tester test");
  t.test(10,5,"test in");
  t.test(5,5,"test in");
}