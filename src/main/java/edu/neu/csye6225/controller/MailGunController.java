package edu.neu.csye6225.controller;


import edu.neu.csye6225.dto.MailDetails;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("v1/mailgun")
public class MailGunController {

    @Value("${YOUR_DOMAIN_NAME}")
    String YOUR_DOMAIN_NAME;

    @Value("${API_KEY}")
    String API_KEY;

    @PostMapping("/mail")
    public JsonNode sendSimpleMessage(MailDetails mailGunDetails) throws UnirestException {
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
			.basicAuth("api", API_KEY)
                .queryString("from", mailGunDetails.getFrom())
                .queryString("to", mailGunDetails.getTo())
                .queryString("subject", mailGunDetails.getSubject())
                .queryString("text", mailGunDetails.getText())
                .asJson();
        return request.getBody();
    }
}
