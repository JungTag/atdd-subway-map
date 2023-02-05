package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.entity.Line;
import subway.entity.Section;
import subway.entity.Station;
import subway.model.*;
import subway.repository.LineRepository;
import subway.repository.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(CreateLineRequest req) {
        Line newLine = Line.create(req.getName(), req.getColor());
        Section newSection = Section.createForNewLine(
            getStationsInSection(req.getUpStationId(), req.getDownStationId()),
            req.getDistance(),
            newLine
        );
        newLine.addSection(newSection);
        lineRepository.save(newLine);

        return new LineResponse(newLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return lineRepository.findById(id).map(LineResponse::new).orElse(null);
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.updateNameAndColor(request.getName(), request.getColor());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private List<Station> getStationsInSection(Long upStationId, Long downStationId) {
        return stationRepository.findByIdInOrderById(
            List.of(
                upStationId,
                downStationId
            ));
    }

    @Transactional
    public SectionResponse createSection(Long lineId, CreateSectionRequest req) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        Section newSection = Section.create(
            getStationsInSection(req.getUpStationId(), req.getDownStationId()),
            req.getDistance(),
            line.getDownEndStation(),
            line
        );
        line.addSection(newSection);

        lineRepository.save(line);

        return SectionResponse.from(newSection);
    }

    @Transactional
    public void deleteLastSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        Station downEndStation = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
        line.deleteSection(downEndStation);
    }
}
