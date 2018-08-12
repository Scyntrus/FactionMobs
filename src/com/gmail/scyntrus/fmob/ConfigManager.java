package com.gmail.scyntrus.fmob;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class ConfigManager {
    private interface ConfigFunction {
        Object get(FileConfiguration config, String path, Object defaultValue);
    }
    private static final HashMap<Class<?>, ConfigFunction> GET_TYPE_FUNCTIONS = new HashMap<>();
    static {
        GET_TYPE_FUNCTIONS.put(Integer.class, (c, p, d) -> c.getInt(p, (Integer) d));
        GET_TYPE_FUNCTIONS.put(int.class, (c, p, d) -> c.getInt(p, (int) d));
        GET_TYPE_FUNCTIONS.put(String.class, (c, p, d) -> c.getString(p, (String) d));
        GET_TYPE_FUNCTIONS.put(Boolean.class, (c, p, d) -> c.getBoolean(p, (Boolean) d));
        GET_TYPE_FUNCTIONS.put(boolean.class, (c, p, d) -> c.getBoolean(p, (boolean) d));
        GET_TYPE_FUNCTIONS.put(Double.class, (c, p, d) -> c.getDouble(p, (Double) d));
        GET_TYPE_FUNCTIONS.put(double.class, (c, p, d) -> c.getDouble(p, (double) d));
        GET_TYPE_FUNCTIONS.put(Float.class, (c, p, d) -> (float) c.getDouble(p, (Double) d));
        GET_TYPE_FUNCTIONS.put(float.class, (c, p, d) -> (float) c.getDouble(p, (float) d));
        GET_TYPE_FUNCTIONS.put(Material.class, (c, p, d) -> {
            String name = c.getString(p, "").trim();
            if (!name.isEmpty()) {
                return Material.matchMaterial(name);
            }
            return d;
        });
    }

    private FileConfiguration config;
    public ConfigManager(FileConfiguration config) {
        this.config = config;
    }

    public void populateOptions(Object obj) {
        populateOptions(obj.getClass(), obj);
    }

    public void populateOptions(Class clazz) {
        populateOptions(clazz, null);
    }

    private void populateOptions(Class clazz, Object obj) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Option.class)) {
                try {
                    if (obj == null && !Modifier.isStatic(f.getModifiers())) continue;
                    if (Modifier.isPrivate(f.getModifiers())) {
                        f.setAccessible(true);
                    }
                    ConfigFunction getter = GET_TYPE_FUNCTIONS.get(f.getType());
                    if (getter == null) {
                        ErrorManager.handleError("Type not supported by ConfigManager: " + f.getType().getName());
                        continue;
                    }
                    Option opt = f.getAnnotation(Option.class);
                    f.set(obj, getter.get(config, opt.key(), f.get(obj)));
                    if (opt.min() > Long.MIN_VALUE) {
                        if (!(f.get(obj) instanceof Number)) {
                            ErrorManager.handleError("Option has min but is not number: " + f.getName());
                            continue;
                        }
                        Number val = (Number) f.get(obj);
                        if (val.doubleValue() < opt.min()) {
                            f.set(obj, opt.min());
                        }
                    }
                } catch (IllegalAccessException e) {
                    ErrorManager.handleError(e);
                }
            }
        }
    }

}
