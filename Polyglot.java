import org.graalvm.polyglot.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;
import java.io.*;

import java.lang.reflect.Field;

class Polyglot {
    public static void main(String[] args) throws Exception {
        //Get js object
        Context polyglot = Context.create();
        Value v = polyglot.eval("js", "[1,2,42,4]");
 
        Class c = Class.forName("com.oracle.truffle.api.vm.PolyglotValue$Interop");

        //initialize serializer
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy())); 
        register(kryo, v,new String[] {"impl", "asClassLiteral", "rootNode", "sourceVM"});
        register(kryo, v,new String[] {"impl", "getArrayElement"});
        register(kryo, v,new String[] {"impl", "languageContext"});
        register(kryo, v,new String[] {"receiver"});
       

        //Serialize
        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeClassAndObject(output, v);
        output.close();

        //Deserialize
        Input input = new Input(new FileInputStream("file.bin"));
        Object object2 = kryo.readClassAndObject(input);
        input.close();
    }

    //Get a field by name (ignoring access control)
    public static Object getByName(Object host, String name) throws Exception {
        Field f = null;
        Class c = host.getClass();
        while(f == null) {
          try {
            f = c.getDeclaredField(name);
          }catch (Exception ex) {
            //System.out.println("couldn't find " + name + " in " + host);
          }
          c = c.getSuperclass();
        }
        f.setAccessible(true);
        return f.get(host);
    }

    //Register alternative serializer for host.(unrolled path)
    public static void register(Kryo kryo, Object host, String[] path) throws Exception{
        Object temp = host;
        for(String s : path) {
          temp = getByName(temp, s);
        }
        Class c = temp.getClass();
        kryo.register(c, new ASTSerializer<>(kryo,c));
    }
}
