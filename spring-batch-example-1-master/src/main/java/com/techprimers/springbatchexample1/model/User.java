package com.techprimers.springbatchexample1.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class User {

    @Id
    private Integer id;
    private String name;
    private String dept;
    private Integer salary;
    private Integer exp;
    private String profileName;
    private Date time;

    public User(Integer id, String name, String dept, Integer salary, Integer exp, String profileName, Date time) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.salary = salary;
        this.exp = exp;
        this.profileName = profileName;
        this.time = time;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", dept='").append(dept).append('\'');
        sb.append(", salary=").append(salary);
        sb.append(", exp=").append(exp);
        sb.append(", profileName='").append(profileName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

	public Integer getExp() {
		return exp;
	}

	public void setExp(Integer exp) {
		this.exp = exp;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
}
