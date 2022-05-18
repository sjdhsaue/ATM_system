package jdbc.bank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdminStart {
	static Connection conn = null;
	static ResultSet rs = null;


	public static void AdminBegin(Connection conn) {
		while (true) {
			// 得到数据库连接
			Scanner console = new Scanner(System.in);

			try {

				while (true) {
					System.out.println("----欢迎使用澳门赌场 管理 系统----");
					boolean flag = Admin.login(conn);
					while (flag) {
						System.out.println("----正在进入系统......----");
						Thread.sleep(1000);
						System.out.println();
						System.out.println("----成功进入系统！----");
						while (true) {
							int in_num = 0;
							Admin.menu1();
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
									} else
										continue;
								}
								break;
							}
							switch (in_num) {
								case 1:// 查询用户信息及ATM
									System.out.println();
									Admin.menu_by_select(conn);
									System.out.println();
									continue;
								case 2:// 开户
									Admin.menu_by_insert(conn);
									continue;
								case 3:// 修改用户及管理密码
									Admin.menuChangePwd(conn);
									continue;
								case 4:// 销户
									Admin.delete_user(conn);
									continue;
								case 5:// 注销
									System.out.println("正在退出......");
									Thread.sleep(1000);
									System.out.println("一路走好");
									System.out.println();
									break;
								default:
									System.out.println("输入指令有误，请重新输入");
									continue;
							}
							break;

						}
						break;
					}
					System.out.println("是否继续使用澳门赌场 管理 系统？Y/任意键退出");
					String enter = console.next();
					if (enter.equalsIgnoreCase("Y")) {
						continue;
					} else {
						System.out.println("正在退出管理系统......");
						Thread.sleep(1000);
						System.out.println("欢迎再来哦~~~");
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
