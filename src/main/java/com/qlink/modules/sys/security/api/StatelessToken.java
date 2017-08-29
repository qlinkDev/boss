package main.java.com.qlink.modules.sys.security.api;

import org.apache.shiro.authc.AuthenticationToken;

import net.sf.json.JSONObject;

/**
 * StatelessToken   
 * <p>用户身份即用户名；凭证即客户端传入的消息摘要。
 * @author liaowu
 *
 */
public class StatelessToken implements AuthenticationToken {

    private String appId;
    private JSONObject params;
    private String clientSign;

    public StatelessToken(String appId,  JSONObject params, String clientSign) {
    	this.appId = appId;
        this.params = params;
        this.clientSign = clientSign;
    }

    public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public JSONObject getParams() {
        return params;
    }

    public void setParams( JSONObject params) {
        this.params = params;
    }

    public String getClientSign() {
        return clientSign;
    }

    public void setClientSign(String clientSign) {
        this.clientSign = clientSign;
    }

    @Override
    public Object getPrincipal() {
       return appId;
    }

    @Override
    public Object getCredentials() {
        return clientSign;
    }
}
