package jdbc.bank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserStart {
	static Connection conn = null;
	static ResultSet rs = null;


	public static void UserBegin(Connection conn) {
		// 得到数据库连接
		while (true) {
			try {
				Scanner console = new Scanner(System.in);
				while (true) {
					System.out.println("----欢迎使用 ATM 系统----");
					boolean flag = Atm.login(conn);
					if (flag == true) {
						System.out.println("----正在进入系统......----");
						Thread.sleep(1000);
						System.out.println();
						System.out.println("----成功进入系统！----");
						while (true) {
							int in_num = 0;
							Atm.menu1();
							while (true) {
								try {
									in_num = console.nextInt();
								} catch (InputMismatchException e) {
									System.out.print("请输入正确的数字指令：");
									String input = console.next();
									Pattern pattern = Pattern.compile("[0-9]*");
									boolean digit = pattern.matcher(input).matches();
									if (digit) {
										break;
									} else continue;
								}
								break;
							}
							switch (in_num) {
								case 1://查询信息
									Atm.select(conn);
									continue;
								case 2://存款
									Atm.save(conn);
									continue;
								case 3://取款
									Atm.remove(conn);
									continue;
								case 4://转账
									Atm.acounts(conn);
									continue;
								case 5://修改密码
									Atm.changeUserPassword(conn);
									if (Atm.old_pass) {
										continue;
									} else
										break;
								case 6://注销
									System.out.println("正在退卡......");
									Thread.sleep(1000);
									System.out.println("请拿走您的银行卡！");
									System.out.println();
									break;
								default:
									System.out.println("输入指令有误，请重新输入");
									continue;
							}
							break;
						}
					}
					System.out.println("是否继续使用 ATM 系统？Y/任意键退出");
					String enter = console.next();
					if (enter.equalsIgnoreCase("Y")) {
						continue;
					} else {
						System.out.println("正在退出终端系统......");
						Thread.sleep(1000);
						System.out.println("欢迎再来赌哦~~~");
						break;
					}
				}
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
