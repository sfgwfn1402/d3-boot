package com.dddframework.demo.infras.repository.mysql.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dddframework.demo.infras.repository.mysql.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
