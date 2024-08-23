package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoidanit.jobhunter.DTO.Meta;
import vn.hoidanit.jobhunter.DTO.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResGetResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.model.Job;
import vn.hoidanit.jobhunter.model.Resume;
import vn.hoidanit.jobhunter.model.User;
import vn.hoidanit.jobhunter.reponsitory.JobRepository;
import vn.hoidanit.jobhunter.reponsitory.ResumeRepository;
import vn.hoidanit.jobhunter.reponsitory.UserRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {
    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;

    public ResumeService(UserRepository userRepository, JobRepository jobRepository,
            ResumeRepository resumeRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
    }

    public Optional<Resume> getResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean isExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null) {
            return false;
        }
        Optional<User> user = this.userRepository.findById(resume.getUser().getId());
        if (user.isEmpty()) {
            return false;
        }
        if (resume.getJob() == null) {
            return false;
        }
        Optional<Job> job = this.jobRepository.findById(resume.getJob().getId());
        if (job.isEmpty()) {
            return false;
        }
        return true;
    }

    public ResCreateResumeDTO createResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    public ResUpdateResumeDTO updateResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void deleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResGetResumeDTO getResumeById(Resume resume) {
        ResGetResumeDTO res = new ResGetResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        res.setUser(new ResGetResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResGetResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO getAllResume(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());
        rs.setMeta(meta);
        List<ResGetResumeDTO> listResume = pageResume.getContent().stream().map(item -> this.getResumeById(item))
                .collect(Collectors.toList());
        rs.setResult(listResume);

        return rs;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageSize() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());

        rs.setMeta(meta);

        // remove sensitive data
        List<ResGetResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.getResumeById(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }
}
