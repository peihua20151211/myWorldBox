package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import org.mozilla.javascript.TopLevel.Builtins;

public class NativeJavaObject implements Serializable, Scriptable, Wrapper {
    private static final Object COERCED_INTERFACE_KEY = "Coerced Interface";
    static final byte CONVERSION_NONE = (byte) 99;
    static final byte CONVERSION_NONTRIVIAL = (byte) 0;
    static final byte CONVERSION_TRIVIAL = (byte) 1;
    private static final int JSTYPE_BOOLEAN = 2;
    private static final int JSTYPE_JAVA_ARRAY = 7;
    private static final int JSTYPE_JAVA_CLASS = 5;
    private static final int JSTYPE_JAVA_OBJECT = 6;
    private static final int JSTYPE_NULL = 1;
    private static final int JSTYPE_NUMBER = 3;
    private static final int JSTYPE_OBJECT = 8;
    private static final int JSTYPE_STRING = 4;
    private static final int JSTYPE_UNDEFINED = 0;
    private static Method adapter_readAdapterObject = null;
    private static Method adapter_writeAdapterObject = null;
    static final long serialVersionUID = -6948590651130498591L;
    private transient Map<String, FieldAndMethods> fieldAndMethods;
    protected transient boolean isAdapter;
    protected transient Object javaObject;
    protected transient JavaMembers members;
    protected Scriptable parent;
    protected Scriptable prototype;
    protected transient Class<?> staticType;

