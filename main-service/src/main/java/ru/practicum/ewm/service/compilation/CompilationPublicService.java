package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.compilation.Compilation;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.repository.compilation.CompilationRepository;
import ru.practicum.ewm.service.event.EventCommonService;
import ru.practicum.ewm.service.request.RequestService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.CompilationMapper.compilationToCompilationDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final EventCommonService eventCommonService;
    private final RequestService requestService;

    public CompilationDto getCompilation(Long comId) {
        Compilation compilation = getCompilationIfExist(comId);
        CompilationDto compilationDto = compilationToCompilationDto(compilation);
        setViewsCompilationDto(compilation.getEvents(), compilationDto);
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(compilation.getEvents());
        for (EventShortDto event : compilationDto.getEvents()) {
            event.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(r -> r.getEvent().getId().equals(event.getId())).count());
        }
        return compilationDto;
    }

    public List<CompilationDto> getCompilations(long id, int size, boolean pinned) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Compilation> compilations = compilationRepository.findAllByIdIsGreaterThanEqualAndPinnedIs(id, pinned, pageRequest);
        List<CompilationDto> compilationDtos = compilations.stream().map(CompilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
        Set<Event> eventSet = compilations.stream().map(Compilation::getEvents).flatMap(List<Event>::stream)
                .collect(Collectors.toSet());
        compilationDtos.forEach(dto -> setViewsCompilationDto(List.copyOf(eventSet), dto));
        List<ParticipationRequest> confirmedRequests = requestService.findConfirmedRequests(List.copyOf(eventSet));
        compilationDtos.forEach(dto -> setRequestsToDto(dto.getEvents(), confirmedRequests));
        return compilationDtos;
    }

    private void setViewsCompilationDto(List<Event> events, CompilationDto compilationDto) {
        Map<Long, Long> views = eventCommonService.getStats(events, false);
        if (!views.isEmpty()) {
            compilationDto.getEvents().forEach(dto -> dto.setViews(views.get(dto.getId())));
        }
    }

    private void setRequestsToDto(List<EventShortDto> eventShortDtos, List<ParticipationRequest> participationRequests) {
        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setConfirmedRequests(
                    (int) participationRequests.stream()
                            .filter(r -> r.getEvent().getId().equals(eventShortDto.getId())).count()
            );
        }
    }

    public Compilation getCompilationIfExist(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(Compilation.class.getSimpleName() + " not found"));
    }
}
