package task.mail.main;

import java.util.Random;

import task.mail.utils.DESUtils;

public class makePwd {
	public static void main(String[] args) {
		String pwd = "sggggda888";
		int num = 0;
		while(!(num>100 && num <999)){
			num = new Random().nextInt(100000)*77 + 1;
		}
		pwd +=num;
		pwd = DESUtils.encode(pwd);
		System.out.println(pwd);
		System.out.println(DESUtils.decode(pwd));
	}
}
