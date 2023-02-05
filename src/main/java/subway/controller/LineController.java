package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.model.*;
import subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody CreateLineRequest request) {
        LineResponse line = lineService.saveLine(request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getAllLines() {
        return ResponseEntity.ok().body(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody UpdateLineRequest request) {
        lineService.updateLine(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long id, @RequestBody CreateSectionRequest request) {
        SectionResponse section = lineService.createSection(id, request);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections")).body(section);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteLastSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteLastSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
