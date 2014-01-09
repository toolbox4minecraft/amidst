package amidst.bytedata;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteClass {
	public static class AccessFlags {
		public static int
			PUBLIC = 0x01,
			PRIVATE = 0x02,
			PROTECTED = 0x04,
			STATIC = 0x08,
			FINAL = 0x10,
			VOLATILE = 0x40,
			TRANSIENT = 0x80;
	}
	/*ACC_PUBLIC	 0x0001	 Declared public; may be accessed from outside its package.
ACC_PRIVATE	 0x0002	 Declared private; usable only within the defining class.
ACC_PROTECTED	 0x0004	 Declared protected; may be accessed within subclasses.
ACC_STATIC	 0x0008	 Declared static.
ACC_FINAL	 0x0010	 Declared final; no further assignment after initialization.
ACC_VOLATILE	 0x0040	 Declared volatile; cannot be cached.
ACC_TRANSIENT	 0x0080	 Declared transient; not written or read by a persistent object manager.
*/
	public class Field {
		public int accessFlags;
		public Field() { }
	};
	
	private byte[] data;
	private boolean isValidClass;
	public int minorVersion;
	public int majorVersion;
	private int cpSize;
	private DataInputStream stream;
	private ClassConstant<?>[] constants;
	private int[] constantTypes;
	private String name;
	public int accessFlags;
	
	private Vector<ClassConstant<Integer>> stringIndices;
	private Vector<ReferenceIndex> methodIndices;
	private Vector<String[]> methods, properties, constructors;
	private Vector<Float> floatConstants;
	private Vector<Long> longConstants;
	
	public Field[] fields;
	public int methodCount;
	
	public ByteClass(String name, byte[] classData) {
		this.name = name;
		methods = new Vector<String[]>();
		properties = new Vector<String[]>();
		constructors = new Vector<String[]>();
		floatConstants = new Vector<Float>();
		longConstants = new Vector<Long>();
		methodIndices = new Vector<ReferenceIndex>();
		stringIndices = new Vector<ClassConstant<Integer>>();
		try {
			data = classData;
			stream = new DataInputStream(new ByteArrayInputStream(data));
			isValidClass = stream.readInt() == 0xCAFEBABE;
			if (isValidClass) {
				minorVersion = stream.readUnsignedShort();
				majorVersion = stream.readUnsignedShort();
				cpSize = stream.readUnsignedShort(); cpSize--;
				constants = new ClassConstant<?>[cpSize];
				constantTypes = new int[cpSize];
				long offset = 10;
				for (int q = 0; q < cpSize; q++) {
					byte tag = stream.readByte();
					offset++;
					constantTypes[q] = tag;
					switch (tag) {
						case 1: //String
							int len = stream.readUnsignedShort();
							String strVal = "";
							for (int i = 0; i < len; i++)
								strVal += (char)stream.readByte();
							constants[q] = new ClassConstant<String>(tag, offset, strVal);
							offset += 2 + len;
							break;
						case 3: //Int
							constants[q] = new ClassConstant<Integer>(tag, offset, stream.readInt());
							offset += 4;
							break;
						case 4: //Float
							float cFloat = stream.readFloat();
							constants[q] = new ClassConstant<Float>(tag, offset, cFloat);
							floatConstants.add(cFloat);
							offset += 4;
							break;
						case 5: //Long
							long cLong = stream.readLong();
							constants[q] = new ClassConstant<Long>(tag, offset, cLong);
							longConstants.add(cLong);
							offset += 8;
							q++;
							break;
						case 6: //Double
							constants[q] = new ClassConstant<Double>(tag, offset, stream.readDouble());
							offset += 8;
							q++;
							break;
						case 7: //Class reference
							constants[q] = new ClassConstant<Integer>(tag, offset, stream.readUnsignedShort());
							offset += 2;
							break;
						case 8: //String reference
							ClassConstant<Integer> strRef = new ClassConstant<Integer>(tag, offset, stream.readUnsignedShort());
							constants[q] = strRef;
							stringIndices.add(strRef);
							offset += 2;
							break;
						case 9: //Field reference
							constants[q] = new ClassConstant<ReferenceIndex>(tag, offset, new ReferenceIndex(stream.readUnsignedShort(), stream.readUnsignedShort()));
							offset += 4;
							break;
						case 10: //Method reference
							constants[q] = new ClassConstant<ReferenceIndex>(tag, offset, new ReferenceIndex(stream.readUnsignedShort(), stream.readUnsignedShort()));
							
							offset += 4;
							break;
						case 11: //Interface method reference
							constants[q] = new ClassConstant<ReferenceIndex>(tag, offset, new ReferenceIndex(stream.readUnsignedShort(), stream.readUnsignedShort()));
							offset += 4;
							break;
						case 12: //Name and type descriptor
							constants[q] = new ClassConstant<ReferenceIndex>(tag, offset, new ReferenceIndex(stream.readUnsignedShort(), stream.readUnsignedShort()));
							offset += 4;
							break;
					}
				}

				//Access Flags
				accessFlags = stream.readUnsignedShort();
				
				
				//This class
				stream.skip(2);
				
				//Super class
				stream.skip(2);
				
				//Interfaces
				int iCount = stream.readUnsignedShort();
				stream.skip(iCount*2);
				
				//Fields
				fields = new Field[stream.readUnsignedShort()];
				for (int i = 0; i < fields.length; i++) {
					fields[i] = new Field();
					fields[i].accessFlags = stream.readUnsignedShort();
					stream.skip(4);
					int attributeInfoCount = stream.readUnsignedShort();
					for (int q = 0; q < attributeInfoCount; q++) {
						stream.skip(2);
						int attributeCount = stream.readInt();
						for (int z = 0; z < attributeCount; z++)
							stream.skip(1);
					}
				}
				
				//Methods
				methodCount = stream.readUnsignedShort();
				for (int i = 0; i < methodCount; i++) {
					stream.skip(2);
					methodIndices.add(new ReferenceIndex(stream.readUnsignedShort(), stream.readUnsignedShort()));
					int attributeInfoCount = stream.readUnsignedShort();
					for (int q = 0; q < attributeInfoCount; q++) {
						stream.skip(2);
						int attributeCount = stream.readInt();
						for (int z = 0; z < attributeCount; z++)
							stream.skip(1);
					}
				}
				
				//Attributes
				
				stream.close();
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	public boolean searchForString(String str) {
		for (ClassConstant<Integer> i : stringIndices) {
			if (((String)constants[i.get() - 1].get()).contains(str))
				return true;
		}
		return false;
	}
	
	public byte[] getData() {
		
		return data;
	}
	
	@Override
	public String toString() {
		return "[ByteClass " + name + "]";
	}
	public String searchByReturnType(String type) {
		for (ReferenceIndex ref : methodIndices) {
			String refType = (String)constants[ref.val2-1].get();
			//Log.i("L" + type + " = " + refType);
			if (("L" + type + ";").equals(refType.substring(refType.indexOf(')') + 1)))
				return (String)constants[ref.val1-1].get();
		}
		return null;
	}
	public void addMethod(String method, String name) {
		methods.add(new String[] {method, name});
	}
	public String getClassName() {
		return name;
	}
	public Vector<String[]> getMethods() {
		return methods;
	}
	public void addProperty(String property, String name) {
		properties.add(new String[] { property, name});
	}
	public Vector<String[]> getProperties() {
		return properties;
	}
	public void addConstructor(String constructor, String name) {
		constructors.add(new String[] {constructor, name});
	}
	public Vector<String[]> getConstructors() {
		return constructors;
	}
	public boolean isInterface() {
		return (accessFlags & 0x0200) == 0x0200;
	}
	public boolean isFinal() {
		return (accessFlags & 0x0010) == 0x0010;
	}
	public String getArguementsForConstructor(int ID) {
		int i = 0;
		for (ReferenceIndex ref : methodIndices) {
			String name = (String)constants[ref.val1-1].get();
			if (name.equals("<init>")) {
				if (i==ID) {
					String args = (String)constants[ref.val2-1].get();
					return toArguementString(args);
				}
				i++;
			}
		}
		return "";
	}
	public static String toArguementString(String eArgs) {
		String[] args = readArguements(eArgs);
		String out = "(";
		for (int i = 0; i < args.length ;i++) {
			out += args[i] + ((i == args.length - 1)?"":",");
		}
		out += ")";
		return out;
	}
	public static String[] readArguements(String eArgs) {
		//Log.i(eArgs);
		String args = eArgs.substring(1);
		String[] argSplit = args.split("\\)");
		
		args = argSplit[0];
		Stack<String> argStack = new Stack<String>();
		Pattern argRegex = Pattern.compile("([\\[]+)?([BCDFIJSZ]|L[^;]+)");
		Pattern objectRegex = Pattern.compile("^([\\[]+)?[LBCDFIJSZ]");
		Matcher matcher = argRegex.matcher(args);
		while (matcher.find()) {
			String arg =args.substring(matcher.start(), matcher.end());
			Matcher objectMatcher = objectRegex.matcher(arg);
			if (objectMatcher.find()) {
				String replaceWith = "";
				switch (arg.charAt(objectMatcher.end()-1)) {
					case 'B':
						replaceWith = "byte";
						break;
					case 'C':
						replaceWith = "char";
						break;
					case 'D':
						replaceWith = "double";
						break;
					case 'F':
						replaceWith = "float";
						break;
					case 'I':
						replaceWith = "int";
						break;
					case 'J':
						replaceWith = "long";
						break;
					case 'S':
						replaceWith = "short";
						break;
					case 'Z':
						replaceWith = "boolean";
						break;
				}
				arg =   arg.substring(0, Math.max(0, objectMatcher.end()-1)) +
						replaceWith +
						arg.substring(Math.min(objectMatcher.end(), arg.length()));
				
			}
			argStack.push(arg);
		}
		String[] argArray = new String[argStack.size()];
		for (int i = 0; i < argArray.length; i++) {
			argArray[argArray.length - 1 - i] = argStack.pop();
		}
		return argArray;
	}
	public boolean searchForFloat(float f) {
		for (Float cFloat : floatConstants) {
			if (cFloat.floatValue() == f) {
				return true;
			}
		}
		return false;
	}
	public boolean searchForLong(long l) {
		for (Long cLong : longConstants) {
			if (cLong.longValue() == l) {
				return true;
			}
		}
		return false;
	}
}
