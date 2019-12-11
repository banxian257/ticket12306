package com.wza.module.service;

import com.wza.module.entity.EmailInfo;
import com.wza.module.entity.TicketConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ConfigService {
    @Value("${ticket.date}")
    private String date;
    @Value("${ticket.departure}")
    private String departure;
    @Value("${ticket.arrival}")
    private String arrival;
    @Value("${ticket.user_name}")
    private String userNames;
    @Value("${ticket.train_number}")
    private String trainNumbers;
    @Value("${ticket.seats}")
    public String seats;
    @Value("${to_email}")
    public String email;
    @Value("${max_pool_count}")
    public Integer maxPoolCount;
    public void init() {
        TicketConfig ticketConfig = new TicketConfig();
        ticketConfig.setDate(date);
        ticketConfig.setDeparture(departure);
        ticketConfig.setArrival(seats);
        ticketConfig.setUserNames(userNames);
        ticketConfig.setTrainNumbers(trainNumbers);
        ticketConfig.setSeats(seats);
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setSubject("半仙抢票系统通知");
        emailInfo.setToAddress(email);
        ticketConfig.setEmailInfo(emailInfo);
        ExecutorService service =
                Executors.newFixedThreadPool(maxPoolCount);
        while (true) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        new BuyTickets().QueryTicket(ticketConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
