-- =============================================
-- PROJETO NORA - Turma do Bem
-- Banco de Dados Relacional - Oracle SQL DDL
-- Sprint 4 - Atualizado
-- =============================================
-- Diego Costa Paulino - RM: 566841
-- Guilherme Alves Dabul - RM: 559901
-- Ryan Vieira de Aguiar Mazali - RM: 567168
-- =============================================

-- =============================================
-- Apagando as tabelas
-- =============================================

drop table mensagem cascade constraints;
drop table conversa cascade constraints;
drop table documento cascade constraints;
drop table acomp_evento cascade constraints;
drop table encaminhamento cascade constraints;
drop table dentista_especialidade cascade constraints;
drop table especialidade cascade constraints;
drop table paciente cascade constraints;
drop table triagem cascade constraints;
drop table pessoa cascade constraints;
drop table dentista cascade constraints;
drop table usuario cascade constraints;
drop table colaborador cascade constraints;
drop table perfil cascade constraints;
drop table endereco cascade constraints;

-- =============================================
-- Criando as tabelas
-- =============================================

-- Tabela: endereco
create table endereco
(id_end      number(8)     constraint end_id_pk primary key,
 logradouro  varchar2(200) constraint end_log_nn not null,
 numero      varchar2(10)  constraint end_num_nn not null,
 complemento varchar2(100),
 bairro      varchar2(100) constraint end_bai_nn not null,
 cidade      varchar2(100) constraint end_cid_nn not null,
 uf          char(2)       constraint end_uf_nn not null,
 cep         char(8)       constraint end_cep_nn not null,
             constraint end_uf_ck check (uf in ('AC','AL','AP','AM','BA','CE','DF','ES','GO',
                                                 'MA','MT','MS','MG','PA','PB','PR','PE','PI',
                                                 'RJ','RN','RS','RO','RR','SC','SP','SE','TO')));

-- Tabela: perfil
create table perfil
(id_perfil   number(2)    constraint perf_id_pk primary key,
 nm_perfil   varchar2(50) constraint perf_nm_nn not null,
 ds_perfil   varchar2(255),
             constraint perf_nm_uk unique (nm_perfil),
             constraint perf_nm_ck check (nm_perfil in (
               'atendente',
               'coordenador',
               'supervisor',
               'voluntario',
               'gestor',
               'analista',
               'administrador'
             )));

-- Tabela: colaborador
create table colaborador
(id_colab    number(6)     constraint colab_id_pk primary key,
 nm_colab    varchar2(150) constraint colab_nm_nn not null,
 cpf_colab   char(11)      constraint colab_cpf_nn not null,
 email_colab varchar2(150),
 cargo_colab varchar2(100),
 dt_entrada  date          constraint colab_dt_nn not null,
 stts_colab  varchar2(20)  default 'ativo' constraint colab_st_nn not null,
 fk_end_id   number(8)     constraint colab_end_fk references endereco,
             constraint colab_cpf_uk unique (cpf_colab),
             constraint colab_email_uk unique (email_colab),
             constraint colab_st_ck check (stts_colab in ('ativo', 'inativo')));

-- Tabela: usuario
create table usuario
(id_user     number(6)     constraint user_id_pk primary key,
 nm_login    varchar2(100) constraint user_login_nn not null,
 senha_hash  varchar2(255) constraint user_senha_nn not null,
 stts_user   varchar2(20)  default 'ativo' constraint user_st_nn not null,
 dt_criacao  date          constraint user_dtcri_nn not null,
 dt_acesso   date,
 fk_colab_id number(6)     constraint user_colab_fk references colaborador,
 fk_perf_id  number(2)     constraint user_perf_fk references perfil,
             constraint user_login_uk unique (nm_login),
             constraint user_st_ck check (stts_user in ('ativo', 'inativo')));

-- Tabela: pessoa
-- ATUALIZADO: adicionado dt_nasc, email_pess, tg_chat_id
create table pessoa
(id_pess     number(8)     constraint pess_id_pk primary key,
 nm_pess     varchar2(150) constraint pess_nm_nn not null,
 cpf_pess    char(11),
 rg_pess     varchar2(20),
 tel_pess    varchar2(20),
 email_pess  varchar2(150),
 dt_nasc     date          constraint pess_dtnsc_nn not null,
 tg_chat_id  varchar2(50),
 canal_orig  varchar2(30)  constraint pess_canal_nn not null,
 dt_cad      date          constraint pess_dtcad_nn not null,
 stts_pess   varchar2(30)  default 'em_triagem' constraint pess_st_nn not null,
 fk_end_id   number(8)     constraint pess_end_fk references endereco,
             constraint pess_cpf_uk unique (cpf_pess),
             constraint pess_canal_ck check (canal_orig in ('whatsapp', 'instagram', 'facebook', 'telegram', 'outro')),
             constraint pess_st_ck check (stts_pess in ('em_triagem', 'aprovada', 'encerrada', 'inelegivel')));

