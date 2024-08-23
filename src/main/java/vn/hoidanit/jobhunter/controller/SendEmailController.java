package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import vn.hoidanit.jobhunter.service.SubscriberService;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * SendEmailController
 */
@RestController
@RequestMapping("/api/v1")
public class SendEmailController {
   
    private final SubscriberService subscriberService;

    public SendEmailController (SubscriberService subscriberService) {
        
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    public String sendEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("vithienym19@gmail.com", "test send email",
        // "<h1>ViDucThien</h1>", false,
        // false);
        this.subscriberService.sendSubscribersEmailJobs();
        ;
        return "Oke";
    }

}