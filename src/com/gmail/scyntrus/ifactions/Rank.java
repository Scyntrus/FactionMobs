package com.gmail.scyntrus.ifactions;

public enum Rank {

    LEADER(5),
    OFFICER(4),
    MEMBER(3),
    RECRUIT(2);

    private int rankVal;

    Rank(int rankVal) {
        this.rankVal = rankVal;
    }

    public boolean isAtLeast(Rank rank) {
        return rank.rankVal <= this.rankVal;
    }

    public static Rank getByName(String name) {
        name = name.toUpperCase();
        if (name.equals("NORMAL"))
            name = "MEMBER";
        else if (name.equals("MODERATOR"))
            name = "OFFICER";
        else if (name.equals("ADMIN"))
            name = "LEADER";
        try {
            return Rank.valueOf(name);
        } catch (IllegalArgumentException  e) {
            return Rank.MEMBER;
        }
    }
}
