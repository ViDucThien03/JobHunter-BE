package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.Subcriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subcriber> createSubscriber(@Valid @RequestBody Subcriber subcriber)
            throws IdInvalidException {
        boolean isExist = this.subscriberService.existsByEmail(subcriber.getEmail());
        if (isExist == true) {
            throw new IdInvalidException("Email is already exist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.createSubscriber(subcriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subcriber> updateSubscriber(@Valid @RequestBody Subcriber subcriber)
            throws IdInvalidException {
        Subcriber currentSubcriber = this.subscriberService.getSubcriberById(subcriber.getId());
        if (currentSubcriber == null) {
            throw new IdInvalidException("Subscriber not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.subscriberService.updateSubscriber(currentSubcriber, subcriber));
    }

    @PostMapping("/subscribers/skills")
    public ResponseEntity<Subcriber> getSubscribersSkill() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
