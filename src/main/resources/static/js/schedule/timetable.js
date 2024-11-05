$(document).ready(function() {
    const groupId = $('#groupId').val();

    $('#adjust-calendar-btn').click(function() {
        window.location.href = '/groups/' + groupId + '/schedules'; // 새 URL로 이동
    });

    $('#user-table-btn').click(function () {
        window.location.href = '/groups/' + groupId + '/schedules/adjust';
    });

    $('#submit-chart-btn').click(function () {
        addChart();
    })

    $('input[name="selectedUser"]').change(function() {
        selectUser();
    });

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
});

// 시간표에 추가하는 함수
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
            window.location.href = `/groups/${groupId}/timetables/view`;
        } else {
            console.error('Failed to submit time table');
        }
    });
}


// 사용자 선택 처리 함수
function selectUser() {
    const groupId = $('#groupId').val();
    const selectedUserId = document.querySelector('input[name="selectedUser"]:checked').value;
    console.log(selectedUserId);
    fetchUserSchedule(groupId, selectedUserId);
}

// 사용자 일정 가져오기
function fetchUserSchedule(groupId, userId) {
    const url = `/groups/${groupId}/timetables?userId=${userId !== 'all' ? userId : ''}`;

    fetch(url)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then((availableTimes) => {
            renderTable(availableTimes);
        })
        .catch(error => console.error('Error fetching user schedule:', error));
}

// 일정 렌더링
function renderTable(availableTimes) {
    const timeTableBody = document.querySelector('.time-table tbody');
    const numOfGroupMember = document.querySelector('#numOfGroupMember').value;
    timeTableBody.innerHTML = '';

    for (let hour = 0; hour < 24; hour++) {
        const row = document.createElement('tr');
        const timeCell = document.createElement('th');
        timeCell.textContent = `${hour}~${hour + 1}`;
        row.appendChild(timeCell);

        for (let day = 0; day < 7; day++) {
            const dayCell = document.createElement('td');
            dayCell.classList.add('time-cell');
            if (availableTimes[hour][day]) {
                dayCell.classList.add('highlighted');
                dayCell.style.opacity = availableTimes[hour][day] / numOfGroupMember;
            }
            row.appendChild(dayCell);
        }
        timeTableBody.appendChild(row); // 새 행을 테이블에 추가
    }
}
