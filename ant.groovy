def writeAnt(){
  def writer = new FileWriter("build.xml");
  writer.write('<?xml version="1.0" encoding="UTF-8" ?>\n');
  def ant = new groovy.xml.MarkupBuilder(writer); ant.doubleQuotes = true;
  
  ant.project (name:"Main", basedir:".", default:"main") {
    path(id:"testClassPath"){
      pathelement(location: "build/classes")
      pathelement(location: "lib/junit-4.6-src.jar")
      pathelement(location: "lib/junit-dep-4.6.jar")
      pathelement(location: "lib/hamcrest-core-1.2.jar")
      pathelement(location: "lib/hamcrest-library-1.2.jar")
      pathelement(location: "lib/windowlicker-core-DEV.jar")
      pathelement(location: "lib/windowlicker-swing-DEV.jar")
      pathelement(location: "lib/smack_3_1_0.jar")
      pathelement(location: "lib/smackx_3_1_0.jar")
      pathelement(location: "lib/mockito-all-1.10.19.jar")
      pathelement(location: "lib/commons-io-1.4.jar")
    }
    path(id:"mainClassPath"){
      pathelement(location: "lib/smack_3_1_0.jar")
      pathelement(location: "lib/smackx_3_1_0.jar")
      pathelement(location: "lib/commons-lang-2.4.jar")
    }
    target(name:"clean") { delete(dir:"build")}
    target(name:"compile") {
      mkdir(dir:"build/classes")
      javac(srcdir:"src/main", destdir:"build/classes", encoding:"utf-8", includeantruntime:"no"){
        classpath(refid:"mainClassPath")
      }
      javac(srcdir:"src/test", destdir:"build/classes", encoding:"utf-8", includeantruntime:"no"){
        classpath(refid:"testClassPath")
      }
    }
    target(name:"MainTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.MainTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"TranslatorTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.xmpp.TranslatorTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"SniperTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.SniperTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"TableModelTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.ui.TableModelTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"SnapshotTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.SnapshotTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"ColumnTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.ui.ColumnTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"MainWindowTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.ui.MainWindowTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"XMPPAuctionHouseTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.xmpp.XMPPAuctionHouseTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"LauncherTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.LauncherTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"ItemTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.ItemTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"XMPPFailureReporterTest", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.xmpp.XMPPFailureReporterTest")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"Sniper2Test", depends:"compile"){
      junit(fork:"yes", haltonfailure:"yes") {
        test(name:"test.Sniper2Test")
        formatter(type: "plain", usefile:"false")
        classpath(refid:"testClassPath")
      }
    }
    target(name:"test", depends:"MainTest, SniperTest, TranslatorTest, TableModelTest, SnapshotTest, ColumnTest, \
      MainWindowTest, XMPPAuctionHouseTest, LauncherTest, ItemTest, XMPPFailureReporterTest, Sniper2Test")
    target(name:"jar", depends:"compile"){
      mkdir(dir:"build/jar")
      jar(destfile:"build/jar/Main.jar", basedir:"build/classes"){
        manifest{ attribute(name:"Main-Class", value:"main.Main")}
      }
    }
    target(name: "run", depends:"jar"){
      java(jar:"build/jar/Main.jar", fork:"true")
    }
    target(name:"clean-build", depends:"clean, jar")
    target(name:"main", depends:"clean, run")
  }
}

writeAnt();