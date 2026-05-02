# Prompt: Implementar Conexao com o Banco de Dados Oracle

Configure a conexao com o banco Oracle do servidor FIAP no projeto Quarkus seguindo estas etapas:

## 1. Atualizar `pom.xml`

Descomentar (ou adicionar) as dependencias abaixo dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-oracle</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-validator</artifactId>
</dependency>
```

## 2. Atualizar `src/main/resources/application.properties`

```properties
# Datasource Oracle — servidor FIAP (use variaveis de ambiente para credenciais)
quarkus.datasource.db-kind=oracle
quarkus.datasource.username=${DB_USER}
quarkus.datasource.password=${DB_PASSWORD}
quarkus.datasource.jdbc.url=${DB_URL:jdbc:oracle:thin:@//oracle.fiap.com.br:1521/orcl}

# Hibernate
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=false

# Swagger sempre disponivel
quarkus.swagger-ui.always-include=true
```

## 3. Anotar entidades com JPA — nomes de tabela/coluna EXATOS do banco herdado da Sprint 3

### `Beneficiario.java`
```java
@Entity
@Table(name = "BeneficiarioDTO")
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_benef")
    @SequenceGenerator(name = "seq_benef", sequenceName = "SEQ_BENEFICIARIO", allocationSize = 1)
    @Column(name = "id_beneficiario")
    private Long id;

    @Column(name = "cpf", nullable = false, length = 14)
    private String cpf;

    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "id_pedido_ajuda")
    private Long idPedidoAjuda;

    @Column(name = "id_programa_social")
    private Long idProgramaSocial;

    @Column(name = "id_endereco")
    private Long idEndereco;
}
```

### `Dentista.java`
```java
@Entity
@Table(name = "DentistaDTO")
public class Dentista {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dent")
    @SequenceGenerator(name = "seq_dent", sequenceName = "SEQ_DENTISTA", allocationSize = 1)
    @Column(name = "id_dentista")
    private Long id;

    @Column(name = "cro", nullable = false, length = 20)   // formato: ^[a-zA-Z]{2,}\d{2}$
    private String croDentista;

    @Column(name = "cpf", nullable = false, length = 14)
    private String cpf;

    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 10)
    private Sexo sexo;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "categoria", length = 60)
    private String categoria;   // especialidade textual (ex: "Ortodontia")

    // disponivel e CHAR(1) no Oracle: 'S' ou 'N'
    @Column(name = "disponivel", length = 1)
    private String disponivel;

    public boolean isDisponivel() { return "S".equals(disponivel); }
    public void setDisponivel(boolean d) { this.disponivel = d ? "S" : "N"; }

    @Column(name = "id_endereco")
    private Long idEndereco;
}
```

### `Atendimento.java`
```java
@Entity
@Table(name = "AtendimentoDTO")
public class Atendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_atend")
    @SequenceGenerator(name = "seq_atend", sequenceName = "SEQ_ATENDIMENTO", allocationSize = 1)
    @Column(name = "id_atendimento")
    private Long id;

    @Column(name = "prontuario", length = 500)   // campo real e prontuario (NAO descricao)
    private String prontuario;

    @Column(name = "data_inicial")
    private LocalDate dataInicial;

    @Column(name = "data_final")
    private LocalDate dataFinal;    // nullable — em aberto ate encerramento

    @Column(name = "id_beneficiario")
    private Long idBeneficiario;

    @Column(name = "id_dentista")
    private Long idDentista;

    @Column(name = "id_colaborador")
    private Long idColaborador;     // nullable
}
```

### `PedidoAjuda.java`
```java
@Entity
@Table(name = "Pedido_Ajuda")
public class PedidoAjuda {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pedido")
    @SequenceGenerator(name = "seq_pedido", sequenceName = "SEQ_PEDIDO_AJUDA", allocationSize = 1)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "cpf", nullable = false, length = 14)
    private String cpf;

    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 10)
    private Sexo sexo;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "descricao_problema", length = 500)
    private String descricaoProblema;

    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", length = 10)
    private StatusPedido status;    // PENDENTE | APROVADO | REJEITADO

    @Column(name = "id_endereco")
    private Long idEndereco;

    @Column(name = "id_dentista")
    private Long idDentista;       // nullable — preenchido ao aprovar pedido
}
```

### `Endereco.java`
```java
@Entity
@Table(name = "EnderecoDTO")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_end")
    @SequenceGenerator(name = "seq_end", sequenceName = "SEQ_ENDERECO", allocationSize = 1)
    @Column(name = "id_endereco")
    private Long id;

    @Column(name = "logradouro", length = 150) private String logradouro;
    @Column(name = "cep", length = 9)           private String cep;
    @Column(name = "numero", length = 10)       private String numero;
    @Column(name = "bairro", length = 100)      private String bairro;
    @Column(name = "cidade", length = 100)      private String cidade;
    @Column(name = "estado", length = 2)        private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco", length = 12)
    private TipoEndereco tipo;
}
```

### `Colaborador.java`
```java
@Entity
@Table(name = "ColaboradorDTO")
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_colab")
    @SequenceGenerator(name = "seq_colab", sequenceName = "SEQ_COLABORADOR", allocationSize = 1)
    @Column(name = "id_colaborador")
    private Long id;

    @Column(name = "cpf", nullable = false, length = 14) private String cpf;
    @Column(name = "nome_completo", nullable = false, length = 100) private String nomeCompleto;
    @Column(name = "data_nascimento")   private LocalDate dataNascimento;
    @Column(name = "data_contratacao")  private LocalDate dataContratacao;
    @Column(name = "email", length = 100) private String email;
}
```

## 4. Verificar

```bash
./mvnw compile
```
