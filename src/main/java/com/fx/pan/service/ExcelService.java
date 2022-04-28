package com.fx.pan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fx.pan.domain.ExcelBean;
import com.fx.pan.domain.FileBean;

/**
 * (Excel)表服务接口
 *
 * @author leaving
 * @since 2022-04-05 13:20:16
 */
public interface ExcelService extends IService<ExcelBean> {

    ExcelBean selectByFileId(Long id);

    ExcelBean checkExist(Long id);

    boolean updateExcelData(Long id, String data);

    ExcelBean createCoEditingByFile(FileBean fileBean, String data);
}

