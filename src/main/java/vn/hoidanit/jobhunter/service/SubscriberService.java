package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hoidanit.jobhunter.DTO.ResEmailJob;
import vn.hoidanit.jobhunter.model.Job;
import vn.hoidanit.jobhunter.model.Skill;
import vn.hoidanit.jobhunter.model.Subcriber;
import vn.hoidanit.jobhunter.reponsitory.JobRepository;
import vn.hoidanit.jobhunter.reponsitory.SkillRepository;
import vn.hoidanit.jobhunter.reponsitory.SubscriberRepository;

@Service

public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean existsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subcriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public Subcriber createSubscriber(Subcriber subcriber) {
        if (subcriber.getSkills() != null) {
            List<Long> reqSkill = subcriber.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> currentSkill = this.skillRepository.findByIdIn(reqSkill);
            subcriber.setSkills(currentSkill);
        }
        return this.subscriberRepository.save(subcriber);
    }

    public Subcriber getSubcriberById(long id) {
        Optional<Subcriber> currentSubscriber = this.subscriberRepository.findById(id);
        if (currentSubscriber.isPresent()) {
            return currentSubscriber.get();
        }
        return null;
    }

    public Subcriber updateSubscriber(Subcriber subcriberDB, Subcriber subcriberReq) {

        if (subcriberReq.getSkills() != null) {
            List<Long> reqSkill = subcriberReq.getSkills().stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> currentSkill = this.skillRepository.findByIdIn(reqSkill);
            subcriberDB.setSkills(currentSkill);
        }
        return this.subscriberRepository.save(subcriberDB);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    @Async
    @Transactional
    public void sendSubscribersEmailJobs() {
        List<Subcriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subcriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }
    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron(){

    // }
}
