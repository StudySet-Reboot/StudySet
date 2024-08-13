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
            $('#start-time').val(date.format()+'T00:00');
            $('#end-time').val(date);
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
});