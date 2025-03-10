package com.spring_boots.spring_boots.category.service;

import com.spring_boots.spring_boots.category.dto.event.*;
import com.spring_boots.spring_boots.category.entity.Event;
import com.spring_boots.spring_boots.category.repository.EventRepository;
import com.spring_boots.spring_boots.common.config.error.ResourceNotFoundException;
import com.spring_boots.spring_boots.s3Bucket.service.S3BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class EventServiceTest {

  @Mock
  private EventRepository eventRepository;

  @Mock
  private EventMapper eventMapper;

  @Mock
  private S3BucketService s3BucketService;

  @InjectMocks
  private EventService eventService;

  private Event mockEvent;
  private EventDto mockEventDto;
  private EventDetailDto mockEventDetailDto;
  private EventAdminDto mockEventAdminDto;
  private MockMultipartFile mockThumbnailFile;
  private MockMultipartFile mockContentFile;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    String thumbnailUrl = "http://test-url.com/thumbnail.jpg";
    List<String> contentUrls = Arrays.asList("http://test-url.com/content1.jpg", "http://test-url.com/content2.jpg");

    mockEvent = Event.builder()
        .id(1L)
        .eventTitle("Test Event")
        .eventContent("Test Content")
        .isActive(true)
        .thumbnailImageUrl(thumbnailUrl)
        .contentImageUrl(contentUrls)
        .build();

    mockEventDto = EventDto.builder()
        .id(1L)
        .eventTitle("Test Event")
        .thumbnailImageUrl(thumbnailUrl)
        .build();

    mockEventDetailDto = EventDetailDto.builder()
        .id(1L)
        .eventTitle("Test Event")
        .eventContent("Test Content")
        .thumbnailImageUrl(thumbnailUrl)
        .contentImageUrl(contentUrls)
        .build();

    mockEventAdminDto = EventAdminDto.builder()
        .id(1L)
        .eventTitle("Test Event")
        .eventContent("Test Content")
        .isActive(true)
        .build();

    mockThumbnailFile = new MockMultipartFile("thumbnailFile", "thumbnail.jpg", "image/jpeg", "thumbnail content".getBytes());
    mockContentFile = new MockMultipartFile("contentFile", "content.jpg", "image/jpeg", "content image content".getBytes());
  }


  private final Long INVALID_EVENT_ID = 99999L;


  @Test
  @DisplayName("이벤트 생성 확인 테스트")
  void createEvent() throws IOException {
    // given
    EventRequestDto requestDto = EventRequestDto.builder()
        .eventTitle(mockEvent.getEventTitle())
        .eventContent(mockEvent.getEventContent())
        .build();

    List<MultipartFile> contentFiles = Arrays.asList(mockContentFile, mockContentFile);

    when(s3BucketService.uploadFile(any(MultipartFile.class)))
        .thenReturn(mockEvent.getThumbnailImageUrl())
        .thenReturn(mockEvent.getContentImageUrl().get(0))
        .thenReturn(mockEvent.getContentImageUrl().get(1));
    when(eventMapper.eventRequestDtoToEvent(any(EventRequestDto.class))).thenReturn(mockEvent);
    when(eventRepository.save(any(Event.class))).thenReturn(mockEvent);
    when(eventMapper.eventToEventDetailDto(any(Event.class))).thenReturn(mockEventDetailDto);

    // when
    EventDetailDto result = eventService.createEvent(requestDto, mockThumbnailFile, contentFiles);

    // then
    assertNotNull(result);
    assertEquals(mockEventDetailDto.getEventTitle(), result.getEventTitle());
    assertEquals(mockEventDetailDto.getThumbnailImageUrl(), result.getThumbnailImageUrl());
    assertEquals(mockEventDetailDto.getContentImageUrl(), result.getContentImageUrl());
    verify(s3BucketService, times(3)).uploadFile(any(MultipartFile.class));
    verify(eventRepository).save(any(Event.class));
  }

  @Test
  @DisplayName("진행 중인 이벤트 목록 확인 테스트")
  void getActiveEvents() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 10);
    LocalDate currentDate = LocalDate.now();

    List<Event> activeEvents = Arrays.asList(mockEvent, mockEvent);
    Page<Event> activePage = new PageImpl<>(activeEvents, pageRequest, activeEvents.size());

    when(eventRepository.findByEndDateGreaterThanEqual(eq(currentDate), eq(pageRequest))).thenReturn(activePage);
    when(eventMapper.eventToEventDto(any(Event.class))).thenReturn(mockEventDto);

    // when
    Page<EventDto> result = eventService.getActiveEvents(0, 10);

    // then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals(mockEventDto.getEventTitle(), result.getContent().get(0).getEventTitle());

    verify(eventRepository).findByEndDateGreaterThanEqual(eq(currentDate), eq(pageRequest));
    verify(eventMapper, times(2)).eventToEventDto(any(Event.class));
  }

  @Test
  @DisplayName("이벤트 상세 조회 확인 테스트")
  void getEventDetail() {
    // given
    Long eventId = mockEvent.getId();
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
    when(eventMapper.eventToEventDetailDto(mockEvent)).thenReturn(mockEventDetailDto);

    // when
    EventDetailDto result = eventService.getEventDetail(eventId);

    // then
    assertNotNull(result);
    assertEquals(mockEventDetailDto.getEventTitle(), result.getEventTitle());
    assertEquals(mockEventDetailDto.getEventContent(), result.getEventContent());
    verify(eventRepository).findById(eventId);
  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 상세 조회 시 예외 발생 확인 테스트")
  void getEventDetail_유효하지않은ID_예외발생() {
    // given
    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> eventService.getEventDetail(INVALID_EVENT_ID));
    verify(eventRepository).findById(INVALID_EVENT_ID);
  }

  @Test
  @DisplayName("이벤트 업데이트 확인 테스트")
  void updateEvent() throws IOException {
    // given
    Long eventId = mockEvent.getId();
    EventRequestDto updateDto = EventRequestDto.builder()
        .eventTitle("Updated Event")
        .eventContent("Updated Content")
        .build();

    List<String> updatedContentUrls = Arrays.asList("http://test-url.com/updated-content1.jpg", "http://test-url.com/updated-content2.jpg");
    Event updatedEvent = Event.builder()
        .id(eventId)
        .eventTitle("Updated Event")
        .eventContent("Updated Content")
        .thumbnailImageUrl("http://test-url.com/updated-thumbnail.jpg")
        .contentImageUrl(updatedContentUrls)
        .build();

    List<MultipartFile> contentFiles = Arrays.asList(mockContentFile, mockContentFile);

    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
    when(s3BucketService.uploadFile(any(MultipartFile.class)))
        .thenReturn(updatedEvent.getThumbnailImageUrl())
        .thenReturn(updatedContentUrls.get(0))
        .thenReturn(updatedContentUrls.get(1));
    when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
    when(eventMapper.eventToEventDetailDto(updatedEvent)).thenReturn(
        EventDetailDto.builder()
            .id(eventId)
            .eventTitle(updateDto.getEventTitle())
            .eventContent(updateDto.getEventContent())
            .thumbnailImageUrl(updatedEvent.getThumbnailImageUrl())
            .contentImageUrl(updatedContentUrls)
            .build()
    );

    // when
    EventDetailDto result = eventService.updateEvent(eventId, updateDto, mockThumbnailFile, contentFiles);

    // then
    assertNotNull(result);
    assertEquals(updateDto.getEventTitle(), result.getEventTitle());
    assertEquals(updateDto.getEventContent(), result.getEventContent());
    assertEquals(updatedEvent.getThumbnailImageUrl(), result.getThumbnailImageUrl());
    assertEquals(updatedContentUrls, result.getContentImageUrl());
    verify(s3BucketService, times(mockEvent.getContentImageUrl().size() + 1)).deleteFile(anyString());
    verify(s3BucketService, times(3)).uploadFile(any(MultipartFile.class));
    verify(eventRepository).save(any(Event.class));
  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 업데이트 시 예외 발생 확인 테스트")
  void updateEvent_유효하지않은ID_예외발생() throws IOException {
    // given
    EventRequestDto updateDto = EventRequestDto.builder()
        .eventTitle("Updated Event")
        .eventContent("Updated Content")
        .build();
    List<MultipartFile> contentFiles = Arrays.asList(mockContentFile, mockContentFile);

    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> eventService.updateEvent(INVALID_EVENT_ID, updateDto, mockThumbnailFile, contentFiles));
    verify(eventRepository).findById(INVALID_EVENT_ID);
    verify(eventRepository, never()).save(any(Event.class));
  }

  @Test
  @DisplayName("이벤트 삭제 확인 테스트")
  void deleteEvent() {
    // given
    Long eventId = mockEvent.getId();
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

    // when
    eventService.deleteEvent(eventId);

    // then
    verify(eventRepository).findById(eventId);
    verify(s3BucketService).deleteFile(extractFileName(mockEvent.getThumbnailImageUrl())); // 썸네일 이미지 삭제
    List<String> urls = mockEvent.getContentImageUrl(); // 컨텐츠 이미지 URL 목록
    for (String url : urls) {
      verify(s3BucketService).deleteFile(extractFileName(url)); // 각 컨텐츠 이미지 URL에 대해 삭제
    }
    verify(eventRepository).deleteById(eventId);
  }

  // URL에서 파일 이름을 추출(파일 이름으로 삭제)
  private String extractFileName(String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

  @Test
  @DisplayName("존재하지 않는 ID로 이벤트 삭제 시 예외 발생 확인 테스트")
  void deleteEvent_유효하지않은ID_예외발생() {
    // given
    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(INVALID_EVENT_ID));
    verify(eventRepository).findById(INVALID_EVENT_ID);
    verify(eventRepository, never()).deleteById(INVALID_EVENT_ID);
  }

  @Test
  @DisplayName("관리자용 이벤트 목록 조회 확인 테스트")
  void getAdminEvents() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 10);
    Page<Event> eventPage = new PageImpl<>(Arrays.asList(mockEvent, mockEvent));
    when(eventRepository.findAll(pageRequest)).thenReturn(eventPage);
    when(eventMapper.eventToEventAdminDto(any(Event.class))).thenReturn(mockEventAdminDto);

    // when
    Page<EventAdminDto> result = eventService.getAdminEvents(0, 10);

    // then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals(mockEventAdminDto.getEventTitle(), result.getContent().get(0).getEventTitle());
    assertEquals(0, result.getNumber());
    assertEquals(2, result.getSize());   // mockEvent 항목 2개 반환
    verify(eventRepository).findAll(pageRequest);
    verify(eventMapper, times(2)).eventToEventAdminDto(any(Event.class));
  }

  @Test
  @DisplayName("관리자용 개별 이벤트 조회 확인 테스트")
  void getAdminEvent() {
    // given
    Long eventId = mockEvent.getId();
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));
    when(eventMapper.eventToEventAdminDto(mockEvent)).thenReturn(mockEventAdminDto);

    // when
    EventAdminDto result = eventService.getAdminEvent(eventId);

    // then
    assertNotNull(result);
    assertEquals(mockEventAdminDto.getId(), result.getId());
    assertEquals(mockEventAdminDto.getEventTitle(), result.getEventTitle());
    verify(eventRepository).findById(eventId);
    verify(eventMapper).eventToEventAdminDto(mockEvent);
  }


  @Test
  @DisplayName("존재하지 않는 ID로 관리자용 개별 이벤트 조회 시 예외 발생 확인 테스트")
  void getAdminEvent_유효하지않은ID_예외발생() {
    // given
    when(eventRepository.findById(INVALID_EVENT_ID)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ResourceNotFoundException.class, () -> eventService.getAdminEvent(INVALID_EVENT_ID));
    verify(eventRepository).findById(INVALID_EVENT_ID);
  }

}