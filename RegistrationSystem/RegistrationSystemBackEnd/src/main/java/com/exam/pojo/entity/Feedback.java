package com.exam.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 问题反馈(Feeback)实体类
 *
 * @author makejava
 * @since 2023-5-11 11:12:37
 */
@Data
public class Feedback implements Serializable {
    private static final long serialVersionUID = -30211505852000448L;

    private String id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 回复内容
     */
    private String replyContent;
    /**
     * 回复人id
     */
    private String replyUserId;
    /**
     * 回复时间
     */
    private String replyTime;





}

