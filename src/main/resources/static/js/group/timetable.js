$(document).ready(function() {
    $('#adjust-calendar-btn').click(function() {
        var groupId = $('#groupId').val();
        window.location.href = '/groups/' + groupId + '/schedules'; // 새 URL로 이동
    });
});

function addChart() {
    var selectedCells = document.querySelectorAll('.highlighted');
    var timelist = { "list": [] };

    selectedCells.forEach(function(cell) {
        var time = cell.parentNode.rowIndex - 1; // Adjust for header row
        var day = cell.cellIndex - 1; // Adjust for hour column
        var listdata = { "day": day, "time": time };
        timelist.list.push(listdata);
    });

    timelist.list.sort(function(a, b) {
        return a.day - b.day || a.time - b.time;
    });

    var stringJson = JSON.stringify(timelist);
    document.getElementsByName('editTime')[0].value = stringJson;
    document.forms['form'].submit();
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
