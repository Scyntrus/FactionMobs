package com.gmail.scyntrus.fmob;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_11_R1.RegistryMaterials;
import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.MinecraftKey;

public class CustomEntityRegistry extends RegistryMaterials<MinecraftKey, Class<? extends Entity>> {
    protected final BiMap<MinecraftKey, Class<? extends Entity>> customEntities = HashBiMap.create();
    protected final BiMap<Class<? extends Entity>, MinecraftKey> customEntityClasses = customEntities.inverse();
    protected final Map<Class<? extends Entity>, Integer> customEntityIds = new HashMap<Class<? extends Entity>, Integer>();

    private RegistryMaterials<MinecraftKey, Class<? extends Entity>> wrapped;

    private static boolean initialized = false;
    private static CustomEntityRegistry singleton = null;

    public static boolean init() {
        if (initialized) {
            return true;
        }

        if (singleton == null) {
            singleton = new CustomEntityRegistry(EntityTypes.b);
        }

        try {
            Field entityTypes_RegistryMaterials = EntityTypes.class.getDeclaredField("b"); //TODO: Update name on version change (RegistryMaterials)
            entityTypes_RegistryMaterials.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(entityTypes_RegistryMaterials, entityTypes_RegistryMaterials.getModifiers() & ~Modifier.FINAL);

            entityTypes_RegistryMaterials.set(null, singleton);
            initialized = true;
            return true;
        } catch (Exception e1) {
            ErrorManager.handleError("Failed to reflect entity registration object.");
            ErrorManager.handleError(e1);
            return false;
        }
    }

    public static CustomEntityRegistry instance() {
        return singleton;
    }

    private CustomEntityRegistry(RegistryMaterials<MinecraftKey, Class<? extends Entity>> original) {
        wrapped = original;
    }

    public void addCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        MinecraftKey localMinecraftKey = new MinecraftKey(entityName);
        customEntities.put(localMinecraftKey, entityClass);
        customEntityIds.put(entityClass, entityId);
    }

    @Override
    public Class<? extends Entity> get(MinecraftKey key) {
        if (customEntities.containsKey(key)) {
            return customEntities.get(key);
        }
        return wrapped.get(key);
    }

    @Override
    public int a(Class<? extends Entity> key) { //TODO: Update name on version change (getId)
        if (customEntityIds.containsKey(key)) {
            return customEntityIds.get(key);
        }
        return this.wrapped.a(key);
    }

    @Override
    public MinecraftKey b(Class<? extends Entity> value) { //TODO: Update name on version change (getKey)
        if (customEntityClasses.containsKey(value)) {
            return customEntityClasses.get(value);
        }
        return wrapped.b(value);
    }
}
