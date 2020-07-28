package com.system.permission.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.system.permission.entity.SysPermission;

/**
 * 菜单基础信息表
 */
public interface SysPermissionDao extends CrudRepository<SysPermission, Long>, JpaSpecificationExecutor<SysPermission> {
	
	public List<SysPermission> findByIsDel(Integer isDel);
	
	public List<SysPermission> findByIsDelAndParentId(Integer isDel,long pid);
	
	public SysPermission findByIdAndIsDel(long id,Integer isDel);
	
//	@Query(value = "select "+
//				   "  p.id, p.bs_name,p.parent_id pId, p.zindex, p.istype, p.bs_code, p.icon, p.page_url "+
//				   " from permission p "+
//				   " LEFT JOIN role_permission rp ON rp.permit_id=p.id "+
//				   " LEFT JOIN role r ON r.id=rp.role_id "+
//				   " LEFT JOIN user_role ur ON ur.role_id=r.id "+
//				   " WHERE ur.user_id=?1 and p.is_del=0 "+
//				   " GROUP BY p.id "+
//				   " order by p.zindex ", nativeQuery = true)
//	  public List<Map<String, Object>> getUserPerms(long id);
    @Query(value = "select "+
            "  p.id, p.bs_name,p.parent_id pId, p.zindex, p.istype, p.bs_code, p.bs_icon, p.page_url "+
            " from app_permission p "+
           " LEFT JOIN app_role_permission rp ON rp.permit_id=p.id "+
            " LEFT JOIN app_role r ON r.id=rp.role_id "+
           " LEFT JOIN app_user_role ur ON ur.role_id=r.id "+
           " WHERE ur.user_id=?1 and p.is_del=0 and rp.is_del=0 "+
           " order by p.zindex ", nativeQuery = true)
    public List<Map<String, Object>> getUserPerms(long id);
}
