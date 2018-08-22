import org.graalvm.polyglot.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

class Polyglot {

	public static void main(String[] args) throws Exception {

      String[] supportedLangs = { "js", "python"};

        Context polyglot = Context.newBuilder(supportedLangs)
            .allowAllAccess(true)
            .build();
        Value v1 = polyglot.eval(Source.newBuilder("js", new File("./arraydef.js")).build());
        Value v2 = polyglot.eval(Source.newBuilder("js", new File("./classdef.js")).build());
				Value v3 = polyglot.asValue(new TestClass().mutate());
        Value v4 = polyglot.eval(Source.newBuilder("js", new File("./complexclassdef.js")).build());
        //Value v5 = polyglot.eval(Source.newBuilder("python", new File("./class.py")).build());
        //v5.getMember("mutate").execute();

				TypesDB.register(v1);
				TypesDB.register(v2);
				TypesDB.register(v3);
        TypesDB.register(v4);
        TypesDB.register(v4.getMember("child1"));
        TypesDB.register(v4.getMember("child1").getMember("grandchild"));
        //TypesDB.register(v5);
        
				Value v = v4;

				System.out.println("--Writing--");
				Writer w = new Writer(new FileOutputStream("file.bin"));
				w.write(v);
				w.close();

				System.out.println();
				System.out.println();

				System.out.println("--Reading--");
				Reader r = new Reader(new FileInputStream("file.bin"), polyglot);
				Value vout = r.read();
				r.close();

				System.out.println();
				System.out.println();

				System.out.println(vout);
    }

	public static class TestClass {

    public static int global = 5;
		public String fielda = "test";
		private String fieldb = "test2"; //TODO no private fields allowed as of current

		public TestClass() {}

    public TestClass mutate() {
      fielda = "mutated";
      fieldb = "mutatedb";
      return this;
    }

    public TestClass construct() {
      return new TestClass();
    }

		public String toString() {
			return fielda + " " + fieldb;
		}
	}
}
