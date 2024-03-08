package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.autotable.annotation.mysql.MysqlTypeConstant;
import com.tangzc.mpe.annotation.DefaultValue;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

/**
 * 我的消息
 * @author don
 */
@Data
@AutoRepository
@Table(comment = "我的消息")
public class Notice {

    @Column(comment = "消息id", type = MysqlTypeConstant.BIGINT, length = 32)
    private String id;

    @Column(comment = "图标")
    private String avatar;

    @Column(comment = "标题（简略内容）")
    private String title;

    @Column(comment = "内容", type = MysqlTypeConstant.TEXT)
    private String description;

    @Column(comment = "额外信息")
    private String extra;

    @DefaultValue("0")
    @Column(value = "`read`", comment = "是否已读")
    private Boolean read;

    @Column(comment = "时间")
    private long datetime;

    @Column(value = "`owner`", comment = "所属用户")
    private String owner;

    @Column(comment = "超链接",type = MysqlTypeConstant.TEXT)
    private String url;
}
