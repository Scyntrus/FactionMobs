package com.gmail.scyntrus.ifactions;

public enum FRank {

    LEADER(5),
    OFFICER(4),
    MEMBER(3),
    RECRUIT(2);
    
    private int rankVal; 
    
    private FRank(int rankVal) {
        this.rankVal = rankVal;
    }
    
    public boolean isAtLeast(FRank rank) {
        return rank.rankVal <= this.rankVal;
    }
    
    public static FRank getByName(String name) {
        name = name.toUpperCase();
        if (name.equals("NORMAL"))
            name = "MEMBER";
        else if (name.equals("MODERATOR"))
            name = "OFFICER";
        else if (name.equals("ADMIN"))
            name = "LEADER";
        try {
            return FRank.valueOf(name);
        } catch (IllegalArgumentException  e) {
            return FRank.MEMBER;
        }
    }
}
