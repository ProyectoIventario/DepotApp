package domainapp.modules.simple.dom.cliente;

import domainapp.modules.simple.dom.EstadoACP;
import domainapp.modules.simple.dom.articulo.Articulo;
import domainapp.modules.simple.types.cliente.CodigoCliente;
import domainapp.modules.simple.types.cliente.Dni;
import domainapp.modules.simple.types.cliente.RazonSocial;
import lombok.*;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.jaxb.PersistentEntityAdapter;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Comparator;

import static org.apache.isis.applib.annotation.SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE;

@javax.jdo.annotations.PersistenceCapable(
        schema = "depotapp",
        identityType= IdentityType.DATASTORE)
@javax.jdo.annotations.Unique(
        name = "Cliente_codigo_UNQ", members = {"codigo"}
)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = Cliente.NAMED_QUERY__FIND_BY_CODIGO_LIKE,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.cliente.Cliente " +
                        "WHERE codigo.indexOf(:codigo) >= 0"
        ),
        @javax.jdo.annotations.Query(
                name = Cliente.NAMED_QUERY__FIND_BY_CODIGO_EXACT,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.cliente.Cliente " +
                        "WHERE codigo == :codigo"
        ),
        @javax.jdo.annotations.Query(
                name = Cliente.NAMED_QUERY__FIND_BY_HABILITADO,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.cliente.Cliente " +
                        "WHERE estado == 'HABILITADO'"
        ),
        @javax.jdo.annotations.Query(
                name = Cliente.NAMED_QUERY__FIND_BY_DESHABILITADO,
                value = "SELECT " +
                        "FROM domainapp.modules.simple.dom.cliente.Cliente " +
                        "WHERE estado == 'DESHABILITADO'"
        )
})
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column="id")
@javax.jdo.annotations.Version(strategy= VersionStrategy.DATE_TIME, column="version")
@DomainObject(logicalTypeName = "depotapp.Cliente", entityChangePublishing = Publishing.ENABLED)
@DomainObjectLayout()
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@ToString(onlyExplicitlyIncluded = true)
public class Cliente implements Comparable<Cliente>{



    static final String NAMED_QUERY__FIND_BY_CODIGO_LIKE = "Cliente.findByCodigoLike";
    static final String NAMED_QUERY__FIND_BY_CODIGO_EXACT = "Cliente.findByCodigoExact";
    static final String NAMED_QUERY__FIND_BY_HABILITADO = "Cliente.findByHabilitado";
    static final String NAMED_QUERY__FIND_BY_DESHABILITADO = "Cliente.findByDeshabilitado";

    @Inject RepositoryService repositoryService;
    @Inject TitleService titleService;
    @Inject
    MessageService messageService;

    public static Cliente creacion(String codigo, int dni, String razonSocial) {
        val cliente = new Cliente();
        codigo = ("000000" + codigo).substring(codigo.length());
        cliente.setCodigo(codigo);
        cliente.setDni(dni);
        cliente.setRazonSocial(razonSocial);
        cliente.setEstado(EstadoACP.HABILITADO);
        return cliente;
    }

    @Action(semantics = NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            position = ActionLayout.Position.PANEL,
            describedAs = "Habilita el cliente")
    public String habilitar() {
        String nombre = this.getCodigo();
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' habilitado", title));
        this.setEstado(EstadoACP.HABILITADO);
        return "Se habilitó el cliente " + nombre;
    }

    @Action(semantics = NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            position = ActionLayout.Position.PANEL,
            describedAs = "Deshabilita el cliente.")
    public String deshabilitar() {
        String nombre = this.getCodigo();
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deshabilitado", title));
        this.setEstado(EstadoACP.DESHABILITADO);
        return "Se deshabilitó el cliente " + nombre;
    }

    public String disableHabilitar() {
        return this.getEstado()==EstadoACP.HABILITADO ? "Ya se encuentra habilitado" : null;
    }

    public String disableDeshabilitar() {
        return this.getEstado()==EstadoACP.DESHABILITADO ? "Ya se encuentra deshabilitado" : null;
    }


    @CodigoCliente
    @Title
    @Getter
    @Setter
    @PropertyLayout(fieldSetId = "cliente", sequence = "1")
    private String codigo;

    @Dni
    @Setter
    @Getter
    @Property(editing = Editing.ENABLED)
    @PropertyLayout(fieldSetId = "cliente", sequence = "2")
    private int dni;

    @RazonSocial
    @Setter
    @Getter
    @PropertyLayout(fieldSetId = "cliente", sequence = "3")
    private String razonSocial;

    @Setter
    @Getter
    @PropertyLayout(fieldSetId = "cliente", sequence = "4")
    private EstadoACP estado;




    @Action(semantics = NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            position = ActionLayout.Position.PANEL,
            describedAs = "Borra el cliente.")
    public String borrar() {
        String nombre = this.getCodigo();
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' borrado", title));
        repositoryService.removeAndFlush(this);
        return "Se borró el artículo " + nombre;
    }


    private final static Comparator<Cliente> comparator =
            Comparator.comparing(Cliente::getCodigo);
    @Override
    public int compareTo(final Cliente other) {
        return comparator.compare(this, other);
    }
}