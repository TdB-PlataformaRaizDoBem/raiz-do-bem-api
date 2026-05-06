-- RM567479 - Murilo Ayabe Severino
-- RM566667 - Paulo Cavalcante Caroba
-- RM566610 - Renan da Silva Paulino

--Apagando as tabelas iniciais
DROP TABLE Dentista_Especialidade CASCADE CONSTRAINTS;
DROP TABLE Dentista_Programa_Social CASCADE CONSTRAINTS;
DROP TABLE Atendimento CASCADE CONSTRAINTS;
DROP TABLE Beneficiario CASCADE CONSTRAINTS;
DROP TABLE Pedido_Ajuda CASCADE CONSTRAINTS;
DROP TABLE Colaborador CASCADE CONSTRAINTS;
DROP TABLE Dentista CASCADE CONSTRAINTS;
DROP TABLE Endereco CASCADE CONSTRAINTS;
DROP TABLE Especialidade CASCADE CONSTRAINTS;
DROP TABLE Programa_Social CASCADE CONSTRAINTS;

--Criando todas as tabelas
CREATE TABLE Atendimento 
    ( 
     id_atendimento                NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     prontuario VARCHAR2 (500 CHAR)  NOT NULL , 
     data_inicial      DATE  NOT NULL , 
     data_final        DATE NULL,
     id_beneficiario   NUMBER  NOT NULL , 
     id_dentista       NUMBER  NOT NULL , 
     id_colaborador    NUMBER NULL
    ) 
;

ALTER TABLE Atendimento 
    ADD CONSTRAINT atendimento_pk PRIMARY KEY ( id_atendimento ) ;

CREATE TABLE Beneficiario 
    ( 
     id_beneficiario                 NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     cpf                CHAR (11 CHAR)  NOT NULL UNIQUE, 
     nome_completo      VARCHAR2 (50 CHAR)  NOT NULL , 
     data_nascimento    DATE  NOT NULL , 
     telefone           VARCHAR2 (15 CHAR)  NOT NULL , 
     email              VARCHAR2 (40 CHAR)  NOT NULL , 
     id_programa_social NUMBER  NOT NULL , 
     id_pedido_ajuda    NUMBER  NOT NULL , 
     id_endereco        NUMBER  NOT NULL 
    ) 
;

ALTER TABLE Beneficiario 
    ADD CONSTRAINT beneficiario_pk PRIMARY KEY ( id_beneficiario ) ;

CREATE TABLE Colaborador 
    ( 
     id_colaborador   NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     cpf              CHAR (11 CHAR)  NOT NULL UNIQUE, 
     nome_completo    VARCHAR2 (50 CHAR)  NOT NULL , 
     data_nascimento  DATE  NOT NULL , 
     data_contratacao DATE  NOT NULL , 
     email            VARCHAR2 (40 CHAR)  NOT NULL 
    ) 
;

ALTER TABLE Colaborador 
    ADD CONSTRAINT colaborador_pk PRIMARY KEY ( id_colaborador ) ;

CREATE TABLE Dentista 
    ( 
     id_dentista   NUMBER  GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     cro           VARCHAR2 (10 CHAR)  NOT NULL UNIQUE, 
     cpf           CHAR (11 CHAR)  NOT NULL UNIQUE, 
     nome_completo VARCHAR2 (50 CHAR)  NOT NULL , 
     sexo          CHAR (1 CHAR)  NOT NULL CHECK(sexo IN ('M', 'F', 'O')), 
     email         VARCHAR2 (40 CHAR)  NOT NULL , 
     telefone      VARCHAR2 (15 CHAR)  NOT NULL , 
     categoria     VARCHAR2 (11 CHAR)  NOT NULL CHECK(categoria IN ('COORDENADOR', 'CLINICO')) , 
     id_endereco   NUMBER  NOT NULL , 
     disponivel    CHAR (1 CHAR)  NOT NULL CHECK(disponivel IN ('S', 'N'))
    ) 
;

ALTER TABLE Dentista 
    ADD CONSTRAINT dentista_pk PRIMARY KEY ( id_dentista ) ;

CREATE TABLE Dentista_Especialidade 
    ( 
     id_especialidade NUMBER  NOT NULL , 
     id_dentista      NUMBER  NOT NULL 
    ) 
;

ALTER TABLE Dentista_Especialidade 
    ADD CONSTRAINT dent_espec_pk PRIMARY KEY ( id_especialidade, id_dentista ) ;

CREATE TABLE Dentista_Programa_Social 
    ( 
     id_dentista NUMBER  NOT NULL , 
     id_programa NUMBER  NOT NULL 
    ) 
;

ALTER TABLE Dentista_Programa_Social 
    ADD CONSTRAINT dentista_programa_pk PRIMARY KEY ( id_dentista, id_programa ) ;

CREATE TABLE Endereco 
    ( 
     id_endereco           NUMBER  GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     logradouro    VARCHAR2 (50 CHAR)  NOT NULL , 
     cep           CHAR (8 CHAR)  NOT NULL , 
     numero        VARCHAR2 (6 CHAR)  NOT NULL , 
     bairro        VARCHAR2 (40 CHAR)  NOT NULL , 
     cidade        VARCHAR2 (30 CHAR)  NOT NULL , 
     estado        VARCHAR2 (30 CHAR)  NOT NULL , 
     tipo_endereco VARCHAR2 (12 CHAR)  NOT NULL CHECK(tipo_endereco IN ('RESIDENCIAL', 'PROFISSIONAL')) 
    ) 
