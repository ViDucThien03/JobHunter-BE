package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResCreateJobDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Company;
import vn.hoidanit.jobhunter.model.Job;
import vn.hoidanit.jobhunter.model.Skill;
import vn.hoidanit.jobhunter.reponsitory.CompanyRepository;
import vn.hoidanit.jobhunter.reponsitory.JobRepository;
import vn.hoidanit.jobhunter.reponsitory.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO createJob(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkill = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> skill = this.skillRepository.findByIdIn(reqSkill);
            job.setSkills(skill);
        }
        if (job.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(job.getCompany().getId());
            if (company.isPresent()) {
                job.setCompany(company.get());
            }
        }
        Job currentJob = this.jobRepository.save(job);
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreateBy());
        res.setUpdatedAt(currentJob.getUpdatedAt());
        res.setUpdateBy(currentJob.getUpdateBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            res.setSkill(skills);
        }
        return res;
    }

    public ResCreateJobDTO updateJob(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkill = job.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> skill = this.skillRepository.findByIdIn(reqSkill);
            job.setSkills(skill);
        }
        if (job.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(job.getCompany().getId());
            if (company.isPresent()) {
                job.setCompany(company.get());
            }
        }
        Job currentJob = this.jobRepository.save(job);
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreateBy());
        res.setUpdatedAt(currentJob.getUpdatedAt());
        res.setUpdateBy(currentJob.getUpdateBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            res.setSkill(skills);
        }
        return res;
    }

    public Job getJobById(long id) {
        Optional<Job> job = this.jobRepository.findById(id);
        if (job.isPresent()) {
            return job.get();
        }
        return null;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> specification, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageJob.getContent());
        return rs;
    }
}
