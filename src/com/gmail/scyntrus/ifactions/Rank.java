package com.gmail.scyntrus.ifactions;

public enum Rank {

    LEADER(5),
    OFFICER(4),
    MEMBER(3),
    RECRUIT(2),
    UNKNOWN(0);

    private int rankVal;

    Rank(int rankVal) {
        this.rankVal = rankVal;
    }

    public boolean isAtLeast(Rank rank) {
        return rank.rankVal <= this.rankVal;
    }

    public static Rank getByName(String name) {
        name = name.toUpperCase();
        switch (name) {
            case "NORMAL":
                return Rank.MEMBER;
            case "MODERATOR":
                return Rank.OFFICER;
            case "ADMIN":
                return Rank.LEADER;
        }
        try {
            return Rank.valueOf(name);
        } catch (IllegalArgumentException  e) {
            return Rank.MEMBER;
        }
    }
}