-- Tabela: dentista
-- ATUALIZADO: adicionado tg_chat_id
create table dentista
(id_dent     number(8)     constraint dent_id_pk primary key,
 nm_dent     varchar2(150) constraint dent_nm_nn not null,
 cro_dent    varchar2(20)  constraint dent_cro_nn not null,
 tel_dent    varchar2(20),
 email_dent  varchar2(150),
 tg_chat_id  varchar2(50),
 cap_mensal  number(3)     default 0 constraint dent_cap_nn not null,
 stts_dent   varchar2(30)  default 'ativo' constraint dent_st_nn not null,
 dt_cred     date          constraint dent_dtcred_nn not null,
 fk_end_id   number(8)     constraint dent_end_fk references endereco,
             constraint dent_cro_uk unique (cro_dent),
             constraint dent_email_uk unique (email_dent),
             constraint dent_st_ck check (stts_dent in ('ativo', 'inativo', 'suspenso')),
             constraint dent_cap_ck check (cap_mensal >= 0));

-- Tabela: especialidade
create table especialidade
(id_espec    number(4)     constraint espec_id_pk primary key,
 nm_espec    varchar2(100) constraint espec_nm_nn not null,
 ds_espec    varchar2(255),
             constraint espec_nm_uk unique (nm_espec));

-- Tabela: dentista_especialidade
create table dentista_especialidade
(fk_dent_id  number(8)    constraint denesp_dent_fk references dentista,
 fk_espec_id number(4)    constraint denesp_espec_fk references especialidade,
             constraint denesp_pk primary key (fk_dent_id, fk_espec_id));

-- Tabela: triagem
-- ATUALIZADO: adicionado sexo_pess, problema_bucal, renda_familiar,
--             nivel_urg_ia, conf_ia e 'inativa' no check de stts_triag
create table triagem
(id_triag       number(8)     constraint triag_id_pk primary key,
 eleg_triag     varchar2(30)  constraint triag_eleg_nn not null,
 prior_triag    varchar2(20)  constraint triag_prior_nn not null,
 sexo_pess      char(1),
 problema_bucal varchar2(200),
 renda_familiar varchar2(20),
 nivel_urg_ia   number(3,1),
 conf_ia        number(5,2),
 sugestao_ia    varchar2(500),
 obs_triag      varchar2(1000),
 dt_triag       date          constraint triag_dt_nn not null,
 stts_triag     varchar2(30)  default 'em_analise' constraint triag_st_nn not null,
 decisao        varchar2(30),
 fk_pess_id     number(8)     constraint triag_pess_fk references pessoa,
 fk_user_id     number(6)     constraint triag_user_fk references usuario,
             constraint triag_eleg_ck check (eleg_triag in ('elegivel', 'inelegivel', 'pendente')),
             constraint triag_prior_ck check (prior_triag in ('baixa', 'media', 'alta', 'urgente')),
             constraint triag_sexo_ck check (sexo_pess in ('M', 'F')),
             constraint triag_renda_ck check (renda_familiar in ('ate_1sm', '1_3sm', 'acima_3sm')),
             constraint triag_st_ck check (stts_triag in ('em_analise', 'aprovada', 'encerrada', 'inativa')),
             constraint triag_dec_ck check (decisao in ('aprovado', 'encerrado', 'reanalise')));

-- Tabela: paciente
create table paciente
(id_pac       number(8)    constraint pac_id_pk primary key,
 dt_aprov     date         constraint pac_dtaprov_nn not null,
 stts_trat    varchar2(30) default 'aguardando' constraint pac_st_nn not null,
 id_triag_ref number(8),
 fk_pess_id   number(8)    constraint pac_pess_fk references pessoa,
             constraint pac_pess_uk unique (fk_pess_id),
             constraint pac_st_ck check (stts_trat in ('aguardando', 'em_tratamento', 'concluido', 'abandonou', 'reencaminhado')));

