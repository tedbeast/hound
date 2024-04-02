package org.revature.dto;

import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Lab {
    private long id;
    private long productKey;
    private long canonical;
    private Timestamp lastUpdated;
    private byte[] data;
}
