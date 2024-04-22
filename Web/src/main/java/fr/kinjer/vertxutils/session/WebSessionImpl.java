package fr.kinjer.vertxutils.session;

import java.util.HashMap;
import java.util.Map;

public class WebSessionImpl implements IWebSession {

    private final String session;
    private long expirationTime;
    private final long resetTime;
    private final Map<String, Object> data = new HashMap<>();

    public WebSessionImpl(String session, long expirationTime) {
        this.session = session;
        this.expirationTime = System.currentTimeMillis() + expirationTime;
        this.resetTime = expirationTime;
    }

    public String getSession() {
        return session;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public long getResetTime() {
        return resetTime;
    }

    public WebSessionImpl resetTime() {
        this.expirationTime = System.currentTimeMillis() + this.resetTime;
        return this;
    }

    public Map<String, Object> getData() {
        if(!this.isExpired()) {
            this.resetTime();
        }
        return data;
    }

    public void addData(String key, Object value) {
        this.getData().put(key, value);
    }

    public void removeData(String key) {
        this.data.remove(key);
    }

    public Object getData(String key) {
        return this.getData().getOrDefault(key, null);
    }

    public String getDataAsString(String key) {
        return (String) this.getData(key);
    }

    public int getDataAsInt(String key) {
        return (int) this.getData(key);
    }

    public long getDataAsLong(String key) {
        return (long) this.getData(key);
    }

    public float getDataAsFloat(String key) {
        return (float) this.getData(key);
    }

    public double getDataAsDouble(String key) {
        return (double) this.getData(key);
    }

    public boolean getDataAsBoolean(String key) {
        return (boolean) this.getData(key);
    }

    public boolean hasData(String key) {
        return this.getData().containsKey(key);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expirationTime;
    }

    @Override
    public String toString() {
        return "WebSession{" +
                "session='" + session + '\'' +
                ", expirationTime=" + expirationTime +
                ", data=" + data +
                '}';
    }
}