-- Tabela: encaminhamento
create table encaminhamento
(id_encam    number(8)     constraint encam_id_pk primary key,
 dt_encam    date          constraint encam_dt_nn not null,
 prior_encam varchar2(20)  constraint encam_prior_nn not null,
 obs_encam   varchar2(1000),
 prev_follow date,
 stts_encam  varchar2(30)  default 'ativo' constraint encam_st_nn not null,
 match_auto  char(1)       default 'N'     constraint encam_match_nn not null,
 dist_km     number(6,2),
 fk_pac_id   number(8)     constraint encam_pac_fk references paciente,
 fk_dent_id  number(8)     constraint encam_dent_fk references dentista,
 fk_user_id  number(6)     constraint encam_user_fk references usuario,
             constraint encam_prior_ck check (prior_encam in ('baixa', 'media', 'alta', 'urgente')),
             constraint encam_st_ck check (stts_encam in ('ativo', 'concluido', 'cancelado', 'reencaminhado')),
             constraint encam_match_ck check (match_auto in ('S', 'N')),
             constraint encam_follow_ck check (prev_follow > dt_encam));

-- Tabela: acomp_evento
create table acomp_evento
(id_acomp    number(8)      constraint acomp_id_pk primary key,
 tp_evento   varchar2(50)   constraint acomp_tp_nn not null,
 ds_evento   varchar2(1000),
 origem      varchar2(30)   constraint acomp_orig_nn not null,
 dt_evento   date           constraint acomp_dt_nn not null,
 resumo_ia   varchar2(2000),
 tp_mensagem varchar2(20)   default 'texto',
 fk_encam_id number(8)      constraint acomp_encam_fk references encaminhamento,
 fk_user_id  number(6)      constraint acomp_user_fk references usuario,
             constraint acomp_orig_ck check (origem in ('paciente', 'dentista', 'ia', 'atendente', 'sistema')),
             constraint acomp_tp_ck check (tp_evento in ('primeira_consulta', 'atualizacao', 'abandono',
                                                          'conclusao', 'reencaminhamento', 'followup', 'outro')),
             constraint acomp_tmens_ck check (tp_mensagem in ('texto', 'audio')),
             constraint acomp_user_ck check (
               (origem = 'ia' and fk_user_id is null) or
               (origem != 'ia')
             ));

-- Tabela: conversa
-- ATUALIZADO: adicionado fk_pess_id (lead antes de virar paciente)
--             e tg_thread_id (contexto da conversa no Telegram)
--             conv_lado_ck atualizado para aceitar pessoa OU paciente OU dentista
create table conversa
(id_conv      number(8)    constraint conv_id_pk primary key,
 canal_conv   varchar2(30) constraint conv_canal_nn not null,
 contexto     varchar2(50) constraint conv_ctx_nn not null,
 iniciado_por varchar2(20) constraint conv_ini_nn not null,
 dt_inicio    date         constraint conv_dt_nn not null,
 stts_conv    varchar2(20) default 'aberta' constraint conv_st_nn not null,
 tg_thread_id varchar2(100),
 fk_pess_id   number(8)    constraint conv_pess_fk references pessoa,
 fk_pac_id    number(8)    constraint conv_pac_fk references paciente,
 fk_dent_id   number(8)    constraint conv_dent_fk references dentista,
 fk_user_id   number(6)    constraint conv_user_fk references usuario,
             constraint conv_canal_ck check (canal_conv in ('whatsapp', 'instagram', 'facebook', 'telegram')),
             constraint conv_st_ck check (stts_conv in ('aberta', 'encerrada')),
             constraint conv_ctx_ck check (contexto in ('acomp_paciente', 'acomp_dentista', 'cadastro')),
             constraint conv_ini_ck check (iniciado_por in ('usuario', 'nora_ia')),
             constraint conv_lado_ck check (
               (fk_pess_id is not null and fk_pac_id is null and fk_dent_id is null) or
               (fk_pac_id is not null and fk_pess_id is null and fk_dent_id is null) or
               (fk_dent_id is not null and fk_pess_id is null and fk_pac_id is null)
             ),
             constraint conv_user_ck check (
               (iniciado_por = 'usuario' and fk_user_id is not null) or
               (iniciado_por = 'nora_ia' and fk_user_id is null)
             ));

