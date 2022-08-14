package domainapp.modules.simple.dom.item;

import domainapp.modules.simple.dom.kitArticulo.KitArticulo;
import domainapp.modules.simple.types.articulo.CodigoArticulo;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.persistence.jdo.applib.services.JdoSupportService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;


@DomainService(
        nature = NatureOfService.VIEW,
        logicalTypeName = "depotapp.ItemsKit"
)
@javax.annotation.Priority(PriorityPrecedence.EARLY)
@lombok.RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class ItemsKit {

    final RepositoryService repositoryService;
    final JdoSupportService jdoSupportService;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public ItemKit create(
            @CodigoArticulo final String codigo,
            final int cantidad,
            final KitArticulo kitArticulo) {
        return repositoryService.persist(ItemKit.creacionItem(kitArticulo, codigo, cantidad));
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    public List<ItemKit> listAll(KitArticulo kitArticulo) {
        return repositoryService.allInstances(ItemKit.class).stream().filter(x -> x.getKitArticulo().equals(kitArticulo)).collect(Collectors.toList());
    }
}
