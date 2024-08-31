document.addEventListener("DOMContentLoaded", function() {
    var createTaskBtn = document.getElementById("createTaskBtn");
    var createModal = document.getElementById("createModal");
    var closeButtons = document.querySelectorAll(".close-btn");

    createTaskBtn.onclick = function() {
        createModal.style.display = "block";
    }

    closeButtons.forEach(function(btn) {
        btn.onclick = function() {
            btn.parentElement.parentElement.style.display = "none";
        }
    });

    window.onclick = function(event) {
        if (event.target == createModal) {
            createModal.style.display = "none";
        }
    }

    // 과제 생성 폼 제출
    var createModalForm = document.getElementById("createModalForm");

    createModalForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼 제출 막음

        // 시작 기한과 마감 기한
        var startTime = new Date(document.getElementById("startTime").value);
        var endTime = new Date(document.getElementById("endTime").value);

        // 날짜 유효성 검사
        if (startTime >= endTime) {
            alert("마감 기한은 시작 기한보다 앞서야 합니다.");
            event.preventDefault(); // 폼 제출을 막음
            return;
        }

        // 유효성 검사를 통과한 경우 폼을 제출
        var formData = new FormData(createModalForm);

        fetch(createModalForm.action, {
            method: 'POST',
            body: formData // FormData를 직접 전송
        })
            .then(response => {
                if (response.ok) {
                    return response.json(); // JSON 응답을 반환
                } else {
                    throw new Error("알 수 없는 응답 상태 코드: " + response.status);
                }
            })
            .then(data => {
                alert("성공적으로 과제를 생성했습니다.");
                window.location.reload(); // 화면 리로드
            })
            .catch(error => {
                alert("문제가 발생했습니다: " + error.message);
            });
    });

    // 현재가 과제 시작기한보다 앞서면 링크 비활성화
    var taskLinks = document.querySelectorAll('.task-item');

    taskLinks.forEach(function (link) {
        var startTime = new Date(link.getAttribute('data-start-time'));
        var currentTime = new Date();

        if (startTime > currentTime) {
            link.style.pointerEvents = 'none';
            link.style.color = 'gray';
        }
    });
});