-- Tabela: mensagem
create table mensagem
(id_mens     number(10)     constraint mens_id_pk primary key,
 enviado_por varchar2(20)   constraint mens_env_nn not null,
 direcao     varchar2(10)   constraint mens_dir_nn not null,
 conteudo    varchar2(4000) constraint mens_cont_nn not null,
 tp_mens     varchar2(20)   default 'texto' constraint mens_tp_nn not null,
 dt_envio    date           constraint mens_dt_nn not null,
 fk_conv_id  number(8)      constraint mens_conv_fk references conversa,
             constraint mens_env_ck check (enviado_por in ('usuario', 'nora_ia', 'externo')),
             constraint mens_dir_ck check (direcao in ('entrada', 'saida')),
             constraint mens_tp_ck check (tp_mens in ('texto', 'audio', 'imagem', 'documento')));

-- Tabela: documento
create table documento
(id_doc      number(8)     constraint doc_id_pk primary key,
 tp_doc      varchar2(50)  constraint doc_tp_nn not null,
 url_doc     varchar2(500) constraint doc_url_nn not null,
 stts_valid  varchar2(30)  default 'pendente' constraint doc_st_nn not null,
 result_ia   varchar2(1000),
 dt_envio    date          constraint doc_dt_nn not null,
 fk_pess_id  number(8)     constraint doc_pess_fk references pessoa,
 fk_dent_id  number(8)     constraint doc_dent_fk references dentista,
             constraint doc_tp_ck check (tp_doc in ('cpf', 'rg', 'comprovante_renda',
                                                     'comprovante_residencia', 'cro',
                                                     'cnpj', 'comprovante_consultorio', 'outro')),
             constraint doc_st_ck check (stts_valid in ('pendente', 'aprovado', 'reprovado', 'em_analise')),
             constraint doc_orig_ck check (
               (fk_pess_id is not null and fk_dent_id is null) or
               (fk_dent_id is not null and fk_pess_id is null)
             ));
             
-- =============================================
-- INSERTS — 7 por tabela
-- Ordem respeitando FKs
-- =============================================

-- endereco (7)
insert into endereco values (1, 'Rua Bresser', '120', null, 'Brás', 'São Paulo', 'SP', '03310000');
insert into endereco values (2, 'Av. Paulista', '1000', 'Apto 52', 'Bela Vista', 'São Paulo', 'SP', '01310100');
insert into endereco values (3, 'Rua Vergueiro', '500', null, 'Vila Mariana', 'São Paulo', 'SP', '04038000');
insert into endereco values (4, 'Rua Cardeal Arcoverde', '200', null, 'Perdizes', 'São Paulo', 'SP', '05010000');
insert into endereco values (5, 'Av. Cruzeiro do Sul', '700', null, 'Santana', 'São Paulo', 'SP', '02010000');
insert into endereco values (6, 'Rua Itaquera', '340', null, 'Zona Leste', 'São Paulo', 'SP', '08210000');
insert into endereco values (7, 'Rua Augusta', '800', 'Sala 3', 'Consolação', 'São Paulo', 'SP', '01304000');
select * from endereco;
commit;

-- perfil (7)
insert into perfil values (1, 'atendente', 'Responsável pelo atendimento direto aos leads');
insert into perfil values (2, 'coordenador', 'Coordena a equipe operacional da ONG');
insert into perfil values (3, 'supervisor', 'Supervisiona os atendentes e processos');
insert into perfil values (4, 'voluntario', 'Colaborador voluntário sem vínculo fixo');
insert into perfil values (5, 'gestor', 'Gestor estratégico da plataforma');
insert into perfil values (6, 'analista', 'Responsável pela análise de dados e métricas');
insert into perfil values (7, 'administrador', 'Acesso total ao sistema');
select * from perfil;
commit;

-- colaborador (7)
insert into colaborador values (1, 'Ana Nora', '11122233344', 'ana.nora@turmadobem.org', 'Atendente', date '2023-01-10', 'ativo', 1);
insert into colaborador values (2, 'Bruno Lima', '22233344455', 'bruno.lima@turmadobem.org', 'Coordenador', date '2022-06-15', 'ativo', 2);
insert into colaborador values (3, 'Carla Souza', '33344455566', 'carla.souza@turmadobem.org', 'Atendente', date '2023-03-20', 'ativo', 3);
insert into colaborador values (4, 'Diego Rocha', '44455566677', 'diego.rocha@turmadobem.org', 'Atendente', date '2024-01-05', 'ativo', 4);
insert into colaborador values (5, 'Elisa Pinto', '55566677788', 'elisa.pinto@turmadobem.org', 'Coordenador', date '2021-09-01', 'ativo', 5);
insert into colaborador values (6, 'Fábio Gomes', '66677788899', 'fabio.gomes@turmadobem.org', 'Atendente', date '2024-03-12', 'inativo', 6);
insert into colaborador values (7, 'Gisele Matos', '77788899900', 'gisele.matos@turmadobem.org', 'Atendente', date '2023-08-22', 'ativo', 7);
select * from colaborador;
commit;

