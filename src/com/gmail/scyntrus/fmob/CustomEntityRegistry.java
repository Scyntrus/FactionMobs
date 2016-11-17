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

public class CustomEntityRegistry extends RegistryMaterials {

    private static CustomEntityRegistry instance = null;

    private final BiMap<MinecraftKey, Class<? extends Entity>> customEntities = HashBiMap.create();
    private final BiMap<Class<? extends Entity>, MinecraftKey> customEntityClasses = customEntities.inverse();
    private final Map<Class<? extends Entity>, Integer> customEntityIds = new HashMap<Class<? extends Entity>, Integer>();

    private final RegistryMaterials wrapped;

    private CustomEntityRegistry(RegistryMaterials<MinecraftKey, Class<? extends Entity>> original) {
        this.wrapped = original;
    }

    public static CustomEntityRegistry getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new CustomEntityRegistry(EntityTypes.b);

        try {
            //TODO: Update name on version change (RegistryMaterials)
            Field registryMaterialsField = EntityTypes.class.getDeclaredField("b");
            registryMaterialsField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

            registryMaterialsField.set(null, instance);
        } catch (Exception e) {
            instance = null;

            throw new RuntimeException("Unable to override the old entity RegistryMaterials", e);
        }

        return instance;
    }

    public static void addCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        CustomEntityRegistry instance = getInstance();

        if(instance == null) {
            throw new IllegalStateException("Unable to obtain the CustomEntityRegistry instance");
        }

        instance.putCustomEntity(entityId, entityName, entityClass);
    }

    public void putCustomEntity(int entityId, String entityName, Class<? extends Entity> entityClass) {
        MinecraftKey minecraftKey = new MinecraftKey(entityName);

        this.customEntities.put(minecraftKey, entityClass);
        this.customEntityIds.put(entityClass, entityId);
    }

    @Override
    public Class<? extends Entity> get(Object key) {
        if (customEntities.containsKey(key)) {
            return customEntities.get(key);
        }

        return (Class<? extends Entity>) wrapped.get(key);
    }

    @Override
    public int a(Object key) { //TODO: Update name on version change (getId)
        if (customEntityIds.containsKey(key)) {
            return customEntityIds.get(key);
        }
        return this.wrapped.a(key);
    }

    @Override
    public MinecraftKey b(Object value) { //TODO: Update name on version change (getKey)
        if (customEntityClasses.containsKey(value)) {
            return customEntityClasses.get(value);
        }

        return (MinecraftKey) wrapped.b(value);
    }
}
