package main.java.com.qlink.modules.sys.security.api;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uu.modules.utils.ReturnCode;

import jodd.io.StreamUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * StatelessAuthcFilter
 * <p>类似于FormAuthenticationFilter，但是根据当前请求上下文信息每次请求时都要登录的认证过滤器。
 * <p>获取客户端传入的用户名、请求参数、签名，生成StatelessToken；然后交给相应的Realm进行认证。
 * @author liaowu
 *
 */
@Service
public class StatelessAuthcFilter extends AccessControlFilter {
	public static Logger logger = LoggerFactory.getLogger(StatelessAuthcFilter.class);
	public static final String PARAM_SIGN = "sign";
    public static final String PARAM_APPID = "appid";
    public static final String PARAM_TIMESTAMP = "timestamp";
    public static final String PARAM_NAME = "params";
    private JSONObject reqObj ;
    
	@Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    	//定义返回json
        JSONObject res = new JSONObject();
        res.put("msg", ReturnCode.ERR__1);
		res.put("code", "-1");
        try {        	
        	//1、客户端生成的消息摘要
        	if(!reqObj.containsKey(PARAM_SIGN)){
        		res.put("msg", ReturnCode.ERR_41011);
        		res.put("code", "41001");
        		throw new Exception();
        	}
        		
            String clientsSign = reqObj.getString(PARAM_SIGN);
            //2、客户端传入的用户身份
            if(!reqObj.containsKey(PARAM_APPID)){
        		res.put("msg", ReturnCode.ERR_41002);
        		res.put("code", "41002");
        		throw new Exception();
        	}
            String appid = reqObj.getString(PARAM_APPID);
            
            //校验是否带TIMESTAMP，否则不允许执行
            if(!reqObj.containsKey(PARAM_TIMESTAMP)){
        		res.put("msg", ReturnCode.ERR_41010);
        		res.put("code", "41010");
        		throw new Exception();
        	}
            //3、客户端请求的参数列表
            reqObj.remove(PARAM_SIGN);

            //4、生成无状态Token
            StatelessToken token = new StatelessToken(appid, reqObj, clientsSign);
            //5、委托给Realm进行登录
        	Subject subject = SecurityUtils.getSubject();  
        	subject.login(token);
        } catch (JSONException e) {
			res.put("code", "47001");
			res.put("msg", ReturnCode.ERR_47001);
			e.printStackTrace();
			onParseFail(response,res); 
			return false;
		} catch(AuthenticationException a){
			a.printStackTrace();
			res.put("code", "40036");
			res.put("msg", ReturnCode.ERR_40036);
			onVerifyFail(response,res); 
			return false;
		}catch (Exception e) {
            e.printStackTrace();
            //6、校验失败
            onVerifyFail(response,res); 
            return false;
        }
        return true;
    }

    //校验失败时默认返回401状态码
    private void onVerifyFail(ServletResponse response,JSONObject res) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        response.setContentType("application/json;charset=utf-8");  
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write(res.toString());
    }
    
    //解析失败时默认返回401状态码
    private void onParseFail(ServletResponse response,JSONObject res) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        response.setContentType("application/json;charset=utf-8");  
        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        httpResponse.getWriter().write(res.toString());
    }
    
    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException{
    	ServletRequest requestWrapper = null;  
        if(request instanceof HttpServletRequest) {  
        	HttpServletRequest req = (HttpServletRequest) request;  
            if(POST_METHOD.equalsIgnoreCase(req.getMethod())) {
            	//解决getInputStream、getReader只能调用一次的问题
            	//先把HttpServletRequest转成BodyReaderHttpServletRequestWrapper
            	requestWrapper = new BodyReaderHttpServletRequestWrapper(req);  
        		StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = null;
                InputStream inputStream = requestWrapper.getInputStream();
                if (inputStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                    char[] charBuffer = new char[128];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    // make an empty string since there is no payload
                    stringBuilder.append("");
                }
                logger.info("client post json data=" + stringBuilder.toString());
//				reqObj = JSONObject.fromObject("{\"timestamp\":\"13456789911\",\"appid\":\"APP\",\"token\":\"password\"}");
				//reqObj = JSONObject.fromObject(stringBuilder.toString());
				String params = URLDecoder.decode(stringBuilder.toString(), "UTF-8");
				reqObj = JSONObject.fromObject(params);
            }else{
            	reqObj = JSONObject.fromObject(request.getParameter("data"));
            	logger.info("client get json data=" + request.getParameter("data"));
            }
        }  
        if(null == requestWrapper) {  
            super.doFilterInternal(request, response,chain);  
        } else {  
        	super.doFilterInternal(requestWrapper, response,chain);  
        }  
    }
    
    /**
     * 包装ServletRequest，将流保存为byte[]
     * <p>先将RequestBody保存为一个byte数组，然后通过Servlet自带的HttpServletRequestWrapper类覆盖getReader()和getInputStream()方法，使流从保存的byte数组读取。
     * <p>然后再Filter中将ServletRequest替换为ServletRequestWrapper
     * @author liaowu
     *
     */
	public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private final byte[] body;
		public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			body = StreamUtil.readBytes(request.getReader());
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(getInputStream()));
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			final ByteArrayInputStream bais = new ByteArrayInputStream(body);
			return new ServletInputStream() {
				@Override
				public int read() throws IOException {
					return bais.read();
				}

				@Override
				public boolean isFinished() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean isReady() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setReadListener(ReadListener paramReadListener) {
					// TODO Auto-generated method stub
				}
			};
		}

	}

}