-- usuario (7)
insert into usuario values (1, 'ana.nora', 'hash_ana_2024', 'ativo', date '2023-01-10', date '2025-05-10', 1, 1);
insert into usuario values (2, 'bruno.lima', 'hash_bruno_2024', 'ativo', date '2022-06-15', date '2025-05-09', 2, 2);
insert into usuario values (3, 'carla.souza', 'hash_carla_2024', 'ativo', date '2023-03-20', date '2025-05-08', 3, 1);
insert into usuario values (4, 'diego.rocha', 'hash_diego_2024', 'ativo', date '2024-01-05', date '2025-05-07', 4, 1);
insert into usuario values (5, 'elisa.pinto', 'hash_elisa_2024', 'ativo', date '2021-09-01', date '2025-05-06', 5, 2);
insert into usuario values (6, 'fabio.gomes', 'hash_fabio_2024', 'inativo', date '2024-03-12', null, 6, 1);
insert into usuario values (7, 'gisele.matos', 'hash_gisele_2024', 'ativo', date '2023-08-22', date '2025-05-05', 7, 1);
select * from usuario;
commit;

-- pessoa (7)
insert into pessoa values (1, 'Maria Silva', '12345678900', null, '11987654321', 'maria.silva@email.com', date '2008-03-12', '111111111', 'telegram', date '2025-05-10', 'em_triagem', 1);
insert into pessoa values (2, 'João Santos', '98765432100', null, '11912345678', 'joao.santos@email.com', date '2010-07-05', '222222222', 'whatsapp', date '2025-05-09', 'aprovada', 3);
insert into pessoa values (3, 'Ana Costa', '45678912300', null, '11977776666', 'ana.costa@email.com', date '2008-01-20', '333333333', 'telegram', date '2025-05-08', 'aprovada', 6);
insert into pessoa values (4, 'Pedro Oliveira', '32165498700', null, '11955554444', 'pedro.oliveira@email.com', date '2012-09-15', '444444444', 'instagram', date '2025-05-10', 'em_triagem', 4);
insert into pessoa values (5, 'Lucas Ferreira', '65432109800', null, '11933332222', 'lucas.ferreira@email.com', date '2007-11-30', '555555555', 'telegram', date '2025-05-07', 'em_triagem', 5);
insert into pessoa values (6, 'Beatriz Alves', '11223344500', null, '11922221111', 'beatriz.alves@email.com', date '2009-04-18', '666666666', 'whatsapp', date '2025-05-06', 'inelegivel', 2);
insert into pessoa values (7, 'Rafael Costa', '99887766500', null, '11911110000', 'rafael.costa@email.com', date '2008-06-25', '777777777', 'telegram', date '2025-05-05', 'em_triagem', 6);
select * from pessoa;
commit;

-- dentista (7)
insert into dentista values (1, 'Dr. Carlos Mendes', 'CRO-SP 12345', '11988887777', 'carlos.mendes@email.com', '100000001', 4, 'ativo', date '2024-01-15', 2);
insert into dentista values (2, 'Dra. Ana Lima', 'CRO-SP 67890', '11977776666', 'ana.lima@email.com', '200000002', 3, 'ativo', date '2024-03-20', 3);
insert into dentista values (3, 'Dr. Roberto Souza', 'CRO-SP 11223', '11966665555', 'roberto.souza@email.com', '300000003', 5, 'ativo', date '2023-06-10', 5);
insert into dentista values (4, 'Dra. Fernanda Castro', 'CRO-SP 44556', '11955554444', 'fernanda.castro@email.com', '400000004', 3, 'ativo', date '2023-09-05', 4);
insert into dentista values (5, 'Dr. Marcos Pereira', 'CRO-SP 77889', '11944443333', 'marcos.pereira@email.com', '500000005', 4, 'inativo', date '2023-02-12', 6);
insert into dentista values (6, 'Dra. Juliana Ramos', 'CRO-SP 99001', '11933332222', 'juliana.ramos@email.com', '600000006', 5, 'ativo', date '2024-05-01', 7);
insert into dentista values (7, 'Dr. Tiago Barros', 'CRO-SP 22334', '11922221111', 'tiago.barros@email.com', '700000007', 2, 'suspenso', date '2022-11-08', 1);
select * from dentista;
commit;

