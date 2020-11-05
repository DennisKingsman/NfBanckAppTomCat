select * from xml_audit;
insert into xml_audit(user_id, recording_date, audit) values (1, current_date, createInitXml(1, 'Den'));
truncate xml_audit;

drop function createInitXml;
create or replace function createInitXml(
	u_Id integer,
	u_Login text
) returns xml as
$BODY$

declare res xml;
begin
	select xmlelement(name audit, xmlattributes('en' as lang),
					 xmlelement(name user, xmlattributes(u_Id::text as userId, u_Login as login),
							   xmlelement(name action))) into res;
							   return res;
end
$BODY$
Language plpgsql
cost 100;

--
select * from xml_audit;

select initBillStatusAudit(1, 1, 'REPLACEMENT', 'CLOSE');
select initBillStatusAudit(1, 1, 'INITIALISATION', 'OPEN');
create or replace function initBillStatusAudit(userId integer, billId integer, actionName text, billStatus text)
returns void as
$BODY$
	declare selectedXml text;

	begin

	select unnest(xpath('//audit', audit))::text into selectedXml from xml_audit where user_id = userId;
	update xml_audit set audit = xml(replace(selectedXml::text, '<action/>', xmlconcat(
											xmlelement(name action, xmlattributes(billId::text as billId, actionName as name, current_date::text as cdate),
													  xmlelement(name status, billStatus)),
											xmlelement(name action)
											)::text
									)) where user_id = userId;

	end
$BODY$
language plpgsql volatile
cost 100;

--
select transferMoney(1, 1, 999.9, 'INCREASE');
select transferMoney(1, 1, 500.55, 'INCREASE', 3, 'WITHDRAW');
create or replace function transferMoney(
	userId integer,
	billToId integer,
	amount numeric,
	actionTo text,
	billFromId integer default null,
	actionFrom text default null
)
returns void as
$BODY$
	declare selectedXml text;
	declare transferToAuditNode xml;
	declare transferFromAuditNode xml;
	declare cDate date = current_date;
	begin
	select unnest(xpath('//audit', audit))::text into selectedXml from xml_audit where user_id = userId;
	select xmlelement(name action, xmlattributes(billToId::text as billId, actionTo as name, cDate::text as cdate),
											xmlelement(name transfer, xmlattributes(billToId::text as to, billFromId::text as from), amount::text)) into transferToAuditNode;
	if billFromId is not null then
		select xmlelement(name action, xmlattributes(billFromId::text as billId, actionFrom as name, cDate::text as cdate),
						 						xmlelement(name transfer, xmlattributes(billToId::text as to, billFromId::text as from), amount::text)) into transferFromAuditNode;

		update xml_audit set audit = xml(replace(selectedXml::text, '<action/>', xmlconcat(
				xmlconcat(transferToAuditNode, transferFromAuditNode), xmlelement(name action))::text )) where user_id = userId;
	else
		update xml_audit set audit = xml(replace(selectedXml::text, '<action/>', xmlconcat(transferToAuditNode, xmlelement(name action))::text )) where user_id = userId;
	end if;
	end

$BODY$
language plpgsql volatile
cost 100;