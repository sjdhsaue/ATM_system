package jdbc.bank;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Atm {
	public static int user_card;
	public static boolean old_pass;

	public Atm() {
	}

	// 用户认证
	public static boolean login(Connection conn) {
		while (true) {
			boolean card = false;
			boolean password = false;
			boolean flag = false;
			try {
				Scanner console = new Scanner(System.in);
				System.out.println("请输入您的卡号：");
				while (true) {
					try {
						user_card = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确类型的六位卡号：");
						String input = console.next();
						Pattern pattern = Pattern.compile("[0-9]*");
						boolean digit = pattern.matcher(input).matches();
						if (digit) {
							break;
						} else continue;
					}
					break;
				}

				Statement stmt = conn.createStatement();
				stmt.execute("select * from user");
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					if (user_card == rs.getInt(1)) {
						card = true;
						break;
					} else {
						card = false;
					}
				}
				if (card == true) {
					int count = 3;
					while (true) {
						System.out.println("请输入您的密码：");
						stmt.execute("select * from user where user_card = '" + user_card + "'");
						rs = stmt.getResultSet();
						int user_password = 0;
						while (true) {
							try {
								user_password = console.nextInt();
							} catch (InputMismatchException e) {
								System.out.print("请输入正确类型的六位密码：");
								String input = console.next();
								Pattern pattern = Pattern.compile("[0-9]*");
								boolean digit = pattern.matcher(input).matches();
								if (digit) {
									break;
								} else continue;
							}
							break;
						}
						while (rs.next()) {
							if (user_password == rs.getInt(2)) {
								password = true;
								stmt.execute("select user_action from user where user_card = '" + user_card + "'");
								rs = stmt.getResultSet();
								while (rs.next()) {
									if (rs.getInt(1) == 1) {
										flag = true;
										break;
									} else {
										System.out.println("您的账户已被冻结");
										System.out.println();
									}
								}
								break;
							} else {
								password = false;
							}
							break;
						}
						if (count == 1 && password == false) {
							stmt.execute("update user set user_action = 0 where user_card = '" + user_card + "'");
							System.out.println("输入密码错误超过三次，您的账户已被冻结，滚吧");
							break;
						}
						if (password == false) {
							System.out.println("密码错误，请重新输入");
							count--;
							System.out.println("剩余输入次数：" + count);
							continue;
						}
						break;
					}
				} else {
					System.out.println("卡号错误");
					continue;
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return flag;
		}
	}

	// 菜单
	public static void menu1() {
		System.out.println("*--------ATM--------");
		System.out.println("*      1、查询用户信息             ");
		System.out.println("*      2、在线存款     ");
		System.out.println("*      3、劲爆提现                    ");
		System.out.println("*      4、转账                            ");
		System.out.println("*      5、修改密码                    ");
		System.out.println("*      6、退卡                            ");
		System.out.println("*--------版本 1.0.0-------");
		System.out.println("请根据菜单序号进行操作：");
	}

	// 查询
	public static void select(Connection conn) {
		while (true) {
			try {
				Scanner console = new Scanner(System.in);
				Statement statement = conn.createStatement();

				statement.execute("select * from user where user_card = " + user_card);

				ResultSet rs = statement.getResultSet();
				System.out.println("正在查询......");
				sleep(1000);
				rs.next();
				System.out.println("查询成功！");
				String user_action;
				if (rs.getInt(5) == 1) {
					user_action = "正常";
				} else {
					user_action = "冻结";
				}
				System.out.println("卡号：" + rs.getInt(1) + "   姓名：" + rs.getString(3) + "   余额：" + rs.getInt(4)
						+ "   账户状态：" + user_action);
				System.out.println();
				System.out.println("是否继续查询？Y/任意键退出");
				String str = console.next();
				if (str.equalsIgnoreCase("Y")) {
					continue;
				} else {
					System.out.println("正在退出查询系统......");
					sleep(1000);
					break;
				}

			} catch (SQLException | InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}

	// 存款
	public static void save(Connection conn) {
		int count = 3;
		while (true) {
			try {
				System.out.println("请输入存钱金额： ");
				Scanner sc = new Scanner(System.in);
				int money = 0;
				while (true) {
					try {
						money = sc.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确的数字金额：");
						String input = sc.next();
						Pattern pattern = Pattern.compile("[0-9]*");
						boolean digit = pattern.matcher(input).matches();
						if (digit) {
							break;
						} else continue;
					}
					break;
				}

				if (money < 0) {
					if (count != 0) {
						count--;
						System.out.println("存款不能小于零！再捣乱" + count + "次退出存款系统");
						continue;
					} else {
						System.out.println("正在退出存款系统.....");
						sleep(1000);
						break;
					}
				} else {
					CallableStatement cs = conn.prepareCall("{call save(?,?,?)}");
					cs.setInt(1, user_card);
					cs.setInt(2, money);
					cs.execute();
					System.out.println("正在存款......");
					sleep(1000);
					String res = cs.getString(3);
					if (res.equals("存款成功")) {
						Scanner console = new Scanner(System.in);
						System.out.println("存款成功，是否打印凭条Y/任意键退出");
						String in = console.next();
						if (in.equalsIgnoreCase("Y")) {
							System.out.println("正在打印凭条......");
							sleep(1000);
							Statement stmt = conn.createStatement();
							stmt.execute("select user_money from user where user_card = '" + user_card + "'");
							ResultSet rs = stmt.getResultSet();
							rs.next();
							System.out.println("此次存入金额：" + money);
							System.out.println("账户余额：" + rs.getInt(1));
							System.out.println();
							System.out.println("是否继续存款：Y/任意键退出 ?");
							String in_in_yn = console.next();
							if (in_in_yn.equalsIgnoreCase("Y")) {
								continue;
							} else {
								System.out.println("正在退出存款系统......");
								sleep(1000);
								break;
							}
						} else {
							System.out.println();
							System.out.println("是否继续存款：Y/任意键退出 ?");
							String in_in_yn = console.next();
							if (in_in_yn.equalsIgnoreCase("Y")) {
								continue;
							} else {
								System.out.println("正在退出存款系统......");
								sleep(1000);
								break;
							}
						}
					} else {
						Scanner console = new Scanner(System.in);
						System.out.println(res);
						System.out.println();
						System.out.println("是否继续存款：Y/任意键退出 ?");
						String in_in_yn = console.next();
						if (in_in_yn.equalsIgnoreCase("Y")) {
							continue;
						} else {
							System.out.println("正在退出存款系统......");
							sleep(1000);
							break;
						}
					}
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 取款
	public static void remove(Connection conn) {
		while (true) {
			try {
				System.out.println("请输入取款金额");
				Scanner sc = new Scanner(System.in);
				int money = 0;
				while (true) {
					try {
						money = sc.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确的数字金额：");
						String input = sc.next();
						Pattern pattern = Pattern.compile("[0-9]*");
						boolean digit = pattern.matcher(input).matches();
						if (digit) {
							break;
						} else continue;
					}
					break;
				}
				CallableStatement cs = conn.prepareCall("{call user_remove(?,?,?)}");
				cs.setInt(1, user_card);
				cs.setInt(2, money);
				cs.execute();
				System.out.println("正在取款......");
				sleep(1000);
				String res = cs.getString(3);
				if (res.equals("取款成功")) {
					Scanner console = new Scanner(System.in);
					System.out.println("取款成功，是否打印凭条Y/任意键退出");
					String in = console.next();
					if (in.equalsIgnoreCase("Y")) {
						System.out.println("正在打印凭条......");
						sleep(1000);
						Statement stmt = conn.createStatement();
						stmt.execute("select user_money from user where user_card = '" + user_card + "'");
						ResultSet rs = stmt.getResultSet();
						rs.next();
						System.out.println("此次取款金额：" + money);
						System.out.println("账户余额：" + rs.getInt(1));
						System.out.println();
						System.out.println("是否继续取款：Y/任意键退出 ?");
						String in_in_yn = console.next();
						if (in_in_yn.equalsIgnoreCase("Y")) {
							continue;
						} else {
							System.out.println("正在退出取款系统......");
							sleep(1000);
							break;
						}
					} else {
						System.out.println();
						System.out.println("是否继续取款：Y/任意键退出 ?");
						String in_in_yn = console.next();
						if (in_in_yn.equalsIgnoreCase("Y")) {
							continue;
						} else {
							System.out.println("正在退出取款系统......");
							sleep(1000);
							break;
						}
					}
				} else {
					System.out.println(res);
					System.out.println();
					System.out.println("是否继续取款：Y/任意键退出 ?");
					Scanner console = new Scanner(System.in);
					String in_in_yn = console.next();
					if (in_in_yn.equalsIgnoreCase("Y")) {
						continue;
					} else {
						System.out.println("正在退出取款系统......");
						sleep(1000);
						break;
					}
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 转账
	public static void acounts(Connection conn) {
		while (true) {
			try {
				boolean out_card_flag = false;
				Scanner console = new Scanner(System.in);
				System.out.println("请输入转账的账号：");
				int out_card = 0;
				while (true) {
					try {
						out_card = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确类型的六位数卡号：");
						String input = console.next();
						Pattern pattern = Pattern.compile("[0-9]*");
						boolean digit = pattern.matcher(input).matches();
						if (digit) {
							break;
						} else continue;
					}
					break;
				}
				Statement stmt = conn.createStatement();
				stmt.execute("select * from user");
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					if (out_card == rs.getInt(1)) {
						out_card_flag = true;
						break;
					} else {
						out_card_flag = false;
					}
				}
				if (out_card_flag == true) {
					boolean out_yn = true;
					stmt.execute("select * from user where user_card =" + out_card);
					rs = stmt.getResultSet();
					rs.next();
					System.out.println("正在查找转账账户......");
					sleep(1000);
					System.out.println("卡号：" + out_card + "   姓名：" + rs.getString(3));
					System.out.println("是否确认转账账户？Y/任意键重新输入");/////////////////////////////////////////////////////////////////
					String in_money = console.next();
					if (!(in_money.equalsIgnoreCase("Y"))) {
						out_yn = false;
					}
					if (out_yn == false) {
						System.out.println();
						System.out.println("是否继续转账：Y/任意键退出 ?");
						String in_in_yn = console.next();
						if (in_in_yn.equalsIgnoreCase("Y")) {
							continue;
						} else {
							System.out.println("正在退出转账系统......");
							sleep(1000);
							break;
						}
					}

					System.out.println("请输入转账的金额：");
					int out_money = 0;
					while (true) {
						try {
							out_money = console.nextInt();
						} catch (InputMismatchException e) {
							System.out.print("请输入正确的数字金额：");
							String input = console.next();
							Pattern pattern = Pattern.compile("[0-9]*");
							boolean digit = pattern.matcher(input).matches();
							if (digit) {
								break;
							} else continue;
						}
						if (out_money > 0) {
							break;
						} else {
							System.out.println("金额必须大于0，请重新输入：");
							continue;
						}
					}
					////////////////////////////////////////////////
					int count = 3;
					boolean password = false;
					while (true) {
						System.out.println("请输入您的密码：");
						stmt.execute("select * from user where user_card = '" + user_card + "'");
						rs = stmt.getResultSet();
						int user_password = 0;
						while (true) {
							try {
								user_password = console.nextInt();
							} catch (InputMismatchException e) {
								System.out.print("请输入正确类型的六位密码：");
								String input = console.next();
								Pattern pattern = Pattern.compile("[0-9]*");
								boolean digit = pattern.matcher(input).matches();
								if (digit) {
									break;
								} else continue;
							}
							break;
						}
						while (rs.next()) {
							if (user_password == rs.getInt(2)) {
								password = true;
								break;
							} else {
								password = false;
							}
							break;
						}
						if (count == 1 && password == false) {
							System.out.println("输入密码错误超过三次，强制退出转账系统......");
							sleep(1000);
							break;
						}
						if (password == false) {
							System.out.println("密码错误，请重新输入");
							count--;
							System.out.println("剩余输入次数：" + count);
							continue;
						}
						break;
					}
					if (password == false) {
						System.out.println("正在退出系统......");
						sleep(1000);
						break;
					}

					/////////////////////////////////////////////////
					CallableStatement cstmt = conn.prepareCall("{call bank_acounts(?,?,?)}");
					cstmt.setInt(1, user_card);
					cstmt.setInt(2, out_card);
					cstmt.setInt(3, out_money);
					System.out.println("正在转账......");
					sleep(1000);
					cstmt.execute();
					rs = cstmt.getResultSet();
					rs.next();
					System.out.println(rs.getString(1));
					if (rs.getString(1).equals("转账成功")) {
						System.out.println("是否打印凭条： Y/任意键退出 ?");
						String in_yn = console.next();
						if (in_yn.equalsIgnoreCase("Y")) {
							System.out.println("正在打印凭条......");
							sleep(1000);
							System.out.println("转出账号：" + user_card + "    转入账号：" + out_card + "   转账金额： " + out_money);
							System.out.println();
							System.out.println("是否继续转账：Y/任意键退出 ?");
							String in_in_yn = console.next();
							if (in_in_yn.equalsIgnoreCase("Y")) {
								continue;
							} else {
								System.out.println("正在退出转账系统......");
								sleep(1000);
								break;
							}
						} else {
							System.out.println();
							System.out.println("是否继续转账：Y/任意键退出 ?");
							String in_in_yn = console.next();
							if (in_in_yn.equalsIgnoreCase("Y")) {
								continue;
							} else {
								System.out.println("正在退出转账系统......");
								sleep(1000);
								break;
							}
						}
					} else {
						System.out.println();
						System.out.println("是否继续转账：Y/任意键退出 ?");
						String in_in_yn = console.next();
						if (in_in_yn.equalsIgnoreCase("Y")) {
							continue;
						} else {
							System.out.println("正在退出转账系统......");
							sleep(1000);
							break;
						}
					}
				} else {
					System.out.println("查无此人");
					System.out.println("正在重新加载转账系统......");
					sleep(2000);
					continue;
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 修改密码

	public static void changeUserPassword(Connection conn) {

		try {
			Statement st = conn.createStatement();
			boolean b = st.execute("select user_password from user where user_card = " + user_card + "");
			if (b) {
				ResultSet rs2 = st.getResultSet();
				rs2.next();
				int oldPassword = rs2.getInt("user_password");
				boolean flag = true;
				int login_count = 3;
				int new_pass_count = 3;
				int new_one_pass = 3;
				while (flag) {
					Scanner sc = new Scanner(System.in);
					System.out.print("输入6位数字旧密码 ");
					int passwd = 0;
					boolean isDigit = true;
					while (isDigit) {
						try {
							passwd = sc.nextInt();
						} catch (InputMismatchException e) {
							System.out.print("请输入六位数字密码  ");
							String input = sc.next();
							Pattern pattern = Pattern.compile("[0-9]*");
							boolean digit = pattern.matcher(input).matches();
							if (digit) {
								break;
							} else continue;
						}
						break;
					}
					if (oldPassword == passwd) {
						while (true) {

							System.out.print("新密码  ");
							int newpassword = 0;
							while (isDigit) {
								try {
									newpassword = sc.nextInt();
								} catch (InputMismatchException e) {
									System.out.print("请输入六位数字密码  ");
									String input1 = sc.next();
									Pattern pattern = Pattern.compile("[0-9]*");
									boolean digit = pattern.matcher(input1).matches();
									if (digit) {
										break;
									} else continue;
								}
								break;
							}
							int len = Integer.toString(newpassword).length();
							if (len != 6) {
								--new_one_pass;
								System.out.println("密码位数错误,剩余次数" + new_one_pass);
								if (new_one_pass == 0) {
									System.out.println("密码输入次数用完,正在退出...");
									try {
										sleep(2000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

									break;
								}
								continue;
							} else {
								System.out.print("确认新密码  ");
								int newpassword2 = 0;
								while (isDigit) {
									try {
										newpassword2 = sc.nextInt();
									} catch (InputMismatchException e) {
										System.out.print("请输入六位数字密码  ");
										String input1 = sc.next();
										Pattern pattern = Pattern.compile("[0-9]*");
										boolean digit = pattern.matcher(input1).matches();
										if (digit) {
											break;
										} else continue;
									}
									break;
								}
								if (newpassword == newpassword2) {
									System.out.println("正在修改您的密码......");
									sleep(1000);
									st.execute("update user set user_password='" + newpassword + "' where user_card = " + user_card + "");
									System.out.println("修改成功");
									flag = false;
									break;
								} else {
									System.out.println("密码两次不相同");
									--new_pass_count;
									if (new_pass_count == 0) {
										flag = false;
										break;
									}
									continue;
								}
							}
						}

					} else {
						--login_count;
						if (login_count == 0) {
							try {
								System.out.println("旧密码输入次数用完，正在退出...");
								sleep(1000);
								old_pass = false;

								break;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							flag = false;
						}
						System.out.println("密码错误，请重新输入" + "  " + "剩余次数" + login_count);
						continue;
					}
					break;
				}
			}
		} catch (SQLException | InterruptedException e) {
			e.printStackTrace();
		}

	}
}
