package main.java.com.qlink.modules.sys.security.api;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * Subject工厂  
 * <p>通过调用context.setSessionCreationEnabled(false)表示不创建会话；
 * <p>如果之后调用Subject.getSession()将抛出DisabledSessionException异常。
 * @author liaowu
 *
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        //不创建session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}
