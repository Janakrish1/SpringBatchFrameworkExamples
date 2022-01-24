package com.techprimers.springbatchexample1.batch;

import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.techprimers.springbatchexample1.model.User;

@Component
public class ExpProcessor implements ItemProcessor<User, User> {
	
	@Override
	public User process(User user) throws Exception {
		System.out.println("Inside Step 2 Process");
		Integer exp = user.getExp();
		String profileName = "";
		if(exp >= 0 && exp < 5) {
			profileName = "Developer";
		}
		else if(exp >= 5 && exp < 10) {
			profileName = "Team Lead";
		}
		else {
			profileName = "Manager";
		}
		user.setTime(new Date());
		user.setProfileName(profileName);
		System.out.println(String.format("Converted Exp [%s] to Profile Name [%s]", exp, profileName));
		return user;
	}
}
