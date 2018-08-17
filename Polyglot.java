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
 

        //primitives
        //if(v.isBoolean isNativePointer isNull isNumber isString isHostObject
        //        asBoolean asNativePointer asString asHostObject asFloat asLong...
				
        //array
        if(v.hasArrayElements()) {
          for(int i = 0; i < v.getArraySize(); i++) {
            Value element = v.getArrayElement(i);
            System.out.println("array element " + i + ": " + element);
          }
        }

        //members
        if(v.hasMembers()) {
          for(String k : v.getMemberKeys()) {
            Value member = v.getMember(k);
            System.out.println("member " + k + ": " + member);
          }
        }
        

        //Class c = Class.forName("com.oracle.truffle.api.vm.PolyglotValue$Interop");
        /*
        //initialize serializer
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy())); 
        //register(kryo, v,new String[] {"impl", "asClassLiteral", "rootNode", "sourceVM"});
        //register(kryo, v,new String[] {"impl", "getArrayElement"});
        //register(kryo, v,new String[] {"impl", "languageContext"});
        //register(kryo, v,new String[] {"receiver"});
       
        Object temp = getByName(v, new String[] {"impl", "asClassLiteral", "rootNode", "sourceVM", "contextClassLoader"});

        ClassLoader cl = (ClassLoader)getByName(v, new String[] {"impl"}).getClass().getClassLoader();
        cl.loadClass("com.oracle.truffle.api.vm.PolyglotValue$Interop");

        //Serialize
        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeClassAndObject(output, v);
        output.close();

        //Deserialize
        Input input = new Input(new FileInputStream("file.bin"));
        Object object2 = kryo.readClassAndObject(input);
        input.close();
        */
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

    public static Object getByName(Object host, String[] path) throws Exception {
        Object out = host;
        for(String s : path) {
            out = getByName(out, s);
        }
        return out;
    }

    //Register alternative serializer for host.(unrolled path)
    public static void register(Kryo kryo, Object host, String[] path) throws Exception{
        Object temp = getByName(host, path);
        Class c = temp.getClass();
        kryo.register(c, new ASTSerializer<>(kryo,c));
    }
}
