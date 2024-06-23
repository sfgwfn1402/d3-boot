package com.dddframework.demo.infras.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dddframework.demo.infras.repository.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
