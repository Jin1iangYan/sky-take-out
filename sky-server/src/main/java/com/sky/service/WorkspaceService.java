package com.sky.service;

import com.sky.vo.BusinessDataVO;

public interface WorkspaceService {

    /**
     * 获取今日运营数据
     * @return 今日运营数据
     */
    BusinessDataVO getBusinessData();
}
