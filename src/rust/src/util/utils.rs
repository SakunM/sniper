pub fn maxs(ns: &Vec<i32>) -> i32{ let mut res = std::i32::MIN; for n in ns { if *n > res { res = *n;}} res}
pub fn mins(ns: &Vec<i32>) -> i32{ let mut res = std::i32::MAX; for n in ns { if *n < res { res = *n;}} res}
pub fn sums(ns: &Vec<i32>) -> i32{ let mut res = 0; for n in ns{ res += n;} res}