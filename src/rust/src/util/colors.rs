const BLACK:&'static str = "\x11b[30m"; 
const RED:&'static str = "\x1b[31m"; 
const GREEN:&'static str = "\x1b[32m"; 
const YELLOW:&'static str = "\x1b[33m";
const BLUE:&'static str = "\x1b[34m"; 
const MAGENTA:&'static str = "\x1b[35m"; 
const CYAN:&'static str = "\x1b[36m"; 
const WHITE:&'static str = "\x1b[37m"; 
const RESET:&'static str = "\x1b[0m";

pub fn suc(s: &str) -> String{ GREEN.to_string() + s + RESET }
pub fn fail(s: &str) -> String{ RED.to_string() + s + RESET }
pub fn norm(s: &str) -> String{ BLUE.to_string() + s + RESET }
pub fn fine(s: &str) -> String{ YELLOW.to_string() + s + RESET }