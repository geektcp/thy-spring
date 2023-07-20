package com.geektcp.common.spring.util;

import com.geektcp.common.mosheh.exception.BaseException;
import com.geektcp.common.mosheh.generator.IdGenerator;
import com.geektcp.common.mosheh.system.Sys;
import com.geektcp.common.spring.model.po.Po;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.genid.GenId;

import javax.persistence.Table;

@Slf4j
public class GenIdUtils implements GenId<String> {

    private static String defaultPre = "_";

    public static void initPre(String pre){
        defaultPre = pre;
    }

    @Override
    public String genId(String table, String column) {
        return generateId(table);
    }

    public static String genId(String table) {
        return genIdByTable(table);
    }

    public static String genIdByPre(String table, String pre) {
        return generateId(table, pre);
    }

    public static <A extends Po> String getId(Class<A> poClass) {
        String tableName = poClass.getAnnotation(Table.class).name();
        String prefix = StringUtils.substringAfter(tableName, defaultPre);
        return IdGenerator.getId(prefix);
    }


    /////////////////////
    private static String genIdByTable(String table) {
        return generateId(table, defaultPre);
    }

    private static String generateId(String tableName) {
        String prefix = StringUtils.substringAfter(tableName, defaultPre);
        return IdGenerator.getId(prefix);
    }

    private static String generateId(String tableName, String pre) {
        String prefix = StringUtils.substringAfter(tableName, defaultPre);
        return IdGenerator.getId(prefix);
    }



}

