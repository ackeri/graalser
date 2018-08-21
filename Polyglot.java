import org.graalvm.polyglot.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

class Polyglot {

	public static void main(String[] args) throws Exception {
        Context polyglot = Context.create();
        Value v1 = polyglot.eval("js", "[1,2,42,4]");
        Value v2 = polyglot.eval("js", "{test:\"test\"}");
				Value v3 = polyglot.asValue(new TestClass());

				TypesDB.register("js", "[]", v1);
				TypesDB.register("js", "{}", v2);
				TypesDB.register("java", "new TestClass()", v3);
				
				Value v = v3;

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

		public String fielda = "test";
		private String fieldb = "test2";

		public TestClass() {}

		public String toString() {
			return fielda + " " + fieldb;
		}
	}
}
