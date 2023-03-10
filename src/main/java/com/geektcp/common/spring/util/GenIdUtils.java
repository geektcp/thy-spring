package com.geektcp.common.spring.util;

import com.geektcp.common.core.exception.BaseException;
import com.geektcp.common.core.generator.IdGenerator;
import com.geektcp.common.spring.model.po.Po;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.genid.GenId;

import javax.persistence.Table;

@Slf4j
public class GenIdUtils implements GenId<String> {
    @Override
    public String genId(String table, String column) {
        return getId(table);
    }

    public static String getId(String tableName) {
        String prefix = StringUtils.substringAfter(tableName, "des_");
        return IdGenerator.getId(prefix);
    }

    public static <A extends Po> String getIdByClass(Class<A> poClass) {
        String tableName = poClass.getAnnotation(Table .class).name();
        String prefix = StringUtils.substringAfter(tableName, "des_");
        return IdGenerator.getId(prefix);
    }

    public String genDefaultId(String table, String column) {
        try {
            String prefix = table;
            Thread.sleep(1);
            return IdGenerator.getId(prefix);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(e);
        }
    }

}
