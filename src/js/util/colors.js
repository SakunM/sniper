class Colors{
  constructor(){
    this.colors = {black: '\u001b[30m', red: '\u001b[31m', green: '\u001b[32m', yellow: '\u001b[33m', blue: '\u001b[34m',
      magenta: '\u001b[35m', cyan: '\u001b[36m', white: '\u001b[37m', reset: '\u001b[0m'}; 
  }
  suc(text){ const {green, reset} = this.colors; return green + text + reset;}
  norm(text){ const {blue, reset} = this.colors; return blue + text + reset;}
  err(msg){ const {red, reset} = this.colors; return red + msg + reset;}
  fine(msg){ const {yellow, reset} = this.colors; return yellow + msg + reset;}
}
function create(arg){ return new Colors(arg);}
module.exports = create;
if(require.main === module){
}