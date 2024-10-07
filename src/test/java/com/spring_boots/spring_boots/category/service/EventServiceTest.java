package com.spring_boots.spring_boots.category.service;

import com.spring_boots.spring_boots.category.dto.event.*;
import com.spring_boots.spring_boots.category.entity.Event;
import com.spring_boots.spring_boots.category.repository.EventRepository;
import com.spring_boots.spring_boots.common.config.error.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock
  private EventRepository eventRepository;

  @Mock
  private EventMapper eventMapper;

  @InjectMocks
  private EventService eventService;

  private final Long INVALID_EVENT_ID = 99999L;


  @Test
  @DisplayName("이벤트 생성 확인 테스트")
  void createEvent() {
    // given
    EventRequestDto requestDto = EventRequestDto.builder()
        .eventTitle("Test Event")
        .eventContent("Test Content")
        .build();

    Event event = Event.builder()
        .id(1L)
        .eventTitle("Test Event")
        .build();

    EventDetailDto responseDto = EventDetailDto.builder()
        .id(1L)
        .eventTitle("Test Event")
        .build();
    
    when(eventMapper.eventRequestDtoToEvent(requestDto)).thenReturn(event);
    when(eventRepository.save(any(Event.class))).thenReturn(event);
    when(eventMapper.eventToEventDetailDto(event)).thenReturn(responseDto);

    // when
    EventDetailDto result = eventService.createEvent(requestDto);

    // then
    assertThat(result).usingRecursiveComparison().isEqualTo(responseDto);

    verify(eventRepository).save(any(Event.class));

  }


  @Test
  @DisplayName("진행 중인 이벤트 목록 확인 테스트")
  void getActiveEvents() {
    // given
    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    Event event1 = Event.builder().id(1L).isActive(true).build();
    Event event2 = Event.builder().id(2L).isActive(true).build();
    Page<Event> eventPage = new PageImpl<>(Arrays.asList(event1, event2), pageable, 2);

    when(eventRepository.findAll(pageable)).thenReturn(eventPage);
    when(eventMapper.eventToEventDto(any(Event.class))).thenReturn(new EventDto());

    // when
    Page<EventDto> result = eventService.getActiveEvents(pageable);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);

    verify(eventRepository).findAll(pageable);
    verify(eventMapper, times(2)).eventToEventDto(any(Event.class));

  }


  @Test
  @DisplayName("이벤트 상세 조회 확인 테스트")
  void getEventDetail() {
    // given
    Long eventId = 1L;
    Event event = Event.builder().id(eventId).build();
    EventDetailDto detailDto = EventDetailDto.builder()
        .id(eventId)
        .eventTitle("테스트 이벤트")
        .build();

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventMapper.eventToEventDetailDto(event)).thenReturn(detailDto);

    // when
    EventDetailDto result = eventService.getEventDetail(eventId);

    // then
    assertThat(result).usingRecursiveComparison().isEqualTo(detailDto);

    verify(eventRepository).findById(eventId);

  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 상세 조회 시 예외 발생 확인 테스트")
  void getEventDetail_WithInvalidId_ShouldResourceNotFoundException() {
    // given
    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> eventService.getEventDetail(INVALID_EVENT_ID))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("조회할 이벤트를 찾을 수 없습니다: " + INVALID_EVENT_ID);

    verify(eventRepository).findById(INVALID_EVENT_ID);

  }


  @Test
  @DisplayName("이벤트 업데이트 확인 테스트")
  void updateEvent() {
    // given
    Long eventId = 1L;
    EventRequestDto updateDto = EventRequestDto.builder()
        .eventTitle("Updated Event")
        .eventContent("Updated Content")
        .build();

    Event existingEvent = Event.builder().id(eventId).build();

    EventDetailDto responseDto = EventDetailDto.builder()
        .id(eventId)
        .eventTitle("Updated Event")
        .build();
    
    
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
    when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);
    when(eventMapper.eventToEventDetailDto(existingEvent)).thenReturn(responseDto);

    // when
    EventDetailDto result = eventService.updateEvent(eventId, updateDto);

    // then
    assertThat(result).usingRecursiveComparison().isEqualTo(responseDto);

    verify(eventRepository).findById(eventId);
    verify(eventRepository).save(any(Event.class));

  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 업데이트 시 예외 발생 확인 테스트")
  void updateEvent_WithInvalidId_ShouldThrowResourceNotFoundException() {
    // given
    EventRequestDto updateDto = EventRequestDto.builder()
        .eventTitle("Updated Event")
        .eventContent("Updated Content")
        .build();

    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> eventService.updateEvent(INVALID_EVENT_ID, updateDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("업데이트할 이벤트를 찾을 수 없습니다: " + INVALID_EVENT_ID);

    verify(eventRepository).findById(INVALID_EVENT_ID);
    verify(eventRepository, times(0)).save(any(Event.class));

  }


  @Test
  @DisplayName("이벤트 삭제 확인 테스트")
  void deleteEvent() {
    // given
    Long eventId = 1L;
    Event event = Event.builder().id(eventId).build();
    
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    // when
    eventService.deleteEvent(eventId);

    // then
    verify(eventRepository).findById(eventId);
    verify(eventRepository).deleteById(eventId);

  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 삭제 시 예외 발생 확인 테스트")
  void deleteEvent_WithInvalidId_ShouldThrowResourceNotFoundException() {
    // given
    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> eventService.deleteEvent(INVALID_EVENT_ID))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("삭제할 이벤트를 찾을 수 없습니다: " + INVALID_EVENT_ID);

    verify(eventRepository).findById(INVALID_EVENT_ID);
    verify(eventRepository, times(0)).deleteById(INVALID_EVENT_ID);

  }

}