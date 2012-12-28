package com.sample.web.service.impl;

import com.sample.web.service.MailSenderService;
import com.sample.web.service.data.EmailServiceTokenModel;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @version 1.0
 * @author: Iain Porter iain.porter@incept5.com
 * @since 13/09/2012
 */
@Service("mailSenderService")
public class MailSenderServiceImpl implements MailSenderService {

    private static final String EMAIL_FROM_ADDRESS = "XXXXXXXXXXXXXX";

    private static Logger LOG = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    private final JavaMailSender mailSender;
    private final VelocityEngine velocityEngine;


    @Autowired
    public MailSenderServiceImpl(JavaMailSender mailSender, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
    }


    public EmailServiceTokenModel sendVerificationEmail(final EmailServiceTokenModel emailVerificationModel) {
        Map<String, String> resources = new HashMap<String, String>();
          return sendVerificationEmail(emailVerificationModel, "[XXXXXXXXXXXXX] Please verify your email Address",
                  "META-INF/velocity/VerifyEmail.vm", resources);
    }

    public EmailServiceTokenModel sendRegistrationEmail(final EmailServiceTokenModel emailVerificationModel) {
        Map<String, String> resources = new HashMap<String, String>();
          return sendVerificationEmail(emailVerificationModel, "Welcome To XXXXXXXX",
                  "META-INF/velocity/RegistrationEmail.vm", resources);
    }

    public EmailServiceTokenModel sendLostPasswordEmail(final EmailServiceTokenModel emailServiceTokenModel) {
        Map<String, String> resources = new HashMap<String, String>();
         return sendVerificationEmail(emailServiceTokenModel, "[XXXXXXXXXX] Reset Password ",
                 "META-INF/velocity/LostPasswordEmail.vm", resources);
    }


    private void addInlineResource(MimeMessageHelper messageHelper, String resourcePath, String resourceIdentifier) throws MessagingException {
        Resource resource = new ClassPathResource(resourcePath);
        messageHelper.addInline(resourceIdentifier, resource);
    }

    private EmailServiceTokenModel sendVerificationEmail(final EmailServiceTokenModel emailVerificationModel, final String emailSubject,
                                                         final String velocityModel, final Map<String, String> resources) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
                messageHelper.setTo(emailVerificationModel.getEmailAddress());
                messageHelper.setFrom(EMAIL_FROM_ADDRESS);
                messageHelper.setSubject(emailSubject);
                Map model = new HashMap();
                model.put("model", emailVerificationModel);
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityModel, model);
                messageHelper.setText(new String(text.getBytes(), "UTF-8"), true);
                      for(String resourceIdentifier: resources.keySet()) {
                   addInlineResource(messageHelper, resources.get(resourceIdentifier), resourceIdentifier);
                }
            }
        };
        LOG.debug("Sending Verification Email to : {}", emailVerificationModel.getEmailAddress());
        this.mailSender.send(preparator);
        return emailVerificationModel;
    }
}
