use super::colors::norm;
use super::colors::fine;
use super::colors::suc;
use super::colors::fail;

pub fn zz_act(title: &str, act: &str, exp: &str, disp: &str){
    let title = format!("{} {}", norm("@"), title);
    if act == exp {
        let succes = format!("{} <{} ..>  => {} : {}", suc("Succes!!"), &exp[..30], suc("OK!"), disp);
        println!("{}  {}",title, succes);
    } else {
        println!("{}  {} <{}>   --- {}",title, fail("Failed!!"), disp, fine("becouse.."));
        println!("        --- {} : {}",suc("Expect"), exp);
        println!("           {} : {}",fail("bat was"), act);
    }
}