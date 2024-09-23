document.addEventListener("DOMContentLoaded", function() {
    const createTaskBtn = document.getElementById("createTaskBtn");
    const createModal = document.getElementById("createModal");
    const closeButtons = document.querySelectorAll(".close-btn");

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
    const createModalForm = document.getElementById("createModalForm");

    createModalForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 폼 제출 막음

        // 시작 기한과 마감 기한
        var startTime = new Date(document.getElementById("startTime").value);
        var endTime = new Date(document.getElementById("endTime").value);

        // 날짜 유효성 검사
        if (startTime > endTime) {
            alert("마감 기한은 시작 기한보다 앞서야 합니다.");
            event.preventDefault(); // 폼 제출을 막음
            return;
        }

        // 유효성 검사를 통과한 경우 폼을 제출
        const formData = new FormData(createModalForm);

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
    const taskLinks = document.querySelectorAll('.task-link');

    taskLinks.forEach(function (link) {
        const startTime = new Date(link.getAttribute('data-start-time'));
        const currentTime = new Date();

        if (startTime > currentTime) {
            link.style.pointerEvents = 'none';
            link.style.color = '#ccc';
        }
    });

    // 과제 진행률 계산 후 전송
    const timelines = document.querySelectorAll('.task-timeline');

    timelines.forEach(function(timeline) {
        const startTime = new Date(timeline.getAttribute('data-start-time'));
        const endTime = new Date(timeline.getAttribute('data-end-time'));
        const currentTime = new Date();

        // 전체 기간
        const totalDuration = endTime - startTime;
        // 현재 날짜까지
        const elapsedDuration = currentTime - startTime;

        // 진행률 계산
        let progressPercent = (elapsedDuration / totalDuration) * 100;
        progressPercent = Math.max(0, Math.min(100, progressPercent));
        const progressBar = timeline.querySelector('.progress-bar');
        progressBar.style.width = progressPercent + '%';
    });
});