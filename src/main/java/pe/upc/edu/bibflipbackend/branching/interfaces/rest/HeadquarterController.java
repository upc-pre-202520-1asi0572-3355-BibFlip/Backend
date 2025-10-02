package pe.upc.edu.bibflipbackend.branching.interfaces.rest;

import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetAllHeadquartersQuery;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterByIdQuery;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterCommandService;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterQueryService;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources.CreateHeadquarterResource;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources.HeadquarterResource;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.transform.CreateHeadquarterCommandFromResourceAssembler;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.transform.HeadquarterResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/headquarters", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Headquarters", description = "Headquarter Management Endpoints")
public class HeadquarterController {
    private final HeadquarterCommandService headquarterCommandService;
    private final HeadquarterQueryService headquarterQueryService;

    public HeadquarterController(HeadquarterCommandService headquarterCommandService, HeadquarterQueryService headquarterQueryService) {
        this.headquarterCommandService = headquarterCommandService;
        this.headquarterQueryService = headquarterQueryService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HeadquarterResource> createHeadquarter(@RequestBody CreateHeadquarterResource resource) {
        var createHeadquarterCommand = CreateHeadquarterCommandFromResourceAssembler.toCommandFromResource(resource);
        var headquarter = headquarterCommandService.handle(createHeadquarterCommand);
        if (headquarter.isEmpty()) return ResponseEntity.badRequest().build();
        var headquarterResource = HeadquarterResourceFromEntityAssembler.toResourceFromEntity(headquarter.get());
        return new ResponseEntity<>(headquarterResource, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{headquarterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HeadquarterResource> getHeadquarterById(@PathVariable Long headquarterId) {
        var getHeadquarterByIdQuery = new GetHeadquarterByIdQuery(headquarterId);
        var headquarter = headquarterQueryService.handle(getHeadquarterByIdQuery);
        if (headquarter.isEmpty()) return ResponseEntity.notFound().build();
        var headquarterResource = HeadquarterResourceFromEntityAssembler.toResourceFromEntity(headquarter.get());
        return ResponseEntity.ok(headquarterResource);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<HeadquarterResource>> getAllHeadquarters() {
        var getAllHeadquartersQuery = new GetAllHeadquartersQuery();
        var headquarters = headquarterQueryService.handle(getAllHeadquartersQuery);
        var headquarterResources = headquarters.stream()
                .map(HeadquarterResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(headquarterResources);
    }
}