-- especialidade (7)
insert into especialidade values (1, 'Clínica Geral', 'Atendimento odontológico geral');
insert into especialidade values (2, 'Endodontia', 'Tratamento de canal e polpa dentária');
insert into especialidade values (3, 'Cirurgia', 'Extrações e procedimentos cirúrgicos');
insert into especialidade values (4, 'Traumatologia', 'Tratamento de fraturas dentárias');
insert into especialidade values (5, 'Ortodontia', 'Correção de posicionamento dental');
insert into especialidade values (6, 'Pediatria', 'Odontologia infantil e adolescente');
insert into especialidade values (7, 'Periodontia', 'Tratamento de gengiva e osso alveolar');
select * from especialidade;
commit;

-- dentista_especialidade (7)
insert into dentista_especialidade values (1, 1);
insert into dentista_especialidade values (1, 2);
insert into dentista_especialidade values (2, 3);
insert into dentista_especialidade values (2, 4);
insert into dentista_especialidade values (3, 5);
insert into dentista_especialidade values (3, 1);
insert into dentista_especialidade values (4, 6);
commit;

-- triagem (7)
insert into triagem values (1, 'elegivel', 'alta', 'F', 'Dor intensa e inchaço no rosto', 'ate_1sm', 4.2, 91.00, 'Urgência alta detectada pela IA', null, date '2025-05-10', 'em_analise', null, 1, null);
insert into triagem values (2, 'elegivel', 'media', 'M', 'Cárie com sangramento', '1_3sm', 2.8, 87.00, 'Prioridade média detectada', null, date '2025-05-09', 'aprovada', 'aprovado', 2, 1);
insert into triagem values (3, 'elegivel', 'urgente', 'F', 'Fratura dentária traumática', 'ate_1sm', 4.8, 96.00, 'Urgência crítica detectada pela IA', 'Acidente há menos de 2h', date '2025-05-08', 'aprovada', 'aprovado', 3, 1);
insert into triagem values (4, 'pendente', 'baixa', 'M', 'Dor de dente leve', '1_3sm', 1.5, 82.00, 'Prioridade baixa detectada', null, date '2025-05-10', 'em_analise', null, 4, null);
insert into triagem values (5, 'elegivel', 'alta', 'M', 'Sangramento gengival constante', 'ate_1sm', 3.9, 88.00, 'Prioridade alta detectada pela IA', null, date '2025-05-07', 'em_analise', null, 5, null);
insert into triagem values (6, 'inelegivel', 'baixa', 'F', 'Limpeza de rotina', 'acima_3sm', 1.1, 79.00, 'Fora do critério de elegibilidade', 'Renda acima do limite', date '2025-05-06', 'encerrada', 'encerrado', 6, 2);
insert into triagem values (7, 'elegivel', 'media', 'M', 'Dor ao mastigar', 'ate_1sm', 2.5, 84.00, 'Prioridade média detectada', null, date '2025-05-05', 'em_analise', null, 7, null);
select * from triagem;
commit;

-- paciente (7 — criamos pacientes para todos os aprovados + extras para ter 7)
insert into paciente values (1, date '2025-05-09', 'em_tratamento', 2, 2);
insert into paciente values (2, date '2025-05-08', 'em_tratamento', 3, 3);
insert into paciente values (3, date '2025-05-07', 'aguardando', null, 5);
insert into paciente values (4, date '2025-05-06', 'aguardando', null, 7);
insert into paciente values (5, date '2025-04-20', 'concluido', null, 1);
insert into paciente values (6, date '2025-04-15', 'abandonou', null, 4);
insert into paciente values (7, date '2025-03-10', 'reencaminhado', null, 6);
select * from paciente;
commit;

-- encaminhamento (7)
insert into encaminhamento values (1, date '2025-05-09', 'media', null, date '2025-05-24', 'ativo', 'S', 2.3, 1, 1, 1);
insert into encaminhamento values (2, date '2025-05-08', 'urgente', 'Paciente com dor intensa', date '2025-05-23', 'ativo', 'S', 4.1, 2, 2, 1);
insert into encaminhamento values (3, date '2025-05-07', 'baixa', null, date '2025-05-22', 'ativo', 'S', 1.8, 3, 1, 2);
insert into encaminhamento values (4, date '2025-04-20', 'media', null, date '2025-05-05', 'concluido', 'S', 3.2, 4, 3, 1);
insert into encaminhamento values (5, date '2025-04-15', 'alta', 'Necessário acompanhamento', date '2025-04-30', 'cancelado', 'N', null, 5, 4, 2);
insert into encaminhamento values (6, date '2025-03-10', 'media', null, date '2025-03-25', 'reencaminhado', 'S', 5.6, 6, 2, 1);
insert into encaminhamento values (7, date '2025-05-01', 'alta', null, date '2025-05-16', 'ativo', 'S', 2.9, 7, 6, 2);
select * from encaminhamento;
commit;

