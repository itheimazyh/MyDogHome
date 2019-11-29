/**
 *
 */
package com.zscat.mallplus.sys.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 951449465@qq.com
 * @Time 2017年9月6日
 * @description
 */
@Service
public interface GeneratorService {
    List<Map<String, Object>> list(String tableName);

    byte[] generatorCode(String[] tableNames);
}