package gargoyle.xenox.util.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Convert {
    private static final Map<Class<?>, Function<String, ?>> converters = new HashMap<>();

    static {
        converters.put(String.class, (Function<String, String>) s -> s);
        converters.put(Integer.class, (Function<String, Integer>) Integer::valueOf);
        converters.put(Boolean.class, (Function<String, Boolean>) Boolean::valueOf);
        converters.put(Double.class, (Function<String, Double>) Double::valueOf);
        converters.put(Long.class, (Function<String, Long>) Long::valueOf);
    }

    private Convert() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(String p, Class<T> type, T defValue) {
        if (p == null) {
            return null;
        }
        if (converters.containsKey(type)) {
            return (T) converters.get(type).apply(p);
        } else {
            return defValue;
        }
    }
}