-- acomp_evento (7)
insert into acomp_evento values (1, 'primeira_consulta', 'Encaminhamento criado automaticamente via match geográfico', 'sistema', date '2025-05-09', null, 'texto', 1, null);
insert into acomp_evento values (2, 'primeira_consulta', 'Encaminhamento criado com prioridade urgente', 'sistema', date '2025-05-08', null, 'texto', 2, null);
insert into acomp_evento values (3, 'followup', 'Follow-up automático enviado ao dentista via bot', 'ia', date '2025-05-10', 'Dentista confirmou que atendeu o paciente. Consulta realizada com sucesso. Retorno em 30 dias.', 'audio', 2, null);
insert into acomp_evento values (4, 'primeira_consulta', 'Encaminhamento criado automaticamente', 'sistema', date '2025-05-07', null, 'texto', 3, null);
insert into acomp_evento values (5, 'conclusao', 'Tratamento concluído com sucesso', 'atendente', date '2025-05-05', null, 'texto', 4, 1);
insert into acomp_evento values (6, 'abandono', 'Paciente não respondeu aos follow-ups', 'sistema', date '2025-04-30', null, 'texto', 5, null);
insert into acomp_evento values (7, 'followup', 'Follow-up disparado automaticamente após 15 dias', 'ia', date '2025-05-16', null, 'texto', 7, null);
select * from acomp_evento;
commit;

-- conversa (7)
insert into conversa values (1, 'telegram', 'cadastro', 'nora_ia', date '2025-05-10', 'aberta', '111111111', 1, null, null, null);
insert into conversa values (2, 'telegram', 'cadastro', 'nora_ia', date '2025-05-09', 'aberta', '222222222', 2, null, null, null);
insert into conversa values (3, 'telegram', 'acomp_dentista', 'nora_ia', date '2025-05-10', 'aberta', '100000001', null, null, 1, null);
insert into conversa values (4, 'telegram', 'acomp_dentista', 'nora_ia', date '2025-05-08', 'aberta', '200000002', null, null, 2, null);
insert into conversa values (5, 'telegram', 'cadastro', 'nora_ia', date '2025-05-07', 'encerrada', '555555555', 5, null, null, null);
insert into conversa values (6, 'telegram', 'acomp_paciente', 'nora_ia', date '2025-05-09', 'aberta', '333333333', null, 2, null, null);
insert into conversa values (7, 'telegram', 'cadastro', 'nora_ia', date '2025-05-05', 'encerrada', '666666666', 6, null, null, null);
select * from conversa;
commit;

-- mensagem (7)
insert into mensagem values (1, 'nora_ia', 'saida', 'Olá! Sou a NORA, assistente da Turma do Bem. Pode me dizer seu nome completo?', 'texto', date '2025-05-10', 1);
insert into mensagem values (2, 'externo', 'entrada', 'Maria Silva', 'texto', date '2025-05-10', 1);
insert into mensagem values (3, 'nora_ia', 'saida', 'Olá Maria! Quantos anos você tem?', 'texto', date '2025-05-10', 1);
insert into mensagem values (4, 'externo', 'entrada', '17 anos', 'texto', date '2025-05-10', 1);
insert into mensagem values (5, 'nora_ia', 'saida', 'Olá Dr. Carlos! O paciente João Santos compareceu à consulta marcada?', 'texto', date '2025-05-10', 3);
insert into mensagem values (6, 'externo', 'entrada', 'Nota de voz (0:24)', 'audio', date '2025-05-10', 3);
insert into mensagem values (7, 'nora_ia', 'saida', 'Perfeito Dr. Carlos! Registro atualizado. Obrigado pela dedicação!', 'texto', date '2025-05-10', 3);
select * from mensagem;
commit;

