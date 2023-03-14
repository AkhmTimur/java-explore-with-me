package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.compilation.Compilation;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.repository.compilation.CompilationRepository;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.service.event.EventCommonService;
import ru.practicum.ewm.service.request.RequestService;
import ru.practicum.ewm.validator.EntityValidator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.mapper.CompilationMapper.compilationToCompilationDto;
import static ru.practicum.ewm.mapper.CompilationMapper.compilationToNewCompilationDto;

@Service
@RequiredArgsConstructor
public class CompilationAdminService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventCommonService eventCommonService;
    private final RequestService requestService;
    private final EntityValidator entityValidator;

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationToNewCompilationDto(newCompilationDto);
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        compilation.setEvents(events);
        CompilationDto compilationDto = compilationToCompilationDto(compilationRepository.save(compilation));
        setViewsToEvents(compilation, compilationDto);
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(events);
        for (EventShortDto event : compilationDto.getEvents()) {
            event.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(event.getId())).count());
        }
        return compilationDto;
    }

    @Transactional
    public void deleteCompilation(Long comId) {
        entityValidator.getCompilationIfExist(comId);
        compilationRepository.deleteById(comId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long comId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = entityValidator.getCompilationIfExist(comId);
        if (updateRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }
        CompilationDto updatedCompilationDto = compilationToCompilationDto(compilationRepository.save(compilation));
        setViewsToEvents(compilation, updatedCompilationDto);
        List<ParticipationRequest> confirmedRequests = requestService
                .findConfirmedRequests(compilation.getEvents());
        for (EventShortDto event : updatedCompilationDto.getEvents()) {
            event.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(event.getId())).count());
        }
        return updatedCompilationDto;
    }

    private void setViewsToEvents(Compilation compilation, CompilationDto compilationDto) {
        Map<Long, Long> views = eventCommonService.getStats(compilation.getEvents(), false);
        if (!views.isEmpty()) {
            compilationDto.getEvents().forEach(e -> e.setViews(views.get(e.getId())));
        }
    }
}
