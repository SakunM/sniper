#![allow(unused)]

use std::fs::File;
use std::io::prelude::*;
use std::error::Error;
use std::process;

use rust;
use rust::domain;

mod util;
use util::colors::*;
use util::tester::zz_act;

fn read_file(path: &str) -> Result<String, Box<dyn Error>> {
    let mut f = File::open(path)?;
    let mut lines = String::new();
    f.read_to_string(&mut lines)?;
    Ok(lines)
}
fn read_file_wrap(path: &str) -> String{
    let r = read_file(path);
    let s = match r { Ok(lines) => lines, Err(e) => panic!("ファイルの読み込みに失敗したよ: {:?}", e)};
    s
}
fn write_file(path: &str, text: &str) -> Result<(), Box<dyn Error>> {
    let bs = text.as_bytes();
    let mut f = File::create(path)?;
    f.write_all(bs)?;
    Ok(())
}
fn test_wf(){
    let text = "hello this is a main.rs!!";
    if let Err(e) = write_file("a:/pj/sniper/mamo.txt", text){ println!("ファイルの書き込みに失敗したよ{}",e); process::exit(1);}
}
fn get_pair(hf: String) -> (String, String) {
    let mut hfl = hf.lines();
    let h = hfl.next().expect("分解に失敗したよ");
    let f = hfl.next().expect("分解に失敗したよ");
    (h.to_string(), f.to_string())
}
fn test_gp(){ let arg = rust::str("hoge\nfuga"); let (f,s) = get_pair(arg); println!("{} - {}",f, s);}

fn ret_to_sp(txt: String) -> String { let (a,b) = get_pair(txt); format!("{} {}",a,b)}

fn test(){ let res = ret_to_sp(rust::str("hoge\nfuga")); println!("{}",res);}

fn refactor(){
    let (arg, exp) = (read_file_wrap("a:/pj/sniper/rr/req1.txt"), read_file_wrap("a:/pj/sniper/rr/res2.txt"));
    let (items, msg) = get_pair(arg); let act = domain(items, msg);
    let (act, exp) = (ret_to_sp(act), ret_to_sp(exp));
    zz_act("Sniper Test", &act, &exp, "今回はあえて通らないテストを行って表示をチェックしたい");
}
fn devloper(arg: &str, exp: &str, ser: usize, disp: &str){
    let title = format!("Sniper Test - {}", ser);
    let (arg, exp) = (read_file_wrap(arg), read_file_wrap(exp));
    let (items, msg) = get_pair(arg); let act = domain(items, msg);
    let (act, exp) = (ret_to_sp(act), ret_to_sp(exp));
    zz_act(&title, &act, &exp, disp);
}
fn develop(){
    let arg1 = "a:/pj/sniper/rr/req1.txt"; let exp1 = "a:/pj/sniper/rr/res1.txt";
    let arg2 = "a:/pj/sniper/rr/req2.txt"; let exp2 = "a:/pj/sniper/rr/res2.txt";
    let arg3 = "a:/pj/sniper/rr/req3.txt"; let exp3 = "a:/pj/sniper/rr/res3.txt";
    let arg4 = "a:/pj/sniper/rr/req4.txt"; let exp4 = "a:/pj/sniper/rr/res4.txt";
    let arg5 = "a:/pj/sniper/rr/req5.txt"; let exp5 = "a:/pj/sniper/rr/res5.txt";
    let arg6 = "a:/pj/sniper/rr/req6.txt"; let exp6 = "a:/pj/sniper/rr/res13.txt";
    devloper(arg1,exp1,1, "参加要請中に入札可能な価格情報が来ると入札中になって買い注文を出すよ");
    devloper(arg2,exp2,2, "参加要請中にオークションが閉まると落札失敗");
    devloper(arg3,exp3,3, "入札中にオークションが閉まっても落札失敗");
    devloper(arg4,exp4,4, "一位入札中にオークションが閉まると落札成功");
    devloper(arg5,exp5,5, "一位入札中でも限度額以上の価格情報が来ると脱落中になって買い注文は出さないよ");
    devloper(arg6,exp6,6, "ヘンテコなメッセージが来ると金額はすべてゼロにしてエラー発生だ");
}

fn product(){
    let r = read_file("a:/pj/sniper/toSniper.txt");
    let s = match r { Ok(lines) => lines, Err(e) => {panic!("ファイルの読み込みに失敗したよ: {:?}", e)}};
    let (f,s) = get_pair(s);
    let res = domain(f,s);
    if let Err(e) = write_file("a:/pj/sniper/fromSniper.txt", &res) {
        println!("ファイルの書き込みに失敗したよ{}",e);
        process::exit(1);
    }
}
fn main() {
    // test();
    // refactor();
    // develop();
    product();
}
