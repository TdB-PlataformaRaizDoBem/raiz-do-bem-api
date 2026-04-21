# Prompt: Implementar Conexão com o Banco de Dados Oracle

Configure a conexão com o banco Oracle no projeto Quarkus seguindo estas etapas:

## 1. Atualizar `pom.xml`

Descomentar (ou adicionar) as dependências abaixo dentro de `<dependencies>`:

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
# Datasource Oracle — use variáveis de ambiente para não expor credenciais
quarkus.datasource.db-kind=oracle
quarkus.datasource.username=${DB_USER:raizdobem}
quarkus.datasource.password=${DB_PASSWORD:raizdobem123}
quarkus.datasource.jdbc.url=${DB_URL:jdbc:oracle:thin:@localhost:1521:xe}

# Hibernate
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=false

# Swagger
quarkus.swagger-ui.always-include=true
```

## 3. Anotar todas as entidades em `model/`

Para cada classe em `br.com.raizdobem.api.model` (exceto enums), adicionar:

```java
import jakarta.persistence.*;

@Entity
@Table(name = "TB_<NOME_TABELA>")
public class MinhaEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_<nome>")
    @SequenceGenerator(name = "seq_<nome>", sequenceName = "SEQ_<NOME>", allocationSize = 1)
    private Long id;

    @Column(name = "NM_CAMPO", nullable = false, length = 100)
    private String campo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTIDADE_RELACIONADA")
    private EntidadeRelacionada relacionada;
    // ... demais atributos
}
```

Mapeamento de tabelas sugerido:
- `Beneficiario` → `TB_BENEFICIARIO`
- `PedidoAjuda` → `TB_PEDIDO_AJUDA`
- `ProgramaSocial` → `TB_PROGRAMA_SOCIAL`
- `Atendimento` → `TB_ATENDIMENTO`
- `Dentista` → `TB_DENTISTA`
- `Especialidade` → `TB_ESPECIALIDADE`
- `Colaborador` → `TB_COLABORADOR`
- `Endereco` → `TB_ENDERECO`

## 4. Verificar

Rodar `./mvnw compile` para confirmar que não há erros de compilação.
