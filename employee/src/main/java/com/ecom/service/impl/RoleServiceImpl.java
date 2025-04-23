package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.dao.RoleDao;
import com.ecom.entity.Role;
import com.ecom.proxy.RoleProxy;
import com.ecom.service.RoleService;
import com.ecom.util.MapperUtil;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private MapperUtil mapper;

	public RoleProxy createNewRole(RoleProxy role) {
		Role convertValue = mapper.convertValue(role, Role.class);
		return mapper.convertValue(roleDao.save(convertValue), RoleProxy.class);
	}
}