-- documento (7)
insert into documento values (1, 'rg', 'https://storage.nora.com/docs/rg_maria.pdf', 'aprovado', null, date '2025-05-10', 1, null);
insert into documento values (2, 'comprovante_renda', 'https://storage.nora.com/docs/renda_maria.pdf', 'aprovado', null, date '2025-05-10', 1, null);
insert into documento values (3, 'cro', 'https://storage.nora.com/docs/cro_carlos.pdf', 'aprovado', 'CRO válido verificado', date '2024-01-15', null, 1);
insert into documento values (4, 'rg', 'https://storage.nora.com/docs/rg_joao.pdf', 'pendente', null, date '2025-05-09', 2, null);
insert into documento values (5, 'comprovante_residencia', 'https://storage.nora.com/docs/res_ana.pdf', 'aprovado', null, date '2025-05-08', 3, null);
insert into documento values (6, 'cro', 'https://storage.nora.com/docs/cro_ana.pdf', 'aprovado', 'CRO válido verificado', date '2024-03-20', null, 2);
insert into documento values (7, 'comprovante_renda', 'https://storage.nora.com/docs/renda_pedro.pdf', 'em_analise', null, date '2025-05-10', 4, null);
select * from documento;
commit;

-- =============================================
-- UPDATES (3 em tabelas diferentes)
-- =============================================

-- UPDATE 1: triagem — inativar triagem encerrada da Beatriz
update triagem
set stts_triag = 'inativa'
where id_triag = 6;
commit;

-- UPDATE 2: dentista — atualizar capacidade mensal do Dr. Marcos (reativação)
update dentista
set cap_mensal = 3,
    stts_dent = 'ativo'
where id_dent = 5;
commit;

-- UPDATE 3: encaminhamento — registrar conclusão do encaminhamento 4
update encaminhamento
set stts_encam = 'concluido',
    obs_encam = 'Tratamento finalizado com sucesso após 3 sessões'
where id_encam = 4;
commit;

-- =============================================
-- DELETES (3 em tabelas diferentes)
-- =============================================

-- DELETE 1: documento — remover documento em análise rejeitado
delete from documento
where id_doc = 7
  and stts_valid = 'em_analise';
commit;

-- DELETE 2: acomp_evento — remover evento duplicado de sistema
delete from acomp_evento
where id_acomp = 6
  and origem = 'sistema';
commit;

-- DELETE 3: mensagem — remover mensagem de teste da conversa encerrada
delete from mensagem
where id_mens = 4
  and fk_conv_id = 1;
commit;

-- =============================================
-- RELATÓRIOS SQL ORACLE
-- =============================================

-- Relatório 1: Classificação — pacientes por prioridade de triagem (ORDER BY)
select
  p.nm_pess,
  t.prior_triag,
  t.nivel_urg_ia,
  t.conf_ia,
  t.stts_triag,
  t.dt_triag
from triagem t
join pessoa p on t.fk_pess_id = p.id_pess
order by
  case t.prior_triag
    when 'urgente' then 1
    when 'alta'    then 2
    when 'media'   then 3
    when 'baixa'   then 4
  end asc,
  t.nivel_urg_ia desc;

-- Relatório 2: Função numérica — média de capacidade mensal dos dentistas ativos
select
  round(avg(cap_mensal), 2)  as media_capacidade,
  max(cap_mensal)            as maior_capacidade,
  min(cap_mensal)            as menor_capacidade,
  sum(cap_mensal)            as capacidade_total
from dentista
where stts_dent = 'ativo';

-- Relatório 3: Função de grupo — total de triagens por status (GROUP BY)
select
  stts_triag                    as status_triagem,
  count(*)                      as total,
  round(count(*) * 100.0 /
    sum(count(*)) over(), 1)    as percentual
from triagem
group by stts_triag
order by total desc;

-- Relatório 4: Subconsulta — dentistas com capacidade acima da média geral
select
  d.nm_dent,
  d.cro_dent,
  d.cap_mensal,
  d.stts_dent
from dentista d
where d.cap_mensal > (
  select avg(cap_mensal)
  from dentista
  where stts_dent = 'ativo'
)
order by d.cap_mensal desc;

-- Relatório 5: JOIN — triagens com nome do paciente, prioridade, status e decisão
select
  p.nm_pess                          as paciente,
  t.prior_triag                      as prioridade,
  t.problema_bucal                   as problema,
  t.nivel_urg_ia                     as urgencia_ia,
  t.stts_triag                       as status_triagem,
  nvl(t.decisao, 'Sem decisão')      as decisao,
  t.dt_triag                         as data_triagem
from triagem t
join pessoa p on t.fk_pess_id = p.id_pess
order by t.nivel_urg_ia desc nulls last;
