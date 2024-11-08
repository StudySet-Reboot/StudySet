import {displayErrorToast, displayErrorToastWithValidation, showConfirm } from '../common/toast.js';

const currentDate = new Date();
let currentYear = currentDate.getFullYear();
let currentMonth = currentDate.getMonth() + 1;

$(document).ready(function() {
    var groupId = $('#groupId').val();
    var calendar = $('#calendar').fullCalendar({
        initialView: 'dayGridMonth',
        locale: 'ko',
        header: {
            center: 'title',
            left: 'none'
        },
        showNonCurrentDates: false,
        height: 600, // 전체 캘린더 높이 조정
        events: function(start, end, timezone, callback) {
            const url = `/api/groups/${groupId}/schedules/events?year=${currentYear}&month=${currentMonth}`;
            fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    callback(data); // 이벤트 데이터를 전달
                })
                .catch(error => {
                    console.error('Error fetching events:', error);
                    callback([]); // 오류 발생 시 빈 배열 반환
                });
        },
        dayClick: function(date, jsEvent, view) {
            console.log(date.format());
            $('#event-create-modal').show();
            $('#start-time').val(date.format() + 'T00:00');
            $('#end-time').val(date);
        },
        eventClick: function(info) {
            var eventObj = info;
            $('#edit-event-name').val(eventObj.title);
            $('#edit-start-time').val(eventObj.start.format());
            if (eventObj.end) {
                $('#edit-end-time').val(eventObj.end.format());
            } else {
                $('#edit-end-time').val(eventObj.start.format()); // 종료시간이 없으면 시작시간과 동일하게 설정
            }
            $('#edit-event-description').val(eventObj.description || '');
            $('#edit-event-place').val(eventObj.location || '');
            $('#edit-is-important').prop('checked', eventObj.isImportant || false);
            $('#edit-event-id').val(eventObj.id);
            $('#edit-event-modal').show();
        }
    });

    $('.fc-prev-button').click(function () {
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12; // 12월로 설정
            currentYear--; // 년도 감소
        }
        console.log(currentMonth);
        calendar.fullCalendar('refetchEvents');
    });

    $('.fc-next-button').click(function () {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        console.log(currentMonth);
        calendar.fullCalendar('refetchEvents');
    });

    $('.fc-today-button').click(function () {
        currentYear = currentDate.getFullYear();
        currentMonth = currentDate.getMonth() + 1;
        calendar.fullCalendar('refetchEvents');
    });

    // 캘린더 스타일 커스터마이징
    $('.fc-day-top .fc-day-number').css({
        'float': 'left',
        'margin-left': '5px',
        'margin-right': '0'
    });
    $('.fc-event-title').css({
        'overflow': 'hidden',
        'white-space': 'nowrap',
        'text-overflow' : 'ellipsis'
    });
    $('.fc-day-header').css({
        'background' : '#ff859a',
        'height' : '30px',
        'vertical-align' : 'middle'
    });

    // 모달 닫기 버튼 이벤트 핸들러
    $('.close-btn').click(function() {
        $('#event-create-modal').hide();
        $('#edit-event-modal').hide();

        $('#eventForm').trigger('reset');
        $('#editForm').trigger('reset');
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if ($(event.target).is('#eventModal')) {
            $('#event-create-modal').hide();
        }
    });

    // 이벤트 생성 폼 제출 이벤트 핸들러
    $('#eventForm').submit(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지
        const scheduleCreate = {
            isImportant: $('#is-important').prop('checked'),
            title: $('#event-name').val(),
            startDate: $('#start-time').val(),
            endDate: $('#end-time').val(),
            description: $('#event-description').val(),
            location: $('#event-place').val()
        };
        if(!scheduleCreate.title){
            displayErrorToast("제목을 입력해주세요.");
        }
        if(scheduleCreate.endDate && scheduleCreate.startDate>scheduleCreate.endDate){
            displayErrorToast("시작일은 종료일보다 앞서야 합니다.");
            return;
        }

        fetch(`/api/groups/${groupId}/schedules/events`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(scheduleCreate)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    if (errorData.validation) {
                        displayErrorToastWithValidation(errorData.validation);
                    } else {
                        throw new Error(`Error ${response.status}: ${errorData.message}`);
                    }
                });
            }
            $('#event-create-modal').hide();
            $('#eventForm').trigger('reset');
            calendar.fullCalendar('refetchEvents');
        }).catch(error => {
            console.error(`An error occurred: ${error.message}`);
        });
    });

    // 이벤트 수정 폼 제출 이벤트 핸들러
    $('#editForm').submit(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지
        var isImportant = ($('#edit-is-important').prop('checked'));
        console.log(isImportant);

        const eventId = $('#edit-event-id').val();
        const scheduleEdit = {
            id: eventId,
            isImportant: isImportant,
            title: $('#edit-event-name').val(),
            startDate: $('#edit-start-time').val(),
            endDate: $('#edit-end-time').val(),
            description: $('#edit-event-description').val(),
            location: $('#edit-event-place').val()
        };
        if(!scheduleEdit.title){
            displayErrorToast("제목을 입력해주세요.");
        }
        if(scheduleEdit.endDate && scheduleEdit.startDate > scheduleEdit.endDate){
            displayErrorToast("시작일은 종료일보다 앞서야 합니다.");
            return;
        }
        fetch(`/api/groups/${groupId}/schedules/events/${eventId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(scheduleEdit)
        }).then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    if (errorData.validation) {
                        displayErrorToastWithValidation(errorData.validation);
                    } else {
                        throw new Error(`Error ${response.status}: ${errorData.message}`);
                    }
                });
            }
            $('#edit-event-modal').hide();
            $('#editForm').trigger('reset');
            calendar.fullCalendar('refetchEvents');
        }).catch(error => {
            console.error('Error:', error);
        });
    });

    $('#del-schedule-btn').click(function () {
        showConfirm(function(result) {
            if (result) {
                const eventId = $('#edit-event-id').val();
                fetch(`/api/groups/${groupId}/schedules/events/${eventId}`, {
                    method: 'DELETE'
                }).then(response => {
                    $('#edit-event-modal').hide();
                    $('#editForm').trigger('reset');
                    calendar.fullCalendar('refetchEvents');
                }).catch(error => console.error('Error:', error));
            }
        });
    });

    $('#adjust-schedule-btn').click(function() {
        window.location.href = '/groups/' + groupId + '/timetables/view';
    });

});
