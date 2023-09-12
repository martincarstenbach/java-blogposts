package de.theplayground.demo01;

public class SessionHitCounter {
    private String sessionID;
    private int hitCount;

    public SessionHitCounter(String sessionID, int hitCount) {
        this.sessionID = sessionID;
        this.hitCount = hitCount;
    }

    public SessionHitCounter() {
        super();
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }
}
