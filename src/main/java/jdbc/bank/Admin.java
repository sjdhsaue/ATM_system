package jdbc.bank;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Admin {
	private static String admin_card;

	public Admin() {
	}

	// 用户认证
	public static boolean login(Connection conn) {
		while (true) {
			boolean card = false;
			boolean password = false;
			boolean flag = false;
			try {
				Scanner console = new Scanner(System.in);
				System.out.println("请输入管理员账号：");
				while (true) {
					admin_card = console.next();
					String reg = "^[a-z]+$";
					if (admin_card.matches(reg)) {
						break;
					} else {
						System.out.println("账号只能是小写字母的组合，请重新输入：");
						continue;
					}
				}
				Statement stmt = conn.createStatement();
				stmt.execute("select * from admin");
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					if (admin_card.equals(rs.getString(1))) {
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
								} else
									continue;
							}
							break;
						}
						stmt.execute("select * from admin where admin_id = '" + admin_card + "'");
						rs = stmt.getResultSet();
						while (rs.next()) {
							if (user_password == rs.getInt(2)) {
								password = true;
								flag = true;
								break;
							} else {
								password = false;
							}
							break;
						}
						if (count == 1 && password == false) {
							// stmt.execute("update user set user_action = 0
							// where user_card = '" + admin_card + "'");//应该删除他
							stmt.execute("insert into admin_old values ('" + admin_card + "')");
							stmt.execute("delete from admin where admin_id = '" + admin_card + "'");
							System.out.println("正在删除你的管理账户......");
							sleep(1000);
							System.out.println("输入密码错误超过三次，你被炒了，滚吧");
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
					System.out.println("查无此人");
					continue;
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return flag;
		}
	}

	// 菜单
	public static void menu1() {
		System.out.println("  *-------澳门赌场管理--------");
		System.out.println("  *  1、查询用户信息及ATM         ");
		System.out.println("  *  2、用户开户及增加管理员      ");
		System.out.println("  *  3、修改信息及密码                 ");
		System.out.println("  *  4、销户                                    ");
		System.out.println("  *  5、注销                                    ");
		System.out.println("  *----------版本 1.0.0-------");
		System.out.println("请根据菜单序号进行操作：");
	}

	// 查询主菜单
	public static void menu_by_select(Connection conn) {
		while (true) {
			System.out.println("  *---------查询-------------");
			System.out.println("  *  1、查询所有用户信息             ");
			System.out.println("  *  2、根据卡号查询                     ");
			System.out.println("  *  3、查询ATM机余额                 ");
			System.out.println("  *  4、返回上一级                         ");
			System.out.println("  *--------------------------");
			System.out.println("请输入查询指令：");
			Scanner console = new Scanner(System.in);
			int in_num = console.nextInt();
			switch (in_num) {
				case 1:
					select_all(conn);
					continue;
				case 2:
					select_by_card(conn);
					continue;
				case 3:
					update_atm_menu(conn);
					continue;
				case 4:
					System.out.println("正在退出......");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("输入指令有误，请重新输入");
					continue;
			}
			break;
		}
	}

	// ATM主菜单
	public static void update_atm_menu(Connection conn) {
		while (true) {
			System.out.println("正在查询当前ATM余额：");
			ResultSet rs = null;
			try {
				sleep(1000);
				Statement stmt = conn.createStatement();
				stmt.execute("select * from atm");
				rs = stmt.getResultSet();
				rs.next();

				System.out.println("  *------ATM余额菜单--------");
				System.out.println("  * 当前ATM余额：   " + rs.getInt(2) + "元    *");
				System.out.println("  *  1、往ATM放钱                        ");
				System.out.println("  *  2、从ATM取钱                        ");
				System.out.println("  *  3、返回上一级                         ");
				System.out.println("  *--------------------------");
				System.out.println("请输入指令：");
				Scanner console = new Scanner(System.in);
				int in_num = console.nextInt();
				switch (in_num) {
					case 1:
						dealCashIn(conn);
						continue;
					case 2:
						dealCashOut(conn);
						continue;
					case 3:
						System.out.println("正在退出......");
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("输入指令有误，请重新输入");
						continue;
				}
				break;
			} catch (InterruptedException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
		}
	}

	// 从ATM取钱
	public static void dealCashOut(Connection conn) {

		boolean flag = true;
		while (flag) {
			Scanner sc = new Scanner(System.in);
			int input_m = 0;
			System.out.println("要取出的现金金额：");
			while (true) {
				try {
					input_m = sc.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("请输入数字：");
					String input = sc.next();
					Pattern pattern = Pattern.compile("[0-9]*");
					boolean digit = pattern.matcher(input).matches();
					if (digit) {
						break;
					} else {
						continue;
					}
				}
				if (input_m % 100 == 0) {
					break;
				} else {
					System.out.println("请输入100的正整数倍数：");
					continue;
				}
			}
			try {
				CallableStatement cs = conn.prepareCall("{call decrease_m(?,?)}");
				cs.setInt(1, input_m);
				cs.execute();
				String rs = cs.getString(2);
				System.out.println("正在从ATM取钱.....");
				sleep(1000);
				System.out.println(rs);
				Scanner console = new Scanner(System.in);
				System.out.println("是否继续取钱？Y/任意键退出系统");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在返回上一级......");
					sleep(1000);
					break;
				}

			} catch (SQLException | InterruptedException e) {
				System.out.println("调用存储过程发生异常");
			}
		}
	}

	// 存钱到ATM
	public static void dealCashIn(Connection conn) {

		boolean flag = true;
		while (flag) {
			Scanner sc = new Scanner(System.in);
			int input_m = 0;
			System.out.println("要存入的现金金额：");
			while (true) {
				try {
					input_m = sc.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("请输入数字：");
					String input = sc.next();
					Pattern pattern = Pattern.compile("[0-9]*");
					boolean digit = pattern.matcher(input).matches();
					if (digit) {
						break;
					} else {
						continue;
					}
				}
				if (input_m % 100 == 0) {
					break;
				} else {
					System.out.println("请输入100的正整数倍数：");
					continue;
				}
			}
			try {
				CallableStatement cs = conn.prepareCall("{call increase_m(?,?)}");
				cs.setInt(1, input_m);
				cs.execute();
				String rs = cs.getString(2);
				System.out.println(rs);
				Scanner console = new Scanner(System.in);
				System.out.println("是否继续放钱？Y/任意键退出系统");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在返回上一级......");
					sleep(1000);
					break;
				}
			} catch (SQLException | InterruptedException e) {
				System.out.println("调用存储过程发生异常");
			}
		}
	}

	// 查询所有用户信息
	public static void select_all(Connection conn) {
		while (true) {
			try {
				System.out.println("正在查询所有用户信息......");
				Scanner console = new Scanner(System.in);
				sleep(1000);
				Statement stmt = conn.createStatement();
				stmt.execute("select * from user");
				ResultSet rs = stmt.getResultSet();
				System.out.println("查询成功");
				while (rs.next()) {
					int card = rs.getInt("user_card");
					String name = rs.getString("user_name");
					int money = rs.getInt("user_money");
					int user_action = rs.getInt("user_action");
					String action;
					if (user_action == 1) {
						action = "正常";
					} else {
						action = "冻结";
					}
					System.out.println("卡号：" + card + "   姓名：" + name + "   余额：" + money + "   状态：" + action);
				}
				System.out.println("是否继续查询？Y/任意键退出系统");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入查询系统......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在退出查询系统......");
					sleep(1000);
					break;
				}
			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// 更改用户状态
	public static void change_action(Connection conn) {
		while (true) {
			boolean card = false;
			try {
				Scanner console = new Scanner(System.in);
				System.out.println("请输入您的卡号：");
				int user_card = 0;
				while (true) {
					try {
						user_card = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确类型的卡号：");
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
					while (true) {
						System.out.println("请更正用户的状态(冻结/正常)：");
						String action = console.next();
						int user_action = 0;
						if (action.equals("正常")) {
							user_action = 1;
						} else if (action.equals("冻结")) {
							user_action = 0;
						} else {
							System.out.println("输入有误，请重新输入");
							continue;
						}
						System.out.println("正在修改用户状态......");
						sleep(1000);
						stmt.execute("update user set user_action =" + user_action + " where user_card =" + user_card);
						System.out.println("修改成功！");
						System.out.println("卡号：" + user_card + "   状态：" + action);
						break;
					}
				} else {
					System.out.println("账号不存在");
					System.out.println("是否继续修改用户状态？Y/任意键退出系统");
					String in_yn = console.next();
					if (in_yn.equalsIgnoreCase("Y")) {
						System.out.println("正在重新载入修改用户状态系统......");
						sleep(1000);
						continue;
					} else {
						System.out.println("正在退出修改用户状态系统......");
						sleep(1000);
						break;
					}
				}
				System.out.println("是否继续修改用户状态？Y/任意键退出系统");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入修改用户状态系统......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在退出修改用户状态系统......");
					sleep(1000);
					break;
				}

			} catch (SQLException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// 开户及增加管理员主菜单
	public static void menu_by_insert(Connection conn) {
		while (true) {
			System.out.println("  *----用户开户及增加管理员---");
			System.out.println("  *  1、用户开户                            ");
			System.out.println("  *  2、增加管理员                        ");
			System.out.println("  *  3、返回上一级                         ");
			System.out.println("  *--------------------------");
			System.out.println("请输入查询指令：");
			Scanner console = new Scanner(System.in);
			int in_num = console.nextInt();
			switch (in_num) {
				case 1:
					insert_account(conn);
					continue;
				case 2:
					insert_admin(conn);
					continue;
				case 3:
					System.out.println("正在退出......");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("输入指令有误，请重新输入");
					continue;
			}
			break;
		}
	}

	// 用户开户
	public static void insert_account(Connection conn) {
		try {
			while (true) {

				Scanner console = new Scanner(System.in);
				System.out.println("请输入您的姓名：");
				String user_name = console.next();
				String reg = "[\\u4e00-\\u9fa5]+";
				if (!(user_name.matches(reg))) {
					System.out.println("请输入汉字！");
					continue;
				} else {
					while (true) {
						System.out.println("请输入您的密码：");
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
								} else
									continue;
							}
							break;
						}
						int len = Integer.toString(user_password).length();
						if (len != 6) {
							System.out.println("密码位数出错，请重新输入：");
							continue;
						} else {
							System.out.println("请输入您存款的金额：");
							int money = 0;
							while (true) {
								try {
									money = console.nextInt();
								} catch (InputMismatchException e) {
									System.out.print("请输入正确的数字金额：");
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
							int user_card = (int) (Math.random() * 999999) + 100000;
							System.out.println("正在开户......");
							sleep(1000);
							CallableStatement cstmt = conn.prepareCall("{call insert_user(?,?,?,?,?)}");
							cstmt.setInt(1, user_card);
							cstmt.setInt(2, user_password);
							cstmt.setString(3, user_name);
							cstmt.setInt(4, money);
							cstmt.setInt(5, 1);
							cstmt.execute();
							System.out.println("开户成功");
							System.out.println("正在打印用户信息......");
							sleep(1000);
							System.out
									.println("卡号：" + user_card + "   姓名：" + user_name + "   余额：" + money + "   状态：正常");
							break;
						}
					}

				}
				System.out.println("是否继续开户？Y/任意键退出");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入开户系统......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在退出系统......");
					sleep(1000);
					break;
				}
			}
		} catch (SQLException | InterruptedException e) {

		}
	}

	// 增加管理员
	public static void insert_admin(Connection conn) {
		try {
			while (true) {
				Scanner console = new Scanner(System.in);
				System.out.println("请输入要添加的管理员账号：");
				String more_card = null;
				while (true) {
					more_card = console.next();
					String reg = "^[a-z]+$";
					if (more_card.matches(reg)) {
						break;
					} else {
						System.out.println("账号只能是小写字母的组合，请重新输入：");
						continue;
					}
				}
				while (true) {
					System.out.println("请输入您的密码：");
					int admin_password = 0;
					while (true) {
						try {
							admin_password = console.nextInt();
						} catch (InputMismatchException e) {
							System.out.print("请输入正确类型的六位密码：");
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
					int len = Integer.toString(admin_password).length();
					if (len != 6) {
						System.out.println("密码位数出错，请重新输入：");
						continue;
					} else {
						System.out.println("正在增加管理员......");
						sleep(1000);
						CallableStatement cstmt = conn.prepareCall("{call insert_admin(?,?)}");
						cstmt.setString(1, more_card);
						cstmt.setInt(2, admin_password);
						cstmt.execute();
						System.out.println("增加成功");
						break;
					}
				}
				System.out.println("是否继续增加管理员？Y/任意键退出");
				String in_yn = console.next();
				if (in_yn.equalsIgnoreCase("Y")) {
					System.out.println("正在重新载入......");
					sleep(1000);
					continue;
				} else {
					System.out.println("正在退出系统......");
					sleep(1000);
					break;
				}
			}
		} catch (SQLException | InterruptedException e) {

		}
	}

	// 修改管理员密码
	public static void changeAdminPassword(Connection conn) {
		int admin_password_old = 0;
		Scanner sc = new Scanner(System.in);
		try {
			Statement statement = conn.createStatement();
			boolean b = statement.execute("SELECT admin_password from admin where admin_id = '" + admin_card + "'");
			if (b) {
				ResultSet rs = statement
						.executeQuery("SELECT admin_password from admin where admin_id = '" + admin_card + "'");
				rs.next();
				admin_password_old = rs.getInt(1);
				CallableStatement cs = conn.prepareCall("call changeadminpwd(?,?)");
				boolean flag = true;
				int admin_try_count_left = 3;
				while (flag) {
					System.out.print("输入6位数字旧密码 ");
					int new_f_pass_input = 3;
					int new_f_password = 0;
					int new_pass_count = 3;
					while (true) {
						try {
							new_f_password = sc.nextInt();
						} catch (InputMismatchException e) {
							System.out.print("请输入六位数字密码  ");
							String input = sc.next();
							Pattern pattern = Pattern.compile("[0-9]*");
							boolean digit = pattern.matcher(input).matches();
							if (digit) {
								break;
							} else
								continue;
						}
						break;
					}
					if (admin_password_old == new_f_password) {
						while (true) {
							System.out.println("新的管理员密码：");
							int new_f_admin_password = 0;
							while (true) {
								try {
									new_f_admin_password = sc.nextInt();
								} catch (InputMismatchException e) {
									System.out.print("请重新输入六位数字管理密码： ");
									String input1 = sc.next();
									Pattern pattern = Pattern.compile("[0-9]*");
									boolean digit = pattern.matcher(input1).matches();
									if (digit) {
										break;
									} else
										continue;
								}
								break;
							}
							int len = Integer.toString(new_f_admin_password).length();
							if (len != 6) {
								--new_f_pass_input;
								System.out.println("密码位数错误，剩余次数： " + new_f_pass_input);
								if (new_f_pass_input == 0) {
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
								System.out.println("确认新的管理员密码： ");
								int new_s_admin_password = 0;
								while (true) {
									try {
										new_s_admin_password = sc.nextInt();
									} catch (InputMismatchException e) {
										System.out.print("请重新输入六位数字管理密码：");
										String input1 = sc.next();
										Pattern pattern = Pattern.compile("[0-9]*");
										boolean digit = pattern.matcher(input1).matches();
										if (digit) {
											break;
										} else
											continue;
									}
									break;
								}
								if (new_s_admin_password == new_f_admin_password) {
									cs.setInt(1, new_s_admin_password);
									cs.setString(2, admin_card);
									cs.execute();
									System.out.println("修改成功");
									try {
										sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
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
						--admin_try_count_left;
						if (admin_try_count_left == 0) {
							try {
								System.out.println("旧密码输入次数用完，正在退出...");
								sleep(1000);
								break;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							flag = false;
						}
						System.out.println("密码错误，请重新输入" + "  " + "剩余次数" + admin_try_count_left);
						continue;
					}
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// 修改用户密码
	public static void changeUserPassword(Connection conn) {
		int user_card = 0;
		try {
			// 选择账号
			while (true) {
				boolean card = false;
				Scanner console = new Scanner(System.in);
				System.out.println("请输入用户账号：");
				while (true) {
					try {
						user_card = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确类型的账号：");
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

				Statement st = conn.createStatement();
				st.execute("select * from user");
				ResultSet rs = st.getResultSet();
				while (rs.next()) {
					if (user_card == rs.getInt(1)) {
						card = true;
						break;
					} else {
						card = false;
					}
				}
				if (card) {
					// 修改密码
					boolean b = st.execute("select user_password from user where user_card = " + user_card + "");
					if (b) {
						ResultSet rs2 = st.getResultSet();
						rs2.next();
						int oldPassword = rs2.getInt("user_password");
						boolean user_flag = true;
						int login_count = 3;
						int new_pass_count = 3;
						int new_one_pass = 3;
						while (user_flag) {
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
									} else
										continue;
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
											} else
												continue;
										}
										break;
									}
									int len = Integer.toString(newpassword).length();
									if (len < 6) {
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
												} else
													continue;
											}
											break;
										}
										if (newpassword == newpassword2) {
											st.execute("update user set user_password='" + newpassword
													+ "' where user_card = " + user_card + "");
											System.out.println("修改成功");
											user_flag = false;
											break;
										} else {
											System.out.println("密码两次不相同");
											--new_pass_count;
											if (new_pass_count == 0) {
												user_flag = false;
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
										break;
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									user_flag = false;
								}
								System.out.println("密码错误，请重新输入" + "  " + "剩余次数" + login_count);
								continue;
							}
							break;
						}
					}
				} else {
					System.out.println("用户卡号错误");
					continue;
				}
				System.out.println("是否继续修改用户密码:Y/任意键退出？");
				Scanner sc = new Scanner(System.in);
				String inputY = sc.next();
				if (inputY.equalsIgnoreCase("Y")) {
					continue;
				} else
					break;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void select_by_card(Connection conn) {
		while (true) {
			boolean card = false;
			try {
				Scanner console = new Scanner(System.in);
				System.out.println("请输入您要查询的卡号：");
				int user_card = 0;
				while (true) {
					try {
						user_card = console.nextInt();
					} catch (InputMismatchException e) {
						System.out.print("请输入正确类型的卡号：");
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
					System.out.println("正在查询......");
					sleep(1000);
					stmt.execute("select * from user where user_card = '" + user_card + "'");
					rs = stmt.getResultSet();
					rs.next();
					String action;
					if (rs.getInt(5) == 1) {
						action = "正常";
					} else {
						action = "冻结";
					}
					System.out.println("查询成功");
					System.out.println("卡号：" + rs.getInt(1) + "   姓名：" + rs.getString(3) + "   余额：" + rs.getInt(4)
							+ "   账户状态：" + action);
					System.out.println("是否继续查询？Y/任意键退出系统");
					String in_yn = console.next();
					if (in_yn.equalsIgnoreCase("Y")) {
						System.out.println("正在重新载入查询系统......");
						sleep(1000);
						continue;
					} else {
						System.out.println("正在退出查询系统......");
						sleep(1000);
						break;
					}
				} else {
					System.out.println("账号不存在");
					System.out.println("是否继续查询？Y/任意键退出系统");
					String in_yn = console.next();
					if (in_yn.equalsIgnoreCase("Y")) {
						System.out.println("正在重新载入查询系统......");
						sleep(1000);
						continue;
					} else {
						System.out.println("正在退出查询系统......");
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

	// 修改主菜单
	public static void menuChangePwd(Connection conn) {
		while (true) {
			System.out.println("*--------密码修改 -------");
			System.out.println("*-1、修改当前管理员密码 ");
			System.out.println("*-2、修改用户密码");
			System.out.println("*-3、修改用户状态");
			System.out.println("*-4、返回上一级");

			System.out.println();
			System.out.print("输入要选择的业务：");
			Scanner sc = new Scanner(System.in);
			int n = sc.nextInt();
			switch (n) {
				case 1:
					changeAdminPassword(conn);
					continue;
				case 2:
					changeUserPassword(conn);
					continue;
				case 3:
					change_action(conn);
					continue;
				case 4:
					System.out.println("正在退出密码修改系统......");
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("输入指令有误，请重新输入");
					continue;
			}
			break;
		}
	}

	// 销户
	public static void delete_user(Connection conn) {
		boolean card = false;
		while (true) {
			System.out.println("请输入销户的账户");
			Scanner sc = new Scanner(System.in);
			int user_card = 0;
			boolean isDigit = true;
			while (isDigit) {
				try {
					user_card = sc.nextInt();
				} catch (InputMismatchException e) {
					System.out.print("请输入六位数字卡号:");
					String input = sc.next();
					Pattern pattern = Pattern.compile("[0-9]*");
					boolean digit = pattern.matcher(input).matches();
					if (digit) {
						break;
					} else
						continue;
				}
				break;
			}
			try {
				Statement st = conn.createStatement();
				st.execute("select * from user");
				ResultSet rs = st.getResultSet();
				while (rs.next()) {
					if (user_card == rs.getInt(1)) {
						card = true;
						break;
					}
				}
				if (card) {
					while (true) {
						System.out.println("请输入密码:");
						Scanner sc2 = new Scanner(System.in);
						int user_password = 0;
						boolean isDigit2 = true;
						while (isDigit2) {
							try {
								user_password = sc2.nextInt();
							} catch (InputMismatchException e) {
								System.out.print("请输入六位数字密码:");
								String input = sc.next();
								Pattern pattern = Pattern.compile("[0-9]*");
								boolean digit = pattern.matcher(input).matches();
								if (digit) {
									break;
								} else
									continue;
							}
							break;
						}
						Statement st2 = conn.createStatement();
						st2.execute("select user_password from user where user_card =" + user_card);
						ResultSet rs2 = st2.getResultSet();
						rs2.next();
						int password = rs2.getInt(1);
						if (user_password == password) {
							while (true) {
								System.out.println("正在查找账户......");
								sleep(1000);
								System.out.println("找到账户");
								st2.execute("select * from user where user_card = " + user_card);
								rs2 = st2.getResultSet();
								rs2.next();
								String action;
								if (rs2.getInt(5) == 1) {
									action = "正常";
								} else {
									action = "冻结";
								}
								System.out.println("卡号：" + rs2.getInt(1) + "   姓名：" + rs2.getString(3) + "   余额："
										+ rs2.getInt(4) + "   状态：" + action);
								System.out.println("是否确认销户：Y/任意键退出");
								Scanner console = new Scanner(System.in);
								String in_yn = console.next();
								if (in_yn.equalsIgnoreCase("Y")) {
									System.out.println("正在销户......");
									sleep(1000);
									Statement st3 = conn.createStatement();
									st3.execute("delete from user where user_card =" + user_card + "");
									System.out.println("销户成功");
									break;
								} else {
									System.out.println("正在退出");
									sleep(1000);
									break;
								}
							}
						} else {
							System.out.println("密码错误，重新输入");
							continue;
						}
						break;
					}
					Scanner console = new Scanner(System.in);
					System.out.println("是否继续销户？Y/任意键退出");
					String in_yn = console.next();
					if (in_yn.equalsIgnoreCase("Y")) {
						System.out.println("正在重新载入......");
						sleep(1000);
						continue;
					} else {
						System.out.println("正在退出系统......");
						sleep(1000);
						break;
					}
				} else {
					System.out.println("请重新输入正确卡号");
					continue;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
