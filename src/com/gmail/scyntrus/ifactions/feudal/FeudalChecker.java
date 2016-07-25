package com.gmail.scyntrus.ifactions.feudal;

import com.gmail.scyntrus.fmob.FactionMob;
import com.gmail.scyntrus.fmob.FactionMobs;
import java.util.HashSet;
import java.util.Iterator;
import us.forseth11.feudal.core.Feudal;
import us.forseth11.feudal.kingdoms.Kingdom;

public class FeudalChecker implements Runnable {

    public FeudalChecker() {
    }

    @Override
    public void run() {
        HashSet<String> names = new HashSet<String>();
        for (Kingdom k : Feudal.getKingdoms()) {
            names.add(k.getName());
        }
        for (Iterator<FactionMob> it = FactionMobs.mobList.iterator(); it.hasNext();) {
            FactionMob fmob = it.next();
            if (!names.contains(fmob.getFactionName())) {
                fmob.updateMob();
                if (!names.contains(fmob.getFactionName()))
                    fmob.forceDie();
                if (!fmob.getEntity().isAlive())
                    it.remove();
            }
        }
    }
}
