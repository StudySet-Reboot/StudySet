$(document).ready(function() {
    $('#calendar').fullCalendar({
        events: [
            {
                title: 'Event 1',
                start: '2024-08-01',
                color: 'rgba(255, 117, 123, 0.66)'
            }
        ],
        dayClick: function(date, jsEvent, view) {
            $('#eventModal').show();
            $('#eventDate').val(date.format());
        }
    });

    // 모달 닫기 버튼 이벤트 핸들러
    $('.close-btn').click(function() {
        $('#eventModal').hide();
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if ($(event.target).is('#eventModal')) {
            $('#eventModal').hide();
        }
    });

    // 이벤트 폼 제출 핸들러
    $('#eventForm').submit(function(event) {
        event.preventDefault();

        var eventData = {
            title: $('#eventTitle').val(),
            start: $('#eventDate').val(),
            description: $('#eventDescription').val(),
            color: 'rgba(255, 117, 123, 0.66)' // 이벤트 색상
        };

        $('#calendar').fullCalendar('renderEvent', eventData, true); // 달력에 새 이벤트 추가
        $('#eventModal').hide(); // 모달 닫기
        $('#eventForm')[0].reset(); // 폼 초기화
    });
});
