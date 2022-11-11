const t = require("../util/tester.js")("Sniper Test"), fs = require("fs");

function reader(path) { return fs.readFileSync(path, "utf-8");}
function writer(path, msg){ fs.writeFileSync(path, msg, "utf-8");}
function test(){
  writer("mamo.txt", "from java script!!");
}

if(require.main === module){
  test()
  // refactor()
  // develop()
  // product()
}

/*
node src/js/src/sniper.js
 */