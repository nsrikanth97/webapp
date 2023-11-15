package edu.neu.csye6225.controller;


import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import edu.neu.csye6225.dto.MailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sendgrid")
@Slf4j
public class SendGridController {

    @Value("${SG_API_KEY}")
    String SENDGRID_API_KEY;

    @Value("${TEMPLATE_ID}")
    String TEMPLATE_ID;



    @PostMapping("/sendmail")
    public void sendSimpleMessage(@RequestBody MailDetails mailDetails) {
        Email from = new Email(mailDetails.getFrom());
        String subject = mailDetails.getSubject();
        Email to = new Email(mailDetails.getTo());
        Content content = new Content("text/plain", mailDetails.getText());
        Mail mail = new Mail(from, subject, to, content);
        mail.setTemplateId(TEMPLATE_ID);
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(com.sendgrid.Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response =sg.api(request);
            log.info("Mail sent successfully");
            log.debug("Mail response status code: " + response.getStatusCode());
            log.debug("Mail response body: " + response.getBody());
            log.debug("Mail response headers: " + response.getHeaders());
        } catch (Exception ex) {
            log.error("Error sending mail: " + ex.getMessage());
            ex.printStackTrace();
        }


    }


}
