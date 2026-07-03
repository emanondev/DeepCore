package emanondev.core.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.*;

public class GsonUtil {
    public static final String SERIALIZED_INT_KEY = ConfigurationSerialization.SERIALIZED_TYPE_KEY + "I";
    public static final String SERIALIZED_FLOAT_KEY = ConfigurationSerialization.SERIALIZED_TYPE_KEY + "F";

    private static final Gson gson = new GsonBuilder()//.setPrettyPrinting()
            .disableHtmlEscaping().create();

    public static String toJson(ConfigurationSerializable ser) {
        return gson.toJson(serialize(ser));
    }

    public static ConfigurationSerializable fromJson(String json) {
        return deserialize(gson.fromJson(json, Map.class));
    }

    public static <T extends ConfigurationSerializable> T fromJson(String json, Class<T> clazz) {
        return (T) deserialize(gson.fromJson(json, Map.class));
    }

    public static Map<String, Object> serialize(ConfigurationSerializable serializable) {
        return (Map<String, Object>) fix(serializable);
    }

    public static <T extends ConfigurationSerializable> T deserialize(Map<String, Object> map, Class<T> clazz) {
        return (T) deserialize(map);
    }

    public static ConfigurationSerializable deserialize(Map<String, Object> map) {
        return (ConfigurationSerializable) defix(map);
    }

    private static Object fix(Object o) {
        if (o == null)
            return null;
        return switch (o) {
            case Integer i -> Collections.singletonMap(SERIALIZED_INT_KEY, o);
            case Short i -> Collections.singletonMap(SERIALIZED_INT_KEY, o);
            case Long i -> Collections.singletonMap(SERIALIZED_INT_KEY, o);
            case Float aFloat -> Collections.singletonMap(SERIALIZED_FLOAT_KEY, o);

            case ConfigurationSerializable s -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(s.getClass()));
                map.putAll((Map<String, Object>) fix(s.serialize()));
                yield map;
            }
            case Map subMap -> {
                boolean hasWeirdValues = false;
                for (Object v : subMap.values()) {
                    if (isWeird(v)) {
                        hasWeirdValues = true;
                        break;
                    }
                }
                if (!hasWeirdValues) {
                    yield o;
                }
                Map map = new LinkedHashMap();
                subMap.forEach((k, v) -> map.put(k, fix(v)));
                yield map;
            }
            case Collection collection -> {
                boolean hasWeirdValues = false;
                for (Object v : collection) {
                    if (isWeird(v)) {
                        hasWeirdValues = true;
                        break;
                    }
                }
                if (!hasWeirdValues) {
                    yield o;
                }
                List list = new ArrayList();
                collection.forEach((v) -> list.add(fix(v)));
                yield list;
            }
            default -> o;//hopefully a double or a string
        };
    }

    private static boolean isWeird(Object o) {
        return o instanceof Integer || o instanceof Short || o instanceof Long || o instanceof ConfigurationSerializable
                || o instanceof Map || o instanceof Collection;
    }

    private static Object defix(Map<String, Object> map) {
        if (map == null)
            return null;

        //boolean hasWeirdValues = false;
        for (String key : map.keySet()) {
            Object o = map.get(key);
            if (o instanceof Map subMap) {
                map.put(key, defix(subMap));
            }
            if (o instanceof Collection collection) {

                List list = new ArrayList<>();
                collection.forEach((v) -> {
                    if (v instanceof Map subMap)
                        list.add(defix(subMap));
                    else
                        list.add(v);
                });
                map.put(key, list);
            }
        }
        if (map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY))
            return ConfigurationSerialization.deserializeObject(map);
        if (map.containsKey(SERIALIZED_INT_KEY))
            return ((Number) map.get(SERIALIZED_INT_KEY)).intValue();
        if (map.containsKey(SERIALIZED_FLOAT_KEY))
            return ((Number) map.get(SERIALIZED_FLOAT_KEY)).floatValue();
        return map;
    }
}
