select * from xml_test;

select unnest(xpath('status/text()', xml_test_f)) from xml_test where id = 8;

delete from xml_test where id = 7;

select unnest(xpath('//foo/text()', xml_test_f))::text as xml_text from xml_test;

delete from xml_test where id in (12, 13);

select unnest(xpath('//status/text()', xml_test_f))::text as res from xml_test;

select unnest(xpath('//User', xml_test_f))::xml from xml_test where user_id = 1;

select unnest(xpath('//User', xml_test_f))::text as res from xml_test where user_id = 1;

insert into xml_test(xml_test_f) values (xmlelement(name Status, xmlattributes(21 as testStatusId), 'CLOSED'));

insert into xml_test(xml_test_f) values (xmlconcat(xmlelement(name Action, xmlattributes('REPLACEMENT' as name, 22 as billId)),
												  xmlelement(name Status, 'SUSPENDED')));

insert into xml_test(user_id, xml_test_f) values (1, xmlelement(name Action, xmlattributes('REPLACEMENT' as name, 22 as billId), xmlelement(name Status, 'SUSPENDED')));

select xml_test_f from xml_test where user_id = 1;

select * from xml_test;

insert into xml_test(user_id, xml_test_f) values (1, xmlelement(name foo, 'Hello'));

select * from xml_test where user_id = 1;

update TABLE NAME set  COL NAME=xml(REPLACE(OLDXML::text,'</NODE NAME>', NEW VALUE TO UPDATE ||'</NODE NAME>')) where name='xxxx'

update xml_test set xml_test_f = xml(replace())

select unnest(xpath('//status/text()', xml_test_f))::text as res, id from xml_test;

select unnest(xpath('/status/text()', xml_test_f)), id from xml_test;
select * from xml_test where id = 3;
select unnest(xpath('//status/text()', xml_test_f)) from xml_test where id = 3;
update xml_test set xml_test_f = xmlelement(name Audit,
											xmlelement(name User, xmlattributes(1 as userId, 'user' as login),
													   xmlconcat(
													   xmlelement(name Action, xmlattributes(22 as billId, 'REPLACEMENT' as name),
																 xmlelement(name Status, 'CLOSED')),
													   xmlelement(name Action)))) where id = 3;

select unnest(xpath('//action', xml_test_f)), id from xml_test;

select unnest(xpath('//action[1]', xml_test_F)) from xml_test;
insert into xml_test(xml_test_f, user_id) values (xmlparse(CONTENT $$<action billid = '1' name = 'REPLACE'>
														   				<status>CLOSED</status>
														  			</action>$$), 4);