package com.fx.pan.domain;

import java.io.Serializable;

/**
 * 角色表
 * @TableName role
 */

public class Role implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 是否可用
     */
    private String available;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色名称
     */
    private String role;

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(Long roleid) {
        this.id = roleid;
    }

    /**
     * 是否可用
     */
    public String getAvailable() {
        return available;
    }

    /**
     * 是否可用
     */
    public void setAvailable(String available) {
        this.available = available;
    }

    /**
     * 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 角色描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 角色名称
     */
    public String getRole() {
        return role;
    }

    /**
     * 角色名称
     */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Role other = (Role) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAvailable() == null ? other.getAvailable() == null : this.getAvailable().equals(other.getAvailable()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAvailable() == null) ? 0 : getAvailable().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", available=").append(available);
        sb.append(", description=").append(description);
        sb.append(", role=").append(role);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
