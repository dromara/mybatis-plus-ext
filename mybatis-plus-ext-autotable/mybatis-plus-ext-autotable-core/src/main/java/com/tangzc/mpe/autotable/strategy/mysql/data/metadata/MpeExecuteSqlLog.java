package com.tangzc.mpe.autotable.strategy.mysql.data.metadata;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author don
 */
@Data
@RequiredArgsConstructor(staticName = "newInstance")
public class MpeExecuteSqlLog {

    private Long id;
    @NonNull
    private String sql;
    private LocalDateTime executeTime = LocalDateTime.now();
}
