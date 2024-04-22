package fr.kinjer.vertxutils.session;

import java.util.Map;

public interface IWebSession {

    String getSession();

    long getExpirationTime();

    long getResetTime();

    WebSessionImpl resetTime();

    Map<String, Object> getData();

    void addData(String key, Object value);

    void removeData(String key);

    Object getData(String key);

    String getDataAsString(String key);

    int getDataAsInt(String key);

    long getDataAsLong(String key);

    float getDataAsFloat(String key);

    double getDataAsDouble(String key);

    boolean getDataAsBoolean(String key);

    boolean hasData(String key);

    boolean isExpired();

}
