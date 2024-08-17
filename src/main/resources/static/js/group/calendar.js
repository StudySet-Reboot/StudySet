$(document).ready(function() {
    var groupId = $('#groupId').val();
    var calendar = $('#calendar').fullCalendar({
        events: {
            url: 'schedules/events',
            method: 'GET',
            failure: function() {
                console.error('There was an error while fetching events.');
            }
        },
        dayClick: function(date, jsEvent, view) {
            console.log(date.format());
            $('#eventModal').show();
            $('#start-time').val(date.format() + 'T00:00');
            $('#end-time').val(date);
        },
        eventClick: function(info) {
            var eventObj = info;
            $('#edit-event-name').val(eventObj.title);
            $('#edit-start-time').val(eventObj.start.format()); // 날짜 포맷 지정
            if (eventObj.end) {
                $('#edit-end-time').val(eventObj.end.format());
            } else {
                $('#edit-end-time').val(eventObj.start.format()); // 종료시간이 없으면 시작시간과 동일하게 설정
            }
            $('#edit-event-description').val(eventObj.description || '');
            $('#edit-event-place').val(eventObj.location || '');
            $('#edit-is-important').prop('checked', eventObj.isImportant || false);
            $('#edit-event-id').val(eventObj.id);
            // 수정 모달 표시
            $('#edit-event-modal').show();
        }
    });

    // 모달 닫기 버튼 이벤트 핸들러
    $('.close-btn').click(function() {
        $('#eventModal').hide();
        $('#edit-event-modal').hide();
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if ($(event.target).is('#eventModal')) {
            $('#eventModal').hide();
        }
    });

    // 이벤트 생성 폼 제출 이벤트 핸들러
    $('#eventForm').submit(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지
        const scheduleCreate = {
            isImportant: $('#is-important').is(':checked'),
            title: $('#event-name').val(),
            startDate: $('#start-time').val(),
            endDate: $('#end-time').val(),
            description: $('#event-description').val(),
            location: $('#event-place').val()
        };

        fetch(`/groups/${groupId}/schedules`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(scheduleCreate)
        }).then(response => response.json())
            .then(data => {
                console.log('Event created:', data);
                $('#eventModal').hide();
                calendar.fullCalendar('refetchEvents');
            })
            .catch(error => console.error('Error:', error));
    });

    // 이벤트 수정 폼 제출 이벤트 핸들러
    $('#editForm').submit(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지
        const eventId = $('#edit-event-id').val();
        const scheduleEdit = {
            id: eventId,
            isImportant: $('#edit-is-important').is(':checked'),
            title: $('#edit-event-name').val(),
            startDate: $('#edit-start-time').val(),
            endDate: $('#edit-end-time').val(),
            description: $('#edit-event-description').val(),
            location: $('#edit-event-place').val()
        };

        fetch(`/groups/${groupId}/schedules/events/${eventId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(scheduleEdit)
        }).then(response => response.json())
            .then(data => {
                console.log('Event updated:', data);
                $('#edit-event-modal').hide();
                calendar.fullCalendar('refetchEvents');
            })
            .catch(error => console.error('Error:', error));
    });

});
