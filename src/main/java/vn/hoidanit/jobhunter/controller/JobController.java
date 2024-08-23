package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.DTO.ResCreateJobDTO;
import vn.hoidanit.jobhunter.DTO.ResultPaginationDTO;
import vn.hoidanit.jobhunter.error.IdInvalidException;
import vn.hoidanit.jobhunter.model.Job;

import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create new job successed")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update new job successed")
    public ResponseEntity<ResCreateJobDTO> updateJobs(@RequestBody Job job) throws IdInvalidException {
        Job currentJob = this.jobService.getJobById(job.getId());
        if (currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.updateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete new job successed")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currentJob = this.jobService.getJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
        Job currentJob = this.jobService.getJobById(id);
        return ResponseEntity.status(HttpStatus.OK).body(currentJob);
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all Jobs new job successed")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> specification,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.getAllJobs(specification, pageable));
    }

}
