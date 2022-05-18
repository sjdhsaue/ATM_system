CREATE
DEFINER=`all`@`%` PROCEDURE `bank_acounts`(in one int,in two int,in money int)
BEGIN

	declare
one_action int default 0;

	declare
two_action int default 0;

	declare
one_id int default 0;

	declare
two_id int default 0;

	declare
m int default 0;

	declare
continue handler for SQLEXCEPTION ROLLBACK;

start transaction;

SELECT user_card
into one_id
from user
where user_card = one;

SELECT user_card
into two_id
from user
where user_card = two;

if
(one_id = two_id)

	then

SELECT "相同账户不能转账";

else

SELECT user_action
into two_action
from user
where user_card = two;

if
(two_action = 1)

		then

SELECT user_money
into m
from user
where user_card = one;

if
(m-money>0)

			then

update user
set user_money = m - money
where user_card = one;

SELECT user_money
into m
from user
where user_card = two;

update user
set user_money = m + money
where user_card = two;

SELECT "转账成功";

else

SELECT "对不起，您的账户余额不足";

END if;

else

SELECT "对方的账户被冻结";

end if;

COMMIT;

end if;

END ;;

CREATE
DEFINER=`all`@`%` PROCEDURE `changeadminpwd`(in passwd int,in id varchar(128) )
begin
update admin
set admin_password=passwd
where admin_id = id;
end ;;

CREATE
DEFINER=`all`@`%` PROCEDURE `decrease_m`(in money int,out rs varchar(128))
begin 
declare
exist_money int default 0;
declare
all_money int default 0;
declare
continue handler for SQLEXCEPTION ROLLBACK;
start transaction;
select atm_money
into exist_money
from atm
where atm_id = 1;
if
money>0 then
if exist_money>=money  then
set all_money=exist_money-money;
update atm
set atm_money=all_money
where atm_id = 1;
set
rs = '退钱成功';
select rs;
else
set rs= 'ATM内存款不足';
select rs;
end if;
else 
set rs = '金额不能小于0';
end if;
commit;
end ;
CREATE
DEFINER=`all`@`%` PROCEDURE `increase_m`(in money int,out rs varchar(128))
begin 

declare
all_money int default 0;

declare
exist_money int default 0;

declare
continue handler for SQLEXCEPTION ROLLBACK;

start transaction;

select atm_money
into exist_money
from atm
where atm_id = 1;

set
all_money = money + exist_money;
if
money>0 then

if all_money > 1000000 then

set rs ='总现金超过容纳量';

select rs;

else

update atm
set atm_money=all_money
where atm_id = 1;

set
rs='存入成功';

select rs;
end if;
else 
set rs = '金额不能小于0';

end if;

commit;

end ;
CREATE
DEFINER=`all`@`%` PROCEDURE `insert_admin`(in admin_id varchar(128),in pass int)
BEGIN
insert into admin
values (admin_id, pass);
END ;;
 DEFINER
=`all`@`%` PROCEDURE `insert_user`(in user_card int,in user_password int,in user_name varchar(128),in user_money int,in user_action int)
begin
insert into user
values (user_card, user_password, user_name, user_money, user_action);
end ;
CREATE
DEFINER=`all`@`%` PROCEDURE `save`(in u_card int,in s_money int,out rs varchar(128))
begin

declare
atm_lit int default 10000;

declare
exist_money int default 0;

declare
exit handler for sqlexception rollback;



if
(s_money <= atm_lit && s_money%100=0)

then

select atm_money
into exist_money
from atm
where atm_id = 1;

if
(s_money+exist_money<=1000000)

then

update user
set user_money = user_money + s_money
where user_card = u_card;

update atm
set atm_money = atm_money + s_money
where atm_id = 1;

set
rs = '存款成功';

select rs;

else 

set rs = '超过ATM容纳量';

end if;

elseif
(s_money%100 != 0)

then

set rs = '存款只能存100倍数的现金';

select rs;

else

set rs = '存款金额大于存款额度';

select rs;

end if;

commit;

end ;
CREATE
DEFINER=`all`@`%` PROCEDURE `user_delete`(in u_card int, out print varchar(128))
begin
declare
exit handler for sqlexception rollback;
start transaction;
delete
from user
where user_card = u_card;
set
print = '删除账户成功';
select print;
commit;
end ;;
CREATE
DEFINER=`all`@`%` PROCEDURE `user_remove`(in u_card int,in u_money int,out print varchar(128))
begin

declare
atm_lit int default 10000;
declare
a int default 0;
declare
b int default 0;

set
@a = (select user_money from user where user_card = u_card);
set
@b = (select atm_money from atm where atm_id = 1);
if
(u_money>0) then
if(u_money%100=0) then
if(u_money>atm_lit) then 
  set  print = '取款额度超额，请输入10000元以下';
select print;
else if(u_money>@a) then
  set print = '银行卡余额不足';
select print;
else if(u_money>@b) then
  set print = 'ATM机余额不足';
select print;
else
update user
set user_money=@a - u_money
where user_card = u_card;
update atm
set atm_money=@b - u_money
where atm_id = 1;
set
print = '取款成功';
select print;
end if;
end if;
end if;
else
set print = '请取出100的整数';
select print;
end if;
else
set print = '输入不正确，不能输入负数';
select print;
end if;

end ;



