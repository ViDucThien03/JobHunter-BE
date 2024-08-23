package vn.hoidanit.jobhunter.DTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateResumeDTO {
    private String updatedBy;
    private Instant updatedAt;
}
