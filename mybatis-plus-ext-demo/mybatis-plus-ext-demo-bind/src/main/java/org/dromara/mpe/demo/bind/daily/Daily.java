package org.dromara.mpe.demo.bind.daily;

import com.baomidou.mybatisplus.annotation.TableField;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.demo.bind.user.User;
import org.dromara.mpe.demo.bind.user.UserDefine;
import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AutoDefine
@AutoRepository
@Table("autotable_daily")
public class Daily {

    private String id;
    private String content;
    @TableField("submit")
    private String submitter;
    @BindEntity(selectFields = {UserDefine.id, UserDefine.name}, conditions = @JoinCondition(selfField = DailyDefine.submitter, joinField = UserDefine.id), last = "limit 1")
    private User submitterUser;
    @BindField(entity = User.class, field = UserDefine.registeredDate, conditions = @JoinCondition(selfField = DailyDefine.submitter, joinField = UserDefine.id), last = "limit 1")
    private Long registeredDate;
}
