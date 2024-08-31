$(document).ready(function() {
    const groupId = $('#groupId').val();
    $('#adjust-calendar-btn').click(function() {
        window.location.href = '/groups/' + groupId + '/schedules'; // 새 URL로 이동
    });
});

function addChart() {
    var selectedCells = document.querySelectorAll('.highlighted');
    var timelist = { "list": [] };
    const groupId = $('#groupId').val();
    selectedCells.forEach(function(cell) {
        var time = cell.parentNode.rowIndex - 1;
        var day = cell.cellIndex - 1;
        var listdata = { "day": day, "time": time };
        timelist.list.push(listdata);
    });

    timelist.list.sort(function(a, b) {
        return a.day - b.day || a.time - b.time;
    });

    var stringJson = JSON.stringify(timelist);
    fetch(`/groups/${groupId}/timetables`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: stringJson
    }).then(response => {
        if (response.ok) {
            window.location.href = `/groups/${groupId}/timetables`;
        } else {
            console.error('Failed to submit time table');
        }
    });
}

// jQuery code to handle mouse events for selecting cells
$(function() {
    var isMouseDown = false;
    $("#time-table td").mousedown(function() {
        isMouseDown = true;
        $(this).toggleClass("highlighted");
        return false;
    }).mouseover(function() {
        if (isMouseDown) {
            $(this).toggleClass("highlighted");
        }
    }).bind("selectstart", function() {
        return false; // prevent text selection in IE
    });

    $(document).mouseup(function() {
        isMouseDown = false;
    });
});