    public NativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        this(scope, javaObject, staticType, false);
    }

    public NativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter) {
        this.parent = scope;
        this.javaObject = javaObject;
        this.staticType = staticType;
        this.isAdapter = isAdapter;
        initMembers();
    }

    protected void initMembers() {
        Class<?> dynamicType;
        if (this.javaObject != null) {
            dynamicType = this.javaObject.getClass();
        } else {
            dynamicType = this.staticType;
        }
        this.members = JavaMembers.lookupClass(this.parent, dynamicType, this.staticType, this.isAdapter);
        this.fieldAndMethods = this.members.getFieldAndMethodsObjects(this, this.javaObject, false);
    }

    public boolean has(String name, Scriptable start) {
        return this.members.has(name, false);
    }

    public boolean has(int index, Scriptable start) {
        return false;
    }

    public Object get(String name, Scriptable start) {
        if (this.fieldAndMethods != null) {
            Object result = this.fieldAndMethods.get(name);
            if (result != null) {
                return result;
            }
        }
        return this.members.get(this, name, this.javaObject, false);
    }

    public Object get(int index, Scriptable start) {
        throw this.members.reportMemberNotFound(Integer.toString(index));
    }

    public void put(String name, Scriptable start, Object value) {
        if (this.prototype == null || this.members.has(name, false)) {
            this.members.put(this, name, this.javaObject, value, false);
            return;
        }
        this.prototype.put(name, this.prototype, value);
    }

    public void put(int index, Scriptable start, Object value) {
        throw this.members.reportMemberNotFound(Integer.toString(index));
    }

    public boolean hasInstance(Scriptable value) {
        return false;
    }

    public void delete(String name) {
    }

    public void delete(int index) {
    }

    public Scriptable getPrototype() {
        if (this.prototype == null && (this.javaObject instanceof String)) {
            return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(this.parent), Builtins.String);
        }
        return this.prototype;
    }

    public void setPrototype(Scriptable m) {
        this.prototype = m;
    }

    public Scriptable getParentScope() {
        return this.parent;
    }

    public void setParentScope(Scriptable m) {
        this.parent = m;
    }

    public Object[] getIds() {
        return this.members.getIds(false);
    }

    @Deprecated
    public static Object wrap(Scriptable scope, Object obj, Class<?> staticType) {
        Context cx = Context.getContext();
        return cx.getWrapFactory().wrap(cx, scope, obj, staticType);
    }

    public Object unwrap() {
        return this.javaObject;
    }

    public String getClassName() {
        return "JavaObject";
    }

    public Object getDefaultValue(Class<?> hint) {
        if (hint == null && (this.javaObject instanceof Boolean)) {
            hint = ScriptRuntime.BooleanClass;
        }
        if (hint == null || hint == ScriptRuntime.StringClass) {
            return this.javaObject.toString();
        }
        String converterName;
        if (hint == ScriptRuntime.BooleanClass) {
            converterName = "booleanValue";
        } else if (hint == ScriptRuntime.NumberClass) {
            converterName = "doubleValue";
        } else {
            throw Context.reportRuntimeError0("msg.default.value");
        }
        Function converterObject = get(converterName, (Scriptable) this);
        if (converterObject instanceof Function) {
            Function f = converterObject;
            return f.call(Context.getContext(), f.getParentScope(), this, ScriptRuntime.emptyArgs);
        } else if (hint != ScriptRuntime.NumberClass || !(this.javaObject instanceof Boolean)) {
            return this.javaObject.toString();
        } else {
            return ScriptRuntime.wrapNumber(((Boolean) this.javaObject).booleanValue() ? 1.0d : 0.0d);
        }
    }

    public static boolean canConvert(Object fromObj, Class<?> to) {
        return getConversionWeight(fromObj, to) < 99;
    }

    static int getConversionWeight(Object fromObj, Class<?> to) {
        int fromCode = getJSTypeCode(fromObj);
        switch (fromCode) {
            case 0:
                if (to == ScriptRuntime.StringClass || to == ScriptRuntime.ObjectClass) {
                    return 1;
                }
                return 99;
            case 1:
                if (to.isPrimitive()) {
                    return 99;
                }
                return 1;
            case 2:
                if (to == Boolean.TYPE) {
                    return 1;
                }
                if (to == ScriptRuntime.BooleanClass) {
                    return 2;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 4;
                }
                return 99;
            case 3:
                if (to.isPrimitive()) {
                    if (to == Double.TYPE) {
                        return 1;
                    }
                    if (to != Boolean.TYPE) {
                        return getSizeRank(to) + 1;
                    }
                    return 99;
                } else if (to == ScriptRuntime.StringClass) {
                    return 9;
                } else {
                    if (to == ScriptRuntime.ObjectClass) {
                        return 10;
                    }
                    if (ScriptRuntime.NumberClass.isAssignableFrom(to)) {
                        return 2;
                    }
                    return 99;
                }
            case 4:
                if (to == ScriptRuntime.StringClass) {
                    return 1;
                }
                if (to.isInstance(fromObj)) {
                    return 2;
                }
                if (!to.isPrimitive()) {
                    return 99;
                }
                if (to == Character.TYPE) {
                    return 3;
                }
                if (to != Boolean.TYPE) {
                    return 4;
                }
                return 99;
            case 5:
                if (to == ScriptRuntime.ClassClass) {
                    return 1;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 4;
                }
                return 99;
            case 6:
            case 7:
                Object javaObj = fromObj;
                if (javaObj instanceof Wrapper) {
                    javaObj = ((Wrapper) javaObj).unwrap();
                }
                if (to.isInstance(javaObj)) {
                    return 0;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 2;
                }
                if (!to.isPrimitive() || to == Boolean.TYPE || fromCode == 7) {
                    return 99;
                }
                return getSizeRank(to) + 2;
            case 8:
                if (to != ScriptRuntime.ObjectClass && to.isInstance(fromObj)) {
                    return 1;
                }
                if (to.isArray()) {
                    if (fromObj instanceof NativeArray) {
                        return 2;
                    }
                    return 99;
                } else if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                } else {
                    if (to == ScriptRuntime.StringClass) {
                        return 4;
                    }
                    if (to == ScriptRuntime.DateClass) {
                        if (fromObj instanceof NativeDate) {
                            return 1;
                        }
                        return 99;
                    } else if (to.isInterface()) {
                        if ((fromObj instanceof NativeObject) || (fromObj instanceof NativeFunction)) {
                            return 1;
                        }
                        return 12;
                    } else if (!to.isPrimitive() || to == Boolean.TYPE) {
                        return 99;
                    } else {
                        return getSizeRank(to) + 4;
                    }
                }
            default:
                return 99;
        }
    }

    static int getSizeRank(Class<?> aType) {
        if (aType == Double.TYPE) {
            return 1;
        }
        if (aType == Float.TYPE) {
            return 2;
        }
        if (aType == Long.TYPE) {
            return 3;
        }
        if (aType == Integer.TYPE) {
            return 4;
        }
        if (aType == Short.TYPE) {
            return 5;
        }
        if (aType == Character.TYPE) {
            return 6;
        }
        if (aType == Byte.TYPE) {
            return 7;
        }
        if (aType == Boolean.TYPE) {
            return 99;
        }
        return 8;
    }

    private static int getJSTypeCode(Object value) {
        if (value == null) {
            return 1;
        }
        if (value == Undefined.instance) {
            return 0;
        }
        if (value instanceof CharSequence) {
            return 4;
        }
        if (value instanceof Number) {
            return 3;
        }
        if (value instanceof Boolean) {
            return 2;
        }
        if (value instanceof Scriptable) {
            if (value instanceof NativeJavaClass) {
                return 5;
            }
            if (value instanceof NativeJavaArray) {
                return 7;
            }
            if (value instanceof Wrapper) {
                return 6;
            }
            return 8;
        } else if (value instanceof Class) {
            return 5;
        } else {
            if (value.getClass().isArray()) {
                return 7;
            }
            return 6;
        }
    }

    @Deprecated
    public static Object coerceType(Class<?> type, Object value) {
        return coerceTypeImpl(type, value);
    }

    static Object coerceTypeImpl(Class<?> type, Object value) {
        if (value != null && value.getClass() == type) {
            return value;
        }
        switch (getJSTypeCode(value)) {
            case 0:
                if (type != ScriptRuntime.StringClass && type != ScriptRuntime.ObjectClass) {
                    reportConversionError("undefined", type);
                    break;
                }
                return "undefined";
                break;
            case 1:
                if (type.isPrimitive()) {
                    reportConversionError(value, type);
                }
                return null;
            case 2:
                if (type != Boolean.TYPE && type != ScriptRuntime.BooleanClass && type != ScriptRuntime.ObjectClass) {
                    if (type != ScriptRuntime.StringClass) {
                        reportConversionError(value, type);
                        break;
                    }
                    return value.toString();
                }
                return value;
                break;
            case 3:
                if (type == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(value);
                }
                if (type == ScriptRuntime.ObjectClass) {
                    return coerceToNumber(Double.TYPE, value);
                }
                if ((!type.isPrimitive() || type == Boolean.TYPE) && !ScriptRuntime.NumberClass.isAssignableFrom(type)) {
                    reportConversionError(value, type);
                    break;
                }
                return coerceToNumber(type, value);
                break;
            case 4:
                if (type == ScriptRuntime.StringClass || type.isInstance(value)) {
                    return value.toString();
                }
                if (type == Character.TYPE || type == ScriptRuntime.CharacterClass) {
                    if (((CharSequence) value).length() == 1) {
                        return Character.valueOf(((CharSequence) value).charAt(0));
                    }
                    return coerceToNumber(type, value);
                } else if ((!type.isPrimitive() || type == Boolean.TYPE) && !ScriptRuntime.NumberClass.isAssignableFrom(type)) {
                    reportConversionError(value, type);
                    break;
                } else {
                    return coerceToNumber(type, value);
                }
            case 5:
                if (value instanceof Wrapper) {
                    value = ((Wrapper) value).unwrap();
                }
                if (type != ScriptRuntime.ClassClass && type != ScriptRuntime.ObjectClass) {
                    if (type != ScriptRuntime.StringClass) {
                        reportConversionError(value, type);
                        break;
                    }
                    return value.toString();
                }
                return value;
                break;
            case 6:
            case 7:
                if (value instanceof Wrapper) {
                    value = ((Wrapper) value).unwrap();
                }
                if (!type.isPrimitive()) {
                    if (type != ScriptRuntime.StringClass) {
                        if (!type.isInstance(value)) {
                            reportConversionError(value, type);
                            break;
                        }
                        return value;
                    }
                    return value.toString();
                }
                if (type == Boolean.TYPE) {
                    reportConversionError(value, type);
                }
                return coerceToNumber(type, value);
            case 8:
                if (type == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(value);
                }
                if (type.isPrimitive()) {
                    if (type == Boolean.TYPE) {
                        reportConversionError(value, type);
                    }
                    return coerceToNumber(type, value);
                } else if (type.isInstance(value)) {
                    return value;
                } else {
                    if (type == ScriptRuntime.DateClass && (value instanceof NativeDate)) {
                        return new Date((long) ((NativeDate) value).getJSTimeValue());
                    }
                    if (type.isArray() && (value instanceof NativeArray)) {
                        NativeArray array = (NativeArray) value;
                        long length = array.getLength();
                        Class<?> arrayType = type.getComponentType();
                        Object Result = Array.newInstance(arrayType, (int) length);
                        for (int i = 0; ((long) i) < length; i++) {
                            try {
                                Array.set(Result, i, coerceTypeImpl(arrayType, array.get(i, array)));
                            } catch (EvaluatorException e) {
                                reportConversionError(value, type);
                            }
                        }
                        return Result;
                    } else if (value instanceof Wrapper) {
                        value = ((Wrapper) value).unwrap();
                        if (!type.isInstance(value)) {
                            reportConversionError(value, type);
                            break;
                        }
                        return value;
                    } else if (!type.isInterface() || (!(value instanceof NativeObject) && !(value instanceof NativeFunction))) {
                        reportConversionError(value, type);
                        break;
                    } else {
                        return createInterfaceAdapter(type, (ScriptableObject) value);
                    }
                }
                break;
        }
        return value;
    }

    protected static Object createInterfaceAdapter(Class<?> type, ScriptableObject so) {
        Object key = Kit.makeHashKeyFromPair(COERCED_INTERFACE_KEY, type);
        Object old = so.getAssociatedValue(key);
        if (old != null) {
            return old;
        }
        return so.associateValue(key, InterfaceAdapter.create(Context.getContext(), type, so));
    }

    private static Object coerceToNumber(Class<?> type, Object value) {
        Class<?> valueClass = value.getClass();
        if (type == Character.TYPE || type == ScriptRuntime.CharacterClass) {
            if (valueClass == ScriptRuntime.CharacterClass) {
                return value;
            }
            return Character.valueOf((char) ((int) toInteger(value, ScriptRuntime.CharacterClass, 0.0d, 65535.0d)));
        } else if (type == ScriptRuntime.ObjectClass || type == ScriptRuntime.DoubleClass || type == Double.TYPE) {
            if (valueClass != ScriptRuntime.DoubleClass) {
                return new Double(toDouble(value));
            }
            return value;
        } else if (type == ScriptRuntime.FloatClass || type == Float.TYPE) {
            if (valueClass == ScriptRuntime.FloatClass) {
                return value;
            }
            double number = toDouble(value);
            if (Double.isInfinite(number) || Double.isNaN(number) || number == 0.0d) {
                return new Float((float) number);
            }
            double absNumber = Math.abs(number);
            if (absNumber < 1.401298464324817E-45d) {
                return new Float(number > 0.0d ? 0.0d : -0.0d);
            } else if (absNumber <= 3.4028234663852886E38d) {
                return new Float((float) number);
            } else {
                return new Float(number > 0.0d ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY);
            }
        } else if (type == ScriptRuntime.IntegerClass || type == Integer.TYPE) {
            if (valueClass == ScriptRuntime.IntegerClass) {
                return value;
            }
            return Integer.valueOf((int) toInteger(value, ScriptRuntime.IntegerClass, -2.147483648E9d, 2.147483647E9d));
        } else if (type == ScriptRuntime.LongClass || type == Long.TYPE) {
            if (valueClass == ScriptRuntime.LongClass) {
                return value;
            }
            return Long.valueOf(toInteger(value, ScriptRuntime.LongClass, Double.longBitsToDouble(-4332462841530417152L), Double.longBitsToDouble(4890909195324358655L)));
        } else if (type == ScriptRuntime.ShortClass || type == Short.TYPE) {
            if (valueClass == ScriptRuntime.ShortClass) {
                return value;
            }
            return Short.valueOf((short) ((int) toInteger(value, ScriptRuntime.ShortClass, -32768.0d, 32767.0d)));
        } else if (type != ScriptRuntime.ByteClass && type != Byte.TYPE) {
            return new Double(toDouble(value));
        } else {
            if (valueClass == ScriptRuntime.ByteClass) {
                return value;
            }
            return Byte.valueOf((byte) ((int) toInteger(value, ScriptRuntime.ByteClass, -128.0d, 127.0d)));
        }
    }

    private static double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            return ScriptRuntime.toNumber((String) value);
        }
        if (!(value instanceof Scriptable)) {
            Method meth;
            try {
                meth = value.getClass().getMethod("doubleValue", (Class[]) null);
            } catch (NoSuchMethodException e) {
                meth = null;
            } catch (SecurityException e2) {
                meth = null;
            }
            if (meth != null) {
                try {
                    return ((Number) meth.invoke(value, (Object[]) null)).doubleValue();
                } catch (IllegalAccessException e3) {
                    reportConversionError(value, Double.TYPE);
                } catch (InvocationTargetException e4) {
                    reportConversionError(value, Double.TYPE);
                }
            }
            return ScriptRuntime.toNumber(value.toString());
        } else if (value instanceof Wrapper) {
            return toDouble(((Wrapper) value).unwrap());
        } else {
            return ScriptRuntime.toNumber(value);
        }
    }

    private static long toInteger(Object value, Class<?> type, double min, double max) {
        double d = toDouble(value);
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            reportConversionError(ScriptRuntime.toString(value), type);
        }
        if (d > 0.0d) {
            d = Math.floor(d);
        } else {
            d = Math.ceil(d);
        }
        if (d < min || d > max) {
            reportConversionError(ScriptRuntime.toString(value), type);
        }
        return (long) d;
    }

    static void reportConversionError(Object value, Class<?> type) {
        throw Context.reportRuntimeError2("msg.conversion.not.allowed", String.valueOf(value), JavaMembers.javaSignature(type));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeBoolean(this.isAdapter);
        if (!this.isAdapter) {
            out.writeObject(this.javaObject);
        } else if (adapter_writeAdapterObject == null) {
            throw new IOException();
        } else {
            try {
                adapter_writeAdapterObject.invoke(null, new Object[]{this.javaObject, out});
            } catch (Exception e) {
                throw new IOException();
            }
        }
        if (this.staticType != null) {
            out.writeObject(this.staticType.getClass().getName());
        } else {
            out.writeObject(null);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.isAdapter = in.readBoolean();
        if (!this.isAdapter) {
            this.javaObject = in.readObject();
        } else if (adapter_readAdapterObject == null) {
            throw new ClassNotFoundException();
        } else {
            try {
                this.javaObject = adapter_readAdapterObject.invoke(null, new Object[]{this, in});
            } catch (Exception e) {
                throw new IOException();
            }
        }
        String className = (String) in.readObject();
        if (className != null) {
            this.staticType = Class.forName(className);
        } else {
            this.staticType = null;
        }
        initMembers();
    }

    static {
        Class<?>[] sig2 = new Class[2];
        Class<?> cl = Kit.classOrNull("org.mozilla.javascript.JavaAdapter");
        if (cl != null) {
            try {
                sig2[0] = ScriptRuntime.ObjectClass;
                sig2[1] = Kit.classOrNull("java.io.ObjectOutputStream");
                adapter_writeAdapterObject = cl.getMethod("writeAdapterObject", sig2);
                sig2[0] = ScriptRuntime.ScriptableClass;
                sig2[1] = Kit.classOrNull("java.io.ObjectInputStream");
                adapter_readAdapterObject = cl.getMethod("readAdapterObject", sig2);
            } catch (NoSuchMethodException e) {
                adapter_writeAdapterObject = null;
                adapter_readAdapterObject = null;
            }
        }
    }
}
