package domainapp.modules.simple.dom.item.itemAjuste;

import domainapp.modules.simple.dom.EstadoOperativo;
import domainapp.modules.simple.dom.ajuste.Ajuste;
import domainapp.modules.simple.dom.articulo.Articulo;
import lombok.RequiredArgsConstructor;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.repository.RepositoryService;

import javax.inject.Inject;
import java.util.List;

@Action(
        semantics = SemanticsOf.IDEMPOTENT,
        commandPublishing = Publishing.ENABLED,
        executionPublishing = Publishing.ENABLED
)
@ActionLayout(associateWith = "itemsAjuste", sequence = "1")
@RequiredArgsConstructor
public class Ajuste_addItemAjuste {

    private final Ajuste ajuste;

    public Ajuste act(
            final Articulo articulo,
            final int cantidad
    ) {
        repositoryService.persist(new ItemAjuste(ajuste,articulo,cantidad));
        return ajuste;
    }

    public List<Articulo> choices0Act() {
        return repositoryService.allInstances(Articulo.class);
    }

    public String validate0Act(final Articulo articulo) {

        return repositoryService.firstMatch(
                Query.named(ItemAjuste.class, ItemAjuste.NAMED_QUERY__BUSCAR_ITEM_POR_AJUSTE_Y_ARTICULO)
                        .withParameter("ajuste", ajuste)
                        .withParameter("articulo", articulo)).isPresent()
                ? String.format("El artículo ingresado ya se encuentra en la lista de artículos de este ajuste.")
                : null;

    }

    public boolean hideAct() { return ajuste.getEstadoOperativo() == EstadoOperativo.COMPLETADO; }

    @Inject
    RepositoryService repositoryService;

}
