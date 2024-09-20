package com.project.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;
    
//    @Value("${twilio.whatsapp.sender.number}")
//    private String fromWhatsAppNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        if (StringUtils.isBlank(toPhoneNumber) || StringUtils.isBlank(messageBody)) {
            throw new IllegalArgumentException("To phone number and message body cannot be blank");
        }
        toPhoneNumber = "+977" + toPhoneNumber;
        try {
            Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromPhoneNumber), messageBody).create();
        } catch (Exception e) {
            System.err.println("Error sending SMS to " + toPhoneNumber + ": " + e.getMessage());
        }
    }
}

