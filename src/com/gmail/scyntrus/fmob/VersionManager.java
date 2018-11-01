package com.gmail.scyntrus.fmob;

public class VersionManager {
    
    public static class VersionException extends Exception {
        private static final long serialVersionUID = 1L;
        public VersionException(String message) {
            super(message);
        }
    }
    
    public static void checkVersion() throws VersionException {
        if (Package.getPackage("org.bukkit.craftbukkit.v1_13_R2") != null) {
            return;
        }
        
        try {
            Class<?> tmpMcpcClass = Class.forName("za.co.mcportcentral.entity.CraftCustomEntity");
            if (tmpMcpcClass != null) {
                if (tmpMcpcClass.getResourceAsStream("/mappings/v1_13_R2/cb2numpkg.srg") != null) {
                    ErrorManager.handleError("[FactionMobs] MCPC detected. MCPC compatibility is experimental.");
                } else {
                    ErrorManager.handleError("[FactionMobs] WARNING: INCOMPATIBLE VERSION OF MCPC DETECTED, FactionMobs will probably not work.");
                }
                return;
            }
        } catch (ClassNotFoundException ignored) {
        }

        String foundVersion = "unkown";
        for (Package p : Package.getPackages()) {
            if (p.getName().matches("org\\.bukkit\\.craftbukkit\\.v[\\dR_]+\\z")) {
                foundVersion = p.getName().substring(23);
                break;
            }
        }

        throw new VersionException("You are running an unsupported version of CraftBukkit (Requires: v1_13_R2, Found:"+foundVersion+"). Please download a newer version. FactionMobs will not be enabled.");
    }
}
