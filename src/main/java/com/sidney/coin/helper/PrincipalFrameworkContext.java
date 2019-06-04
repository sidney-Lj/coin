package com.sidney.coin.helper;

public class PrincipalFrameworkContext {

    public static final String USER_ID_KEY = "ctx-userid";
    public static final String SESSIONID_KEY = "ctx-sessionid";
    private static final ThreadLocal<PrincipalFrameworkContext> LOCAL = ThreadLocal.withInitial(PrincipalFrameworkContext::new);

    private String userId = "";
    private String sessionId = "";
    private String userIp = "";

    private PrincipalFrameworkContext() {

    }

    public static PrincipalFrameworkContext getContext() {
        return LOCAL.get();
    }

    public String getUserId() {
        return userId;
    }

    public PrincipalFrameworkContext setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public PrincipalFrameworkContext setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getUserIp() {
        return userIp;
    }

    public PrincipalFrameworkContext setUserIp(String userIp) {
        this.userIp = userIp;
        return this;
    }
    public void clear(){
        this.setSessionId("").setUserId("").setUserIp("");
    }
}
