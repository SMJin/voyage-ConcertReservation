package kr.hhplus.be.server.point.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointChargeRequest {
    private String username;
    private Long amount;
}

