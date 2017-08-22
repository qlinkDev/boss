package com.uu.modules.sys.security.api;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uu.common.config.Global;
import com.uu.common.utils.DigestUtils;
import com.uu.common.utils.HmacSHA256Utils;
import com.uu.common.utils.StringUtils;
import com.uu.modules.sys.entity.YYKeyStore;
import com.uu.modules.sys.service.YYKeyStoreService;

/**
 * 用于api认证的Realm
 * @author liaowu
 *
 */
@Service
public class StatelessRealm extends AuthorizingRealm {
	public static Logger logger = LoggerFactory.getLogger(AuthorizingRealm.class);
	@Autowired
	private YYKeyStoreService keyStoreService;
	
	@Override
    public boolean supports(AuthenticationToken token) {
        //仅支持StatelessToken类型的Token
        return token instanceof StatelessToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //根据用户名查找角色，请根据需求实现
        //String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo =  new SimpleAuthorizationInfo();
        authorizationInfo.addRole("admin");
        return authorizationInfo;
    }
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        StatelessToken statelessToken = (StatelessToken) token;
        String appId = statelessToken.getAppId();
        //在服务器端生成客户端参数消息摘要
        String serverSign = "";
        logger.info(":::ClientSign = "+statelessToken.getClientSign());
        logger.info(":::ServerSign = "+serverSign);
        YYKeyStore keyStore = keyStoreService.get(appId);
        if (keyStore == null) 
        	keyStore = keyStoreService.findKeyBySourceType(appId);
        if (Global.isDemoMode()) {
            serverSign = keyStore.getKeyValue();//演示模式直接用密码比较
		}else{
            if(null!=keyStore.getKeyType() && StringUtils.isNoneBlank(keyStore.getKeyType()) && "HmacSHA256".equals(keyStore.getKeyType())){
                serverSign = HmacSHA256Utils.digest(keyStore.getKeyValue(), statelessToken.getParams());
            }else{
                serverSign = DigestUtils.getSignature(statelessToken.getParams(),keyStore.getKeyValue(),"UTF-8");
            }
			//serverDigest = HmacSHA256Utils.digest(getKey(appId), statelessToken.getParams());
		}
        
        //然后进行客户端消息摘要和服务器端消息摘要的匹配
        return new SimpleAuthenticationInfo(
        		appId,
                serverSign,
                "statelessRealm");
    }
	
	public void setKeyStoreService(YYKeyStoreService keyStoreService) {
		this.keyStoreService = keyStoreService;
	}
}