;

ALTER TABLE Endereco 
    ADD CONSTRAINT endereco_pk PRIMARY KEY ( id_endereco ) ;

CREATE TABLE Especialidade 
    ( 
     id_especialidade        NUMBER  GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     descricao VARCHAR2 (25 CHAR)  NOT NULL 
    ) 
;

ALTER TABLE Especialidade 
    ADD CONSTRAINT especialidade_pk PRIMARY KEY ( id_especialidade ) ;

CREATE TABLE Pedido_Ajuda 
    ( 
     id_pedido                 NUMBER GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL , 
     cpf                CHAR (11 CHAR)  NOT NULL UNIQUE, 
     nome_completo      VARCHAR2 (50 CHAR)  NOT NULL , 
     data_nascimento    DATE  NOT NULL , 
     sexo               CHAR (1 CHAR)  NOT NULL CHECK(sexo IN ('M', 'F', 'O')), 
     telefone           VARCHAR2 (15 CHAR)  NOT NULL , 
     email              VARCHAR2 (40 CHAR)  NOT NULL , 
     descricao_problema VARCHAR2 (300 CHAR)  NOT NULL , 
     data_pedido        DATE  NOT NULL , 
     status_pedido      VARCHAR2 (20 CHAR)  NOT NULL CHECK(status_pedido IN ('PENDENTE', 'APROVADO', 'REJEITADO')), 
     id_dentista        NUMBER NULL, 
     id_endereco        NUMBER  NOT NULL 
    ) 
;

ALTER TABLE Pedido_Ajuda 
    ADD CONSTRAINT pedido_ajuda_pk PRIMARY KEY ( id_pedido ) ;

CREATE TABLE Programa_Social 
    ( 
     id_programa       NUMBER  NOT NULL, 
     programa VARCHAR2 (30 CHAR)  NOT NULL 
    ) 
;

ALTER TABLE Programa_Social 
    ADD CONSTRAINT programa_social_pk PRIMARY KEY ( id_programa ) ;

ALTER TABLE Atendimento 
    ADD CONSTRAINT atendimento_beneficiario_fk FOREIGN KEY 
    ( 
     id_beneficiario
    ) 
    REFERENCES Beneficiario 
    ( 
     id_beneficiario
    ) 
;

ALTER TABLE Atendimento 
    ADD CONSTRAINT atendimento_colaborador_fk FOREIGN KEY 
    ( 
     id_colaborador
    ) 
    REFERENCES Colaborador 
    ( 
     id_colaborador
    ) 
;

ALTER TABLE Atendimento 
    ADD CONSTRAINT atendimento_dentista_fk FOREIGN KEY 
    ( 
     id_dentista
    ) 
    REFERENCES Dentista 
    ( 
     id_dentista
    ) 
;

ALTER TABLE Beneficiario 
    ADD CONSTRAINT beneficiario_endereco_fk FOREIGN KEY 
    ( 
     id_endereco
    ) 
    REFERENCES Endereco 
    ( 
     id_endereco
    ) 
;

ALTER TABLE Beneficiario 
    ADD CONSTRAINT beneficiario_pedido_fk FOREIGN KEY 
    ( 
     id_pedido_ajuda
    ) 
    REFERENCES Pedido_Ajuda 
    ( 
     id_pedido
    ) 
;

ALTER TABLE Beneficiario 
    ADD CONSTRAINT beneficiario_programa_fk FOREIGN KEY 
    ( 
     id_programa_social
    ) 
    REFERENCES Programa_Social 
    ( 
     id_programa
    ) 
;

ALTER TABLE Dentista_Especialidade 
    ADD CONSTRAINT dent_espec_den_fk FOREIGN KEY 
    ( 
     id_dentista
    ) 
    REFERENCES Dentista 
    ( 
     id_dentista
    ) 
;

ALTER TABLE Dentista_Especialidade 
    ADD CONSTRAINT dent_espec_espec_fk FOREIGN KEY 
    ( 
     id_especialidade
    ) 
    REFERENCES Especialidade 
    ( 
     id_especialidade
    ) 
;

ALTER TABLE Dentista 
    ADD CONSTRAINT dentista_end_fk FOREIGN KEY 
    ( 
     id_endereco
    ) 
    REFERENCES Endereco 
    ( 
     id_endereco
    ) 
;

ALTER TABLE Dentista_Programa_Social 
    ADD CONSTRAINT dentista_programa_dent_fk FOREIGN KEY 
    ( 
     id_dentista
    ) 
    REFERENCES Dentista 
    ( 
     id_dentista
    ) 
;

ALTER TABLE Dentista_Programa_Social 
    ADD CONSTRAINT dentista_programa_prog_fk FOREIGN KEY 
    ( 
     id_programa
    ) 
    REFERENCES Programa_Social 
    ( 
     id_programa
    ) 
;

ALTER TABLE Pedido_Ajuda 
    ADD CONSTRAINT pedido_ajuda_dentista_fk FOREIGN KEY 
    ( 
     id_dentista
    ) 
    REFERENCES Dentista 
    ( 
     id_dentista
    ) 
;

ALTER TABLE Pedido_Ajuda 
    ADD CONSTRAINT pedido_ajuda_endereco_fk FOREIGN KEY 
    ( 
     id_endereco
    ) 
    REFERENCES Endereco 
    ( 
     id_endereco
    ) 
;