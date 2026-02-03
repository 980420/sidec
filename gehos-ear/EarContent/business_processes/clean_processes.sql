
BEGIN
delete from public.jbpm_log;
delete from public.jbpm_variableinstance;
delete from public.jbpm_taskactorpool;
delete from public.jbpm_taskinstance;
delete from public.jbpm_task;
delete from public.jbpm_task;
delete from public.jbpm_tokenvariablemap;
delete from public.jbpm_moduleinstance;
update public.jbpm_token set processinstance_ = null;
delete from public.jbpm_processinstance;
delete from public.jbpm_token;
delete from public.jbpm_transition;
update public.jbpm_delegation set processdefinition_ = null;
update public.jbpm_node set decisiondelegation = null;
update public.jbpm_node set action_ = null;
update public.jbpm_action set actiondelegation_ = null;
delete from public.jbpm_action;
delete from public.jbpm_delegation;
delete from public.jbpm_moduledefinition;
update public.jbpm_node set processdefinition_ = null;
update public.jbpm_processdefinition set startstate_ = null;
delete from public.jbpm_event;
delete from public.jbpm_node;
delete from public.jbpm_processdefinition;
delete from comun.proceso_desplegado;
END;
