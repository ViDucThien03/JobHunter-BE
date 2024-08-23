package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.DTO.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResGetResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.Company;
import vn.hoidanit.jobhunter.model.Job;
import vn.hoidanit.jobhunter.model.Resume;
import vn.hoidanit.jobhunter.model.User;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        boolean isIdExist = this.resumeService.isExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("User or Job is not exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping("/resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.getResumeById(resume.getId());
        if (currentResume.isEmpty()) {
            throw new IdInvalidException("Resume is not exist");
        }
        Resume reqResume = currentResume.get();
        reqResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.updateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.getResumeById(id);
        if (currentResume.isEmpty()) {
            throw new IdInvalidException("Resume is not exist");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResGetResumeDTO> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.getResumeById(id);
        if (currentResume.isEmpty()) {
            throw new IdInvalidException("Resume is not exist");
        }
        return ResponseEntity.ok().body(this.resumeService.getResumeById(currentResume.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resume success")
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> specification,
            Pageable pageable) {
        List<Long> listJobId = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.getUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJob();
                if (companyJobs != null && companyJobs.size() > 0) {
                    listJobId = companyJobs.stream().map(x -> x.getId()).collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobSpecification = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(listJobId)).get());
        Specification<Resume> finalSpec = jobSpecification.and(specification);
        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resume by user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUSer(
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
