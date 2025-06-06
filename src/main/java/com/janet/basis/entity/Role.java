package com.janet.basis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Role")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int roleID;
  @Column(nullable = false, unique = true)
  private String roleName;

  public int getRoleID() {
    return roleID;
  }

  public void setRoleID(int roleID) {
    this.roleID = roleID;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
}
