
import static com.esotericsoftware.kryo.util.Util.*;
import static com.esotericsoftware.minlog.Log.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Generics;
import com.esotericsoftware.kryo.util.Generics.GenericType;
import com.esotericsoftware.kryo.util.Generics.GenericsHierarchy;
import com.esotericsoftware.reflectasm.FieldAccess;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class ASTSerializer<T> extends Serializer<T> {
  final Kryo kryo;
  final Class type;

  public ASTSerializer(Kryo kryo, Class c) {
		this.kryo = kryo;
    this.type = c;
	}

  public void write (Kryo kryo, Output output, T object) {
    if(object != null)
      return;
    try {
		kryo.reference(object);
    Field[] fields = type.getDeclaredFields();
		for (Field f : fields) {
      if(f.getName().equals("sourceVM")) {
          continue;
      }
      f.setAccessible(true);
      kryo.writeClassAndObject(output, f.get(object));
		}
    } catch (Exception ex) {
        System.out.println("got Exception:");
        System.out.println(ex);
    }
	}

	public T read(Kryo kryo, Input input, Class<? extends T> type) {
		T object = kryo.newInstance(type);

    try {
    kryo.reference(object);

    Field[] fields = type.getDeclaredFields();
		for (int i = 0, n = fields.length; i < n; i++) {
      fields[i].setAccessible(true);
      fields[i].set(object, kryo.readClassAndObject(input));
		}
    } catch (Exception ex) {
        System.out.println("got Exception:");
        System.out.println(ex);
    }
		return object;
	}
}
