package com.plum.usercenter.common;

import com.plum.usercenter.constant.PageConstant;
import lombok.Data;

/**
 * 通用分页请求
 */
@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = PageConstant.SORT_ORDER_ASC;
}
