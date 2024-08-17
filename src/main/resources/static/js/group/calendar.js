$(document).ready(function() {
    var groupId = $('#groupId').val();
    var eventList = {
        url: 'schedules/events',
        method: 'GET',
        failure: function() {
            console.error('There was an error while fetching events.');
        }
    }

    $('#calendar').fullCalendar({
        events: eventList,
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
            if(eventObj.end) {
                $('#edit-end-time').val(eventObj.end.format());
            } else {
                $('#edit-end-time').val(eventObj.start.format()); // 종료시간이 없으면 시작시간과 동일하게 설정
            }
            $('#edit-event-description').val(eventObj.description || '');
            $('#edit-event-place').val(eventObj.location || '');
            $('#edit-is-important').prop('checked', eventObj.isImportant || false);

            console.log(eventObj.id);
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
});