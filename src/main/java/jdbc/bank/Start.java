package jdbc.bank;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Start {
    static Connection conn = null;

    public static void main(String[] args) {
        Conn_Start();
    }

    public static void menu() {
        System.out.println("-----欢迎光临-----");
        System.out.println("1、进入管理端");
        System.out.println("2、进入终端");
        System.out.println("3、退出系统");
        System.out.println("请输入指令进行操作：");
    }

    public static void Conn_Start() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Scanner console = new Scanner(System.in);
                System.out.println("请输入数据库地址：");
                String ip;
                while (true) {
                    ip = console.next();
                    String reg = "\\d+\\.\\d+\\.\\d+\\.\\d+";
                    if (!(ip.matches(reg))) {
                        System.out.println("请输入正确的IP地址：");
                        continue;
                    } else {
                        break;
                    }
                }
                System.out.println("正在连接数据库.....");
                Thread.sleep(1000);
                conn = JdbcConn.getConnection(ip);
                while (true) {
                    menu();
                    int in_num = 0;
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
                        case 1:
                            System.out.println("正在进入管理端......");
                            Thread.sleep(1000);
                            AdminStart.AdminBegin(conn);
                            continue;
                        case 2:
                            System.out.println("正在进入终端......");
                            Thread.sleep(1000);
                            UserStart.UserBegin(conn);
                            continue;
                        case 3:
                            System.out.println("正在退出......");
                            Thread.sleep(1000);
                            System.out.println("再见~~~~~~~");
                            break;
                        default:
                            System.out.println("输入指令有误，请重新输入：");
                            continue;
                    }
                    break;
                }
                break;
            } catch (CommunicationsException e) {
                System.out.println("数据库地址输入有误");
                continue;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}