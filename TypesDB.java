import org.graalvm.polyglot.*;
import java.util.*;

public class TypesDB {
	
	public enum Types {
		BOOLEAN, NATIVE_POINTER, NULL, NUMBER, STRING, SEEN, DYNAMIC_START
	}
	public static final Types[] fromInt = Types.values();

	private static List<Type> types = new ArrayList<Type>();
	
	public static void register(String lang, String src, Value v) {
		types.add(new Type(lang, src, v));
	}

	public static short getTypeID(Value v) {
		//TODO woefully unimplemented
		if(v.hasArrayElements()) {
			return (short)(Types.DYNAMIC_START.ordinal() + 1);
		} else {
			return (short)(Types.DYNAMIC_START.ordinal() + 3);
		}
	}

	public static Value newInstance(short id, Context c) {
		return types.get(id - fromInt.length).newInstance(c);
	}

	public static List<String> getMembers(short id) {
		if(id < fromInt.length)
			return new ArrayList<String>();

		return types.get(id - fromInt.length).members;
	}


	public static class Type {
		private String lang;
		private String src;
		public List<String> members;
		private Value prototype;

		public Type(String lang, String src, Value prototype) {
			this.lang = lang;
			this.src = src;
			this.prototype = prototype;
			this.members = new ArrayList<String>();

			for(String m : new TreeSet<String>(prototype.getMemberKeys())) {
				Value v = prototype.getMember(m);
				if(!v.canExecute()) { //TODO techincally can be executable and data
					this.members.add(m);
				} 
			}
		}

		public Value newInstance(Context c) {
			if(lang.equals("java")) {
				return prototype.getMetaObject().newInstance();
			} else {
				return c.eval(lang, src);
			}
		}
	}

}
