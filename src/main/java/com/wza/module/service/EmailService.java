package com.wza.module.service;

import com.wza.module.entity.EmailInfo;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    //邮件的发送者
    @Value("${spring.mail.username}")
    private String from;

    //注入MailSender
    @Resource
    private JavaMailSender javaMailSender;

    //发送邮件的模板引擎
    @Autowired
    private FreeMarkerConfigurer configurer;

    /**
     * 发送邮件
     *
     * @throws Exception
     */
    public void sendMessageMail(EmailInfo emailInfo) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        //获取要发送的内容
        Template template = configurer.getConfiguration().getTemplate(emailInfo.getTemplateName());
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailInfo.getParams());
        //发送者
        helper.setFrom(from);
        //收件
        InternetAddress.parse("598466044@qq.com");
        //主题
        helper.setSubject(emailInfo.getSubject());

        //收件人
        helper.setTo(emailInfo.getToAddress().split(";"));
        //抄送人
        if (StringUtils.isNotBlank(emailInfo.getCopyToAddress())) {
            helper.setCc(emailInfo.getCopyToAddress().split(";"));
        }
        helper.setText(html, true);
        javaMailSender.send(mimeMessage);
    }


}